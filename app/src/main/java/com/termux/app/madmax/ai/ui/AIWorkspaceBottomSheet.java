package com.termux.app.madmax.ai.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.termux.R;
import com.termux.app.madmax.ai.core.AIWorkspaceManager;
import com.termux.app.madmax.ai.engine.AIProvider;
import com.termux.app.madmax.ai.model.AICommandExplanation;
import com.termux.app.madmax.ai.model.AICommandGeneration;
import com.termux.app.madmax.ai.model.AIErrorDiagnosis;
import com.termux.app.madmax.ai.model.AIHistoryItem;
import com.termux.app.madmax.ai.model.GitHubRepoItem;
import com.termux.app.madmax.ai.services.AICommandService;
import com.termux.app.madmax.ai.services.AIHistoryManager;
import com.termux.app.madmax.ai.services.GitHubWorkspaceManager;

import java.util.List;
import java.util.Map;

/**
 * Material 3 AI Command Intelligence Workspace bottom sheet.
 */
public class AIWorkspaceBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "AIWorkspaceBottomSheet";

    public interface CommandActionCallback {
        void onInsertCommand(@NonNull String command);
        void onExecuteCommand(@NonNull String command);
        @Nullable String onCaptureTerminalOutput();
    }

    private CommandActionCallback mCallback;
    private AICommandService mCommandService;
    private AIHistoryManager mHistoryManager;
    private GitHubWorkspaceManager mGitHubManager;

    private FrameLayout mTabContainer;
    private TabLayout mTabLayout;
    private int mCurrentTab = 0;

    public static AIWorkspaceBottomSheet newInstance() {
        return new AIWorkspaceBottomSheet();
    }

    public void setCommandActionCallback(@Nullable CommandActionCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = requireContext();
        AIWorkspaceManager manager = AIWorkspaceManager.getInstance(context);
        mCommandService = manager.getCommandService();
        mHistoryManager = manager.getHistoryManager();
        mGitHubManager = manager.getGitHubWorkspaceManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_ai_workspace, container, false);

        mTabContainer = root.findViewById(R.id.ai_tab_container);
        mTabLayout = root.findViewById(R.id.ai_tab_layout);

        ImageButton closeBtn = root.findViewById(R.id.btn_close_ai_sheet);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> dismiss());
        }

        setupTabs();
        return root;
    }

    private void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setText("Explain").setIcon(R.drawable.ic_explain));
        mTabLayout.addTab(mTabLayout.newTab().setText("Generate").setIcon(R.drawable.ic_generate));
        mTabLayout.addTab(mTabLayout.newTab().setText("Diagnose").setIcon(R.drawable.ic_diagnose));
        mTabLayout.addTab(mTabLayout.newTab().setText("History").setIcon(R.drawable.ic_history));
        mTabLayout.addTab(mTabLayout.newTab().setText("GitHub").setIcon(R.drawable.ic_github));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentTab = tab.getPosition();
                renderCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        renderCurrentTab();
    }

    private void renderCurrentTab() {
        mTabContainer.removeAllViews();
        switch (mCurrentTab) {
            case 0:
                renderExplainTab();
                break;
            case 1:
                renderGenerateTab();
                break;
            case 2:
                renderDiagnoseTab();
                break;
            case 3:
                renderHistoryTab();
                break;
            case 4:
                renderGitHubTab();
                break;
        }
    }

    // --- TAB 1: EXPLAIN ---
    private void renderExplainTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        TextView label = createSectionHeader(context, "Command Inspector & Flag Decomposer");
        layout.addView(label);

        EditText input = createMonospaceInputField(context, "Enter shell command to explain (e.g. tar -czvf archive.tar.gz /data)");
        layout.addView(input);

        ProgressBar progress = createProgressBar(context);
        layout.addView(progress);

        MaterialButton btnExplain = createPrimaryButton(context, "Analyze Command", R.drawable.ic_explain);
        layout.addView(btnExplain);

        LinearLayout resultContainer = createBaseVerticalLayout(context);
        layout.addView(resultContainer);

        btnExplain.setOnClickListener(v -> {
            String cmd = input.getText().toString().trim();
            if (cmd.isEmpty()) {
                Toast.makeText(context, "Please enter a command to analyze.", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE);
            resultContainer.removeAllViews();

            mCommandService.explainCommand(cmd, new AIProvider.Callback<AICommandExplanation>() {
                @Override
                public void onSuccess(@NonNull AICommandExplanation result) {
                    progress.setVisibility(View.GONE);
                    displayExplanationResult(resultContainer, result);
                }

                @Override
                public void onError(@NonNull Throwable error) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, "Analysis failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        mTabContainer.addView(layout);
    }

    private void displayExplanationResult(LinearLayout container, AICommandExplanation explanation) {
        Context context = requireContext();

        // Risk badge + Binary Header
        MaterialCardView card = createResultCard(context);
        LinearLayout inner = createBaseVerticalLayout(context);
        inner.setPadding(24, 24, 24, 24);

        TextView riskBadge = new TextView(context);
        riskBadge.setText("Risk: " + explanation.getRiskLevel().getLabel().toUpperCase());
        riskBadge.setTextColor(explanation.getRiskLevel().getColor());
        riskBadge.setTextSize(12);
        riskBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        inner.addView(riskBadge);

        if (explanation.getSafetyWarning() != null) {
            TextView warning = new TextView(context);
            warning.setText("⚠️ " + explanation.getSafetyWarning());
            warning.setTextColor(0xFFFF3B30);
            warning.setTextSize(12);
            warning.setPadding(0, 8, 0, 8);
            inner.addView(warning);
        }

        TextView summary = new TextView(context);
        summary.setText(explanation.getSummary());
        summary.setTextSize(14);
        summary.setPadding(0, 12, 0, 12);
        inner.addView(summary);

        if (!explanation.getFlagsBreakdown().isEmpty()) {
            TextView flagsTitle = new TextView(context);
            flagsTitle.setText("Detected Flags & Arguments:");
            flagsTitle.setTextSize(12);
            flagsTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            flagsTitle.setPadding(0, 8, 0, 4);
            inner.addView(flagsTitle);

            for (Map.Entry<String, String> entry : explanation.getFlagsBreakdown().entrySet()) {
                TextView flagItem = new TextView(context);
                flagItem.setText("• " + entry.getKey() + " → " + entry.getValue());
                flagItem.setTextSize(12);
                flagItem.setPadding(8, 2, 0, 2);
                inner.addView(flagItem);
            }
        }

        // Action buttons
        LinearLayout actions = createHorizontalActions(context, explanation.getRawCommand());
        inner.addView(actions);

        card.addView(inner);
        container.addView(card);
    }

    // --- TAB 2: GENERATE ---
    private void renderGenerateTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        TextView label = createSectionHeader(context, "Natural Language to Shell Translator");
        layout.addView(label);

        EditText input = createInputField(context, "Describe what you want to do in plain English...");
        layout.addView(input);

        // Quick Suggestion Chips
        TextView chipLabel = new TextView(context);
        chipLabel.setText("Quick prompts:");
        chipLabel.setTextSize(12);
        chipLabel.setPadding(0, 8, 0, 4);
        layout.addView(chipLabel);

        ChipGroup chipGroup = new ChipGroup(context);
        chipGroup.setSingleLine(false);
        addChip(chipGroup, "Find files > 100MB", input);
        addChip(chipGroup, "Kill port 8080", input);
        addChip(chipGroup, "Extract tar.gz", input);
        addChip(chipGroup, "Reverse SSH tunnel", input);
        addChip(chipGroup, "Docker system prune", input);
        addChip(chipGroup, "Disk space usage", input);
        layout.addView(chipGroup);

        ProgressBar progress = createProgressBar(context);
        layout.addView(progress);

        MaterialButton btnGenerate = createPrimaryButton(context, "Generate Command", R.drawable.ic_generate);
        layout.addView(btnGenerate);

        LinearLayout resultContainer = createBaseVerticalLayout(context);
        layout.addView(resultContainer);

        btnGenerate.setOnClickListener(v -> {
            String prompt = input.getText().toString().trim();
            if (prompt.isEmpty()) {
                Toast.makeText(context, "Please enter a prompt.", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE);
            resultContainer.removeAllViews();

            mCommandService.generateCommand(prompt, new AIProvider.Callback<AICommandGeneration>() {
                @Override
                public void onSuccess(@NonNull AICommandGeneration result) {
                    progress.setVisibility(View.GONE);
                    displayGenerationResult(resultContainer, result);
                }

                @Override
                public void onError(@NonNull Throwable error) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, "Generation failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        mTabContainer.addView(layout);
    }

    private void addChip(ChipGroup group, String text, EditText targetInput) {
        Chip chip = new Chip(group.getContext());
        chip.setText(text);
        chip.setTextSize(11);
        chip.setOnClickListener(v -> {
            targetInput.setText(text);
            targetInput.setSelection(text.length());
        });
        group.addView(chip);
    }

    private void displayGenerationResult(LinearLayout container, AICommandGeneration gen) {
        Context context = requireContext();
        MaterialCardView card = createResultCard(context);
        LinearLayout inner = createBaseVerticalLayout(context);
        inner.setPadding(24, 24, 24, 24);

        TextView cmdBox = new TextView(context);
        cmdBox.setText(gen.getGeneratedCommand());
        cmdBox.setTextSize(14);
        cmdBox.setTypeface(android.graphics.Typeface.MONOSPACE);
        cmdBox.setTextColor(0xFF007AFF);
        cmdBox.setBackgroundColor(0x15007AFF);
        cmdBox.setPadding(16, 16, 16, 16);
        inner.addView(cmdBox);

        TextView explanation = new TextView(context);
        explanation.setText(gen.getExplanation());
        explanation.setTextSize(13);
        explanation.setPadding(0, 12, 0, 12);
        inner.addView(explanation);

        LinearLayout actions = createHorizontalActions(context, gen.getGeneratedCommand());
        inner.addView(actions);

        card.addView(inner);
        container.addView(card);
    }

    // --- TAB 3: DIAGNOSE ---
    private void renderDiagnoseTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        TextView label = createSectionHeader(context, "Terminal Error Analyzer & Remediation");
        layout.addView(label);

        EditText input = createMonospaceInputField(context, "Paste terminal stderr or error message here...");
        layout.addView(input);

        // Auto capture button
        MaterialButton btnCapture = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnCapture.setText("Capture Error from Active Terminal");
        btnCapture.setIconResource(R.drawable.ic_terminal);
        btnCapture.setOnClickListener(v -> {
            if (mCallback != null) {
                String captured = mCallback.onCaptureTerminalOutput();
                if (captured != null && !captured.trim().isEmpty()) {
                    input.setText(captured.trim());
                    Toast.makeText(context, "Captured terminal output.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No terminal output captured.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        layout.addView(btnCapture);

        ProgressBar progress = createProgressBar(context);
        layout.addView(progress);

        MaterialButton btnDiagnose = createPrimaryButton(context, "Diagnose Error", R.drawable.ic_diagnose);
        layout.addView(btnDiagnose);

        LinearLayout resultContainer = createBaseVerticalLayout(context);
        layout.addView(resultContainer);

        btnDiagnose.setOnClickListener(v -> {
            String err = input.getText().toString().trim();
            if (err.isEmpty()) {
                Toast.makeText(context, "Please enter or capture an error message.", Toast.LENGTH_SHORT).show();
                return;
            }

            progress.setVisibility(View.VISIBLE);
            resultContainer.removeAllViews();

            mCommandService.diagnoseError(err, new AIProvider.Callback<AIErrorDiagnosis>() {
                @Override
                public void onSuccess(@NonNull AIErrorDiagnosis result) {
                    progress.setVisibility(View.GONE);
                    displayDiagnosisResult(resultContainer, result);
                }

                @Override
                public void onError(@NonNull Throwable error) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, "Diagnosis failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        mTabContainer.addView(layout);
    }

    private void displayDiagnosisResult(LinearLayout container, AIErrorDiagnosis diag) {
        Context context = requireContext();
        MaterialCardView card = createResultCard(context);
        LinearLayout inner = createBaseVerticalLayout(context);
        inner.setPadding(24, 24, 24, 24);

        TextView cat = new TextView(context);
        cat.setText("Category: " + diag.getCategory().getDisplayName());
        cat.setTextSize(12);
        cat.setTypeface(null, android.graphics.Typeface.BOLD);
        cat.setTextColor(0xFFFF9500);
        inner.addView(cat);

        TextView cause = new TextView(context);
        cause.setText("Root Cause: " + diag.getRootCause());
        cause.setTextSize(13);
        cause.setPadding(0, 8, 0, 8);
        inner.addView(cause);

        TextView remedy = new TextView(context);
        remedy.setText("Remedy: " + diag.getRemedyExplanation());
        remedy.setTextSize(13);
        remedy.setPadding(0, 0, 0, 8);
        inner.addView(remedy);

        if (!diag.getSuggestedFixCommand().isEmpty()) {
            TextView fixLabel = new TextView(context);
            fixLabel.setText("Recommended Fix Command:");
            fixLabel.setTextSize(12);
            fixLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            inner.addView(fixLabel);

            TextView fixCmd = new TextView(context);
            fixCmd.setText(diag.getSuggestedFixCommand());
            fixCmd.setTypeface(android.graphics.Typeface.MONOSPACE);
            fixCmd.setTextSize(13);
            fixCmd.setTextColor(0xFF34C759);
            fixCmd.setBackgroundColor(0x1534C759);
            fixCmd.setPadding(12, 12, 12, 12);
            inner.addView(fixCmd);

            LinearLayout actions = createHorizontalActions(context, diag.getSuggestedFixCommand());
            inner.addView(actions);
        }

        card.addView(inner);
        container.addView(card);
    }

    // --- TAB 4: HISTORY ---
    private void renderHistoryTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView label = createSectionHeader(context, "Command Intelligence History");
        label.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        headerRow.addView(label);

        MaterialButton btnClear = new MaterialButton(context, null, com.google.android.material.R.attr.borderlessButtonStyle);
        btnClear.setText("Clear");
        btnClear.setTextSize(12);
        btnClear.setOnClickListener(v -> {
            mHistoryManager.clearHistory();
            renderHistoryTab();
        });
        headerRow.addView(btnClear);
        layout.addView(headerRow);

        List<AIHistoryItem> history = mHistoryManager.getHistory();
        if (history.isEmpty()) {
            TextView empty = new TextView(context);
            empty.setText("No history items yet. Commands explained or generated will appear here.");
            empty.setTextSize(13);
            empty.setTextColor(0xFF8E8E93);
            empty.setPadding(0, 24, 0, 24);
            empty.setGravity(android.view.Gravity.CENTER);
            layout.addView(empty);
        } else {
            for (AIHistoryItem item : history) {
                MaterialCardView itemCard = createResultCard(context);
                LinearLayout inner = createBaseVerticalLayout(context);
                inner.setPadding(16, 16, 16, 16);

                TextView typeBadge = new TextView(context);
                typeBadge.setText(item.getType().getLabel().toUpperCase());
                typeBadge.setTextColor(item.getType().getColor());
                typeBadge.setTextSize(10);
                typeBadge.setTypeface(null, android.graphics.Typeface.BOLD);
                inner.addView(typeBadge);

                TextView query = new TextView(context);
                query.setText(item.getQuery());
                query.setTextSize(13);
                query.setTypeface(null, android.graphics.Typeface.BOLD);
                query.setPadding(0, 4, 0, 4);
                inner.addView(query);

                if (!item.getResultCommand().isEmpty()) {
                    TextView res = new TextView(context);
                    res.setText(item.getResultCommand());
                    res.setTypeface(android.graphics.Typeface.MONOSPACE);
                    res.setTextSize(12);
                    res.setTextColor(0xFF007AFF);
                    inner.addView(res);

                    LinearLayout actions = createHorizontalActions(context, item.getResultCommand());
                    inner.addView(actions);
                }

                itemCard.addView(inner);
                layout.addView(itemCard);
            }
        }

        mTabContainer.addView(layout);
    }

    // --- TAB 5: GITHUB WORKSPACE ---
    private void renderGitHubTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        TextView label = createSectionHeader(context, "GitHub Workspace Dashboard");
        layout.addView(label);

        // Curated Repositories
        List<GitHubRepoItem> repos = mGitHubManager.getPopularRepositories();
        for (GitHubRepoItem repo : repos) {
            MaterialCardView card = createResultCard(context);
            LinearLayout inner = createBaseVerticalLayout(context);
            inner.setPadding(20, 20, 20, 20);

            TextView repoName = new TextView(context);
            repoName.setText("📦 " + repo.getFullName());
            repoName.setTextSize(15);
            repoName.setTypeface(null, android.graphics.Typeface.BOLD);
            inner.addView(repoName);

            TextView desc = new TextView(context);
            desc.setText(repo.getDescription());
            desc.setTextSize(12);
            desc.setPadding(0, 4, 0, 8);
            inner.addView(desc);

            LinearLayout actionRow = new LinearLayout(context);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            actionRow.setGravity(android.view.Gravity.END);

            MaterialButton btnCodespace = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btnCodespace.setText("Codespace");
            btnCodespace.setTextSize(11);
            btnCodespace.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mGitHubManager.generateCodespaceLaunchUrl(repo.getFullName())));
                startActivity(browserIntent);
            });
            actionRow.addView(btnCodespace);

            MaterialButton btnClone = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btnClone.setText("Clone in Termux");
            btnClone.setTextSize(11);
            btnClone.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ((LinearLayout.LayoutParams) btnClone.getLayoutParams()).setMarginStart(16);
            btnClone.setOnClickListener(v -> {
                String cloneCmd = mGitHubManager.generateCloneCommand(repo.getCloneUrl());
                if (mCallback != null) {
                    mCallback.onExecuteCommand(cloneCmd);
                    dismiss();
                }
            });
            actionRow.addView(btnClone);

            inner.addView(actionRow);
            card.addView(inner);
            layout.addView(card);
        }

        mTabContainer.addView(layout);
    }

    // --- UI Helper Methods ---
    private LinearLayout createBaseVerticalLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    private TextView createSectionHeader(Context context, String title) {
        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setTextSize(14);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setPadding(0, 0, 0, 12);
        return tv;
    }

    private EditText createInputField(Context context, String hint) {
        EditText et = new EditText(context);
        et.setHint(hint);
        et.setTextSize(14);
        et.setBackgroundResource(R.drawable.session_background_selected);
        et.setPadding(20, 20, 20, 20);
        return et;
    }

    private EditText createMonospaceInputField(Context context, String hint) {
        EditText et = createInputField(context, hint);
        et.setTypeface(android.graphics.Typeface.MONOSPACE);
        return et;
    }

    private ProgressBar createProgressBar(Context context) {
        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        pb.setIndeterminate(true);
        pb.setVisibility(View.GONE);
        pb.setPadding(0, 12, 0, 12);
        return pb;
    }

    private MaterialButton createPrimaryButton(Context context, String text, int iconRes) {
        MaterialButton btn = new MaterialButton(context);
        btn.setText(text);
        btn.setIconResource(iconRes);
        btn.setIconSize(36);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 16;
        btn.setLayoutParams(lp);
        return btn;
    }

    private MaterialCardView createResultCard(Context context) {
        MaterialCardView card = new MaterialCardView(context);
        card.setRadius(24);
        card.setCardElevation(0);
        card.setStrokeWidth(2);
        card.setStrokeColor(0x338E8E93);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = 16;
        card.setLayoutParams(lp);
        return card;
    }

    private LinearLayout createHorizontalActions(Context context, String command) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.END);
        row.setPadding(0, 8, 0, 0);

        MaterialButton btnCopy = new MaterialButton(context, null, com.google.android.material.R.attr.borderlessButtonStyle);
        btnCopy.setText("Copy");
        btnCopy.setIconResource(R.drawable.ic_copy);
        btnCopy.setIconSize(32);
        btnCopy.setTextSize(12);
        btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("MadMax Command", command));
                Toast.makeText(context, "Command copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
        row.addView(btnCopy);

        MaterialButton btnInsert = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnInsert.setText("Insert");
        btnInsert.setIconResource(R.drawable.ic_insert);
        btnInsert.setIconSize(32);
        btnInsert.setTextSize(12);
        btnInsert.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) btnInsert.getLayoutParams()).setMarginStart(12);
        btnInsert.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onInsertCommand(command);
                dismiss();
            }
        });
        row.addView(btnInsert);

        MaterialButton btnRun = new MaterialButton(context);
        btnRun.setText("Run");
        btnRun.setIconResource(R.drawable.ic_play);
        btnRun.setIconSize(32);
        btnRun.setTextSize(12);
        btnRun.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) btnRun.getLayoutParams()).setMarginStart(12);
        btnRun.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onExecuteCommand(command);
                dismiss();
            }
        });
        row.addView(btnRun);

        return row;
    }
}
