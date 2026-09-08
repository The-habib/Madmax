package com.termux.app.madmax.ai.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
 * Executive Material 3 AI Command Intelligence Workspace bottom sheet.
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
        if (mTabContainer == null) return;
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

    // =========================================================================
    // TAB 1: EXPLAIN
    // =========================================================================
    private void renderExplainTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Command Inspector & Flag Decomposer"));
        layout.addView(createSectionSubtext(context, "Deconstruct Unix syntax, verify security risk, and understand each flag"));

        EditText input = createMonospaceInputField(context, "Enter shell command to explain (e.g. tar -czvf archive.tar.gz /data)");
        layout.addView(input);

        ProgressBar progress = createProgressBar(context);
        layout.addView(progress);

        MaterialButton btnExplain = createPrimaryButton(context, "Analyze Command", R.drawable.ic_explain, 0xFF8E24AA);
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

        MaterialCardView card = createResultCard(context);
        LinearLayout inner = createCardInnerLayout(context);

        // Header row: Command & Risk Badge
        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView cmdTitle = new TextView(context);
        cmdTitle.setText(explanation.getBinaryName());
        cmdTitle.setTextSize(14);
        cmdTitle.setTypeface(null, Typeface.BOLD);
        cmdTitle.setTextColor(0xFFF2F2F5);
        cmdTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        headerRow.addView(cmdTitle);

        // Risk Pill Badge
        TextView riskBadge = new TextView(context);
        String riskLabel = explanation.getRiskLevel().getLabel().toUpperCase();
        riskBadge.setText("RISK: " + riskLabel);
        riskBadge.setTextSize(9);
        riskBadge.setTypeface(null, Typeface.BOLD);
        int riskColor = explanation.getRiskLevel().getColor();
        riskBadge.setTextColor(riskColor);
        int padH = dpToPx(context, 8);
        int padV = dpToPx(context, 3);
        riskBadge.setPadding(padH, padV, padH, padV);
        riskBadge.setBackground(createRoundedBackground(context, (riskColor & 0x00FFFFFF) | 0x20000000, (riskColor & 0x00FFFFFF) | 0x40000000, 6));
        headerRow.addView(riskBadge);
        inner.addView(headerRow);

        // Raw Command Box
        HorizontalScrollView cmdBoxScroll = createTerminalCodeBox(context, explanation.getRawCommand());
        inner.addView(cmdBoxScroll);

        // Safety Warning Box (if any)
        if (explanation.getSafetyWarning() != null && !explanation.getSafetyWarning().isEmpty()) {
            LinearLayout warnBox = new LinearLayout(context);
            warnBox.setOrientation(LinearLayout.HORIZONTAL);
            warnBox.setBackground(createRoundedBackground(context, 0x25FF3B30, 0x50FF3B30, 8));
            int warnPad = dpToPx(context, 10);
            warnBox.setPadding(warnPad, warnPad, warnPad, warnPad);
            LinearLayout.LayoutParams warnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            warnLp.topMargin = dpToPx(context, 10);
            warnBox.setLayoutParams(warnLp);

            TextView warnIcon = new TextView(context);
            warnIcon.setText("!");
            warnIcon.setTypeface(null, Typeface.BOLD);
            warnIcon.setTextColor(0xFFFF3B30);
            warnIcon.setTextSize(14);
            warnBox.addView(warnIcon);

            TextView warnText = new TextView(context);
            warnText.setText(explanation.getSafetyWarning());
            warnText.setTextColor(0xFFFF8A80);
            warnText.setTextSize(11);
            warnText.setPadding(dpToPx(context, 8), 0, 0, 0);
            warnBox.addView(warnText);

            inner.addView(warnBox);
        }

        // Summary Description
        TextView summary = new TextView(context);
        summary.setText(explanation.getSummary());
        summary.setTextSize(12);
        summary.setTextColor(0xFFE0E0E6);
        summary.setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 8));
        inner.addView(summary);

        // Flags Breakdown Table
        if (!explanation.getFlagsBreakdown().isEmpty()) {
            TextView flagsTitle = new TextView(context);
            flagsTitle.setText("DETECTED FLAGS & PARAMETERS");
            flagsTitle.setTextSize(10);
            flagsTitle.setTypeface(null, Typeface.BOLD);
            flagsTitle.setTextColor(0xFF8E8E93);
            flagsTitle.setLetterSpacing(0.06f);
            flagsTitle.setPadding(0, dpToPx(context, 6), 0, dpToPx(context, 6));
            inner.addView(flagsTitle);

            for (Map.Entry<String, String> entry : explanation.getFlagsBreakdown().entrySet()) {
                LinearLayout flagRow = new LinearLayout(context);
                flagRow.setOrientation(LinearLayout.HORIZONTAL);
                flagRow.setGravity(Gravity.CENTER_VERTICAL);
                flagRow.setPadding(0, dpToPx(context, 3), 0, dpToPx(context, 3));

                TextView flagBadge = new TextView(context);
                flagBadge.setText(entry.getKey());
                flagBadge.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                flagBadge.setTextSize(11);
                flagBadge.setTextColor(0xFF80DEEA);
                flagBadge.setBackground(createRoundedBackground(context, 0xFF14141A, 0xFF282834, 4));
                int fPadH = dpToPx(context, 6);
                int fPadV = dpToPx(context, 2);
                flagBadge.setPadding(fPadH, fPadV, fPadH, fPadV);
                flagRow.addView(flagBadge);

                TextView flagDesc = new TextView(context);
                flagDesc.setText(entry.getValue());
                flagDesc.setTextSize(11);
                flagDesc.setTextColor(0xFFB0B0B8);
                flagDesc.setPadding(dpToPx(context, 8), 0, 0, 0);
                flagRow.addView(flagDesc);

                inner.addView(flagRow);
            }
        }

        // Action buttons
        LinearLayout actions = createHorizontalActions(context, explanation.getRawCommand());
        inner.addView(actions);

        card.addView(inner);
        container.addView(card);
    }

    // =========================================================================
    // TAB 2: GENERATE
    // =========================================================================
    private void renderGenerateTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Natural Language to Shell Translator"));
        layout.addView(createSectionSubtext(context, "Describe desired terminal tasks in plain English to generate correct syntax"));

        EditText input = createInputField(context, "Describe what you want to do (e.g. Find all files over 100MB and sort by size)");
        layout.addView(input);

        // Quick Suggestion Chips
        TextView chipLabel = new TextView(context);
        chipLabel.setText("QUICK PROMPTS");
        chipLabel.setTextSize(10);
        chipLabel.setTypeface(null, Typeface.BOLD);
        chipLabel.setTextColor(0xFF8E8E93);
        chipLabel.setLetterSpacing(0.06f);
        chipLabel.setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 6));
        layout.addView(chipLabel);

        HorizontalScrollView chipScroll = new HorizontalScrollView(context);
        chipScroll.setHorizontalScrollBarEnabled(false);
        chipScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ChipGroup chipGroup = new ChipGroup(context);
        chipGroup.setSingleLine(true);
        addChip(chipGroup, "Find files > 100MB", input);
        addChip(chipGroup, "Kill port 8080", input);
        addChip(chipGroup, "Extract tar.gz", input);
        addChip(chipGroup, "Reverse SSH tunnel", input);
        addChip(chipGroup, "Docker system prune", input);
        addChip(chipGroup, "Check disk space", input);
        chipScroll.addView(chipGroup);
        layout.addView(chipScroll);

        ProgressBar progress = createProgressBar(context);
        layout.addView(progress);

        MaterialButton btnGenerate = createPrimaryButton(context, "Generate Command", R.drawable.ic_generate, 0xFFFF3B30);
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
        Context context = group.getContext();
        Chip chip = new Chip(context);
        chip.setText(text);
        chip.setTextSize(11);
        chip.setChipCornerRadius(dpToPx(context, 14));
        chip.setChipBackgroundColor(ColorStateList.valueOf(0xFF202028));
        chip.setChipStrokeColor(ColorStateList.valueOf(0xFF33333E));
        chip.setChipStrokeWidth(dpToPx(context, 1));
        chip.setTextColor(0xFFE0E0E6);
        chip.setOnClickListener(v -> {
            targetInput.setText(text);
            targetInput.setSelection(text.length());
        });
        group.addView(chip);
    }

    private void displayGenerationResult(LinearLayout container, AICommandGeneration gen) {
        Context context = requireContext();
        MaterialCardView card = createResultCard(context);
        LinearLayout inner = createCardInnerLayout(context);

        TextView label = new TextView(context);
        label.setText("GENERATED SHELL COMMAND");
        label.setTextSize(10);
        label.setTypeface(null, Typeface.BOLD);
        label.setTextColor(0xFF34C759);
        label.setLetterSpacing(0.06f);
        inner.addView(label);

        HorizontalScrollView cmdBox = createTerminalCodeBox(context, gen.getGeneratedCommand());
        inner.addView(cmdBox);

        TextView explanation = new TextView(context);
        explanation.setText(gen.getExplanation());
        explanation.setTextSize(12);
        explanation.setTextColor(0xFFE0E0E6);
        explanation.setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 8));
        inner.addView(explanation);

        LinearLayout actions = createHorizontalActions(context, gen.getGeneratedCommand());
        inner.addView(actions);

        card.addView(inner);
        container.addView(card);
    }

    // =========================================================================
    // TAB 3: DIAGNOSE
    // =========================================================================
    private void renderDiagnoseTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Terminal Error Analyzer & Remediation"));
        layout.addView(createSectionSubtext(context, "Diagnose stderr messages, command failures, and automated recovery paths"));

        EditText input = createMonospaceInputField(context, "Paste terminal stderr or error message here...");
        layout.addView(input);

        // Auto capture button
        MaterialButton btnCapture = new MaterialButton(context);
        btnCapture.setText("Capture Active Terminal Screen");
        btnCapture.setIconResource(R.drawable.ic_terminal);
        btnCapture.setIconSize(dpToPx(context, 16));
        btnCapture.setIconTint(ColorStateList.valueOf(0xFF80DEEA));
        btnCapture.setTextColor(0xFF80DEEA);
        btnCapture.setBackgroundTintList(ColorStateList.valueOf(0xFF1E2628));
        btnCapture.setStrokeColor(ColorStateList.valueOf(0xFF2C3E42));
        btnCapture.setStrokeWidth(dpToPx(context, 1));
        btnCapture.setCornerRadius(dpToPx(context, 8));
        btnCapture.setTextSize(12);
        LinearLayout.LayoutParams capLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        capLp.topMargin = dpToPx(context, 8);
        btnCapture.setLayoutParams(capLp);
        btnCapture.setOnClickListener(v -> {
            if (mCallback != null) {
                String captured = mCallback.onCaptureTerminalOutput();
                if (captured != null && !captured.trim().isEmpty()) {
                    input.setText(captured.trim());
                    Toast.makeText(context, "Captured active terminal output.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Terminal output is currently empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        layout.addView(btnCapture);

        ProgressBar progress = createProgressBar(context);
        layout.addView(progress);

        MaterialButton btnDiagnose = createPrimaryButton(context, "Diagnose Error", R.drawable.ic_diagnose, 0xFF00ACC1);
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
        LinearLayout inner = createCardInnerLayout(context);

        // Status pill
        TextView cat = new TextView(context);
        cat.setText("DETECTED: " + diag.getCategory().getDisplayName().toUpperCase());
        cat.setTextSize(10);
        cat.setTypeface(null, Typeface.BOLD);
        cat.setTextColor(0xFFFF9500);
        cat.setBackground(createRoundedBackground(context, 0x22FF9500, 0x44FF9500, 6));
        int padH = dpToPx(context, 8);
        int padV = dpToPx(context, 3);
        cat.setPadding(padH, padV, padH, padV);
        inner.addView(cat);

        TextView cause = new TextView(context);
        cause.setText("Root Cause: " + diag.getRootCause());
        cause.setTextSize(12);
        cause.setTypeface(null, Typeface.BOLD);
        cause.setTextColor(0xFFF2F2F5);
        cause.setPadding(0, dpToPx(context, 8), 0, dpToPx(context, 4));
        inner.addView(cause);

        TextView remedy = new TextView(context);
        remedy.setText("Remedy: " + diag.getRemedyExplanation());
        remedy.setTextSize(12);
        remedy.setTextColor(0xFFB0B0B8);
        remedy.setPadding(0, 0, 0, dpToPx(context, 8));
        inner.addView(remedy);

        if (!diag.getSuggestedFixCommand().isEmpty()) {
            HorizontalScrollView fixScroll = createTerminalCodeBox(context, diag.getSuggestedFixCommand());
            inner.addView(fixScroll);

            LinearLayout actions = createHorizontalActions(context, diag.getSuggestedFixCommand());
            inner.addView(actions);
        }

        card.addView(inner);
        container.addView(card);
    }

    // =========================================================================
    // TAB 4: HISTORY
    // =========================================================================
    private void renderHistoryTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView label = createSectionHeader(context, "Command Intelligence History");
        label.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        headerRow.addView(label);

        MaterialButton btnClear = new MaterialButton(context, null, com.google.android.material.R.attr.borderlessButtonStyle);
        btnClear.setText("Clear");
        btnClear.setTextSize(11);
        btnClear.setTextColor(0xFF8E8E93);
        btnClear.setOnClickListener(v -> {
            mHistoryManager.clearHistory();
            renderHistoryTab();
        });
        headerRow.addView(btnClear);
        layout.addView(headerRow);

        List<AIHistoryItem> history = mHistoryManager.getHistory();
        if (history.isEmpty()) {
            TextView empty = new TextView(context);
            empty.setText("No history items yet. Commands analyzed or generated will appear here.");
            empty.setTextSize(12);
            empty.setTextColor(0xFF8E8E93);
            empty.setPadding(0, dpToPx(context, 24), 0, dpToPx(context, 24));
            empty.setGravity(Gravity.CENTER);
            layout.addView(empty);
        } else {
            for (AIHistoryItem item : history) {
                MaterialCardView itemCard = createResultCard(context);
                LinearLayout inner = createCardInnerLayout(context);

                TextView typeBadge = new TextView(context);
                typeBadge.setText(item.getType().getLabel().toUpperCase());
                typeBadge.setTextColor(item.getType().getColor());
                typeBadge.setTextSize(9);
                typeBadge.setTypeface(null, Typeface.BOLD);
                typeBadge.setBackground(createRoundedBackground(context, (item.getType().getColor() & 0x00FFFFFF) | 0x20000000, 0, 4));
                int padH = dpToPx(context, 6);
                int padV = dpToPx(context, 2);
                typeBadge.setPadding(padH, padV, padH, padV);
                inner.addView(typeBadge);

                TextView query = new TextView(context);
                query.setText(item.getQuery());
                query.setTextSize(12);
                query.setTypeface(null, Typeface.BOLD);
                query.setTextColor(0xFFF2F2F5);
                query.setPadding(0, dpToPx(context, 4), 0, dpToPx(context, 4));
                inner.addView(query);

                if (!item.getResultCommand().isEmpty()) {
                    HorizontalScrollView cmdBox = createTerminalCodeBox(context, item.getResultCommand());
                    inner.addView(cmdBox);

                    LinearLayout actions = createHorizontalActions(context, item.getResultCommand());
                    inner.addView(actions);
                }

                itemCard.addView(inner);
                layout.addView(itemCard);
            }
        }

        mTabContainer.addView(layout);
    }

    // =========================================================================
    // TAB 5: GITHUB WORKSPACE
    // =========================================================================
    private void renderGitHubTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "GitHub Workspace Tools"));
        layout.addView(createSectionSubtext(context, "Clone repositories and launch cloud environments"));

        List<GitHubRepoItem> repos = mGitHubManager.getPopularRepositories();
        for (GitHubRepoItem repo : repos) {
            MaterialCardView card = createResultCard(context);
            LinearLayout inner = createCardInnerLayout(context);

            LinearLayout titleRow = new LinearLayout(context);
            titleRow.setOrientation(LinearLayout.HORIZONTAL);
            titleRow.setGravity(Gravity.CENTER_VERTICAL);

            ImageView repoIcon = new ImageView(context);
            repoIcon.setImageResource(R.drawable.ic_github);
            repoIcon.setColorFilter(0xFFFFFFFF);
            int iconSize = dpToPx(context, 16);
            titleRow.addView(repoIcon, new LinearLayout.LayoutParams(iconSize, iconSize));

            TextView repoName = new TextView(context);
            repoName.setText(repo.getFullName());
            repoName.setTextSize(13);
            repoName.setTypeface(null, Typeface.BOLD);
            repoName.setTextColor(0xFFF2F2F5);
            repoName.setPadding(dpToPx(context, 8), 0, 0, 0);
            titleRow.addView(repoName);
            inner.addView(titleRow);

            TextView desc = new TextView(context);
            desc.setText(repo.getDescription());
            desc.setTextSize(11);
            desc.setTextColor(0xFF8E8E93);
            desc.setPadding(0, dpToPx(context, 4), 0, dpToPx(context, 8));
            inner.addView(desc);

            LinearLayout actionRow = new LinearLayout(context);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);
            actionRow.setGravity(Gravity.END);

            MaterialButton btnCodespace = new MaterialButton(context);
            btnCodespace.setText("Codespace");
            btnCodespace.setTextSize(11);
            btnCodespace.setBackgroundTintList(ColorStateList.valueOf(0xFF202028));
            btnCodespace.setStrokeColor(ColorStateList.valueOf(0xFF33333E));
            btnCodespace.setStrokeWidth(dpToPx(context, 1));
            btnCodespace.setCornerRadius(dpToPx(context, 6));
            btnCodespace.setTextColor(0xFFB0BEC5);
            btnCodespace.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mGitHubManager.generateCodespaceLaunchUrl(repo.getFullName())));
                startActivity(browserIntent);
            });
            actionRow.addView(btnCodespace);

            MaterialButton btnClone = new MaterialButton(context);
            btnClone.setText("Clone in Termux");
            btnClone.setTextSize(11);
            btnClone.setBackgroundTintList(ColorStateList.valueOf(0xFFFF3B30));
            btnClone.setCornerRadius(dpToPx(context, 6));
            btnClone.setTextColor(0xFFFFFFFF);
            LinearLayout.LayoutParams lpClone = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpClone.setMarginStart(dpToPx(context, 8));
            btnClone.setLayoutParams(lpClone);
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

    // =========================================================================
    // UI BUILDER HELPERS
    // =========================================================================
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
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(0xFFF2F2F5);
        tv.setPadding(0, dpToPx(context, 2), 0, dpToPx(context, 2));
        return tv;
    }

    private TextView createSectionSubtext(Context context, String subtext) {
        TextView tv = new TextView(context);
        tv.setText(subtext);
        tv.setTextSize(11);
        tv.setTextColor(0xFF8E8E93);
        tv.setPadding(0, 0, 0, dpToPx(context, 10));
        return tv;
    }

    private EditText createInputField(Context context, String hint) {
        EditText et = new EditText(context);
        et.setHint(hint);
        et.setHintTextColor(0xFF70707A);
        et.setTextSize(13);
        et.setTextColor(0xFFF2F2F5);
        et.setBackground(createRoundedBackground(context, 0xFF121216, 0xFF2A2A34, 10));
        int padH = dpToPx(context, 14);
        int padV = dpToPx(context, 12);
        et.setPadding(padH, padV, padH, padV);
        return et;
    }

    private EditText createMonospaceInputField(Context context, String hint) {
        EditText et = createInputField(context, hint);
        et.setTypeface(Typeface.MONOSPACE);
        return et;
    }

    private ProgressBar createProgressBar(Context context) {
        ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        pb.setIndeterminate(true);
        pb.setVisibility(View.GONE);
        pb.setPadding(0, dpToPx(context, 8), 0, dpToPx(context, 8));
        return pb;
    }

    private MaterialButton createPrimaryButton(Context context, String text, int iconRes, int bgTint) {
        MaterialButton btn = new MaterialButton(context);
        btn.setText(text);
        btn.setIconResource(iconRes);
        btn.setIconSize(dpToPx(context, 16));
        btn.setTextSize(12);
        btn.setBackgroundTintList(ColorStateList.valueOf(bgTint));
        btn.setCornerRadius(dpToPx(context, 8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dpToPx(context, 12);
        btn.setLayoutParams(lp);
        return btn;
    }

    private MaterialCardView createResultCard(Context context) {
        MaterialCardView card = new MaterialCardView(context);
        card.setRadius(dpToPx(context, 14));
        card.setCardElevation(0);
        card.setStrokeWidth(dpToPx(context, 1));
        card.setStrokeColor(0xFF2C2C34);
        card.setCardBackgroundColor(0xFF1A1A20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dpToPx(context, 10);
        card.setLayoutParams(lp);
        return card;
    }

    private LinearLayout createCardInnerLayout(Context context) {
        LinearLayout inner = new LinearLayout(context);
        inner.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(context, 14);
        inner.setPadding(pad, pad, pad, pad);
        return inner;
    }

    private HorizontalScrollView createTerminalCodeBox(Context context, String command) {
        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setBackground(createRoundedBackground(context, 0xFF0A0A0D, 0xFF24242A, 8));
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollLp.topMargin = dpToPx(context, 8);
        scroll.setLayoutParams(scrollLp);

        LinearLayout cmdLayout = new LinearLayout(context);
        cmdLayout.setOrientation(LinearLayout.HORIZONTAL);
        cmdLayout.setGravity(Gravity.CENTER_VERTICAL);
        int padH = dpToPx(context, 12);
        int padV = dpToPx(context, 8);
        cmdLayout.setPadding(padH, padV, padH, padV);

        TextView prompt = new TextView(context);
        prompt.setText("$ ");
        prompt.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        prompt.setTextColor(0xFFFF3B30);
        prompt.setTextSize(12);
        cmdLayout.addView(prompt);

        TextView cmdView = new TextView(context);
        cmdView.setText(command);
        cmdView.setTypeface(Typeface.MONOSPACE);
        cmdView.setTextSize(12);
        cmdView.setTextColor(0xFFECECEF);
        cmdLayout.addView(cmdView);

        scroll.addView(cmdLayout);
        return scroll;
    }

    private LinearLayout createHorizontalActions(Context context, String command) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.END);
        row.setPadding(0, dpToPx(context, 10), 0, 0);

        MaterialButton btnCopy = new MaterialButton(context);
        btnCopy.setText("Copy");
        btnCopy.setIconResource(R.drawable.ic_copy);
        btnCopy.setIconSize(dpToPx(context, 13));
        btnCopy.setIconTint(ColorStateList.valueOf(0xFFB0BEC5));
        btnCopy.setTextSize(11);
        btnCopy.setTextColor(0xFFB0BEC5);
        btnCopy.setBackgroundTintList(ColorStateList.valueOf(0xFF202028));
        btnCopy.setStrokeColor(ColorStateList.valueOf(0xFF32323C));
        btnCopy.setStrokeWidth(dpToPx(context, 1));
        btnCopy.setCornerRadius(dpToPx(context, 6));
        btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("MadMax Command", command));
                Toast.makeText(context, "Command copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
        row.addView(btnCopy);

        MaterialButton btnInsert = new MaterialButton(context);
        btnInsert.setText("Insert");
        btnInsert.setIconResource(R.drawable.ic_insert);
        btnInsert.setIconSize(dpToPx(context, 13));
        btnInsert.setIconTint(ColorStateList.valueOf(0xFFB0BEC5));
        btnInsert.setTextSize(11);
        btnInsert.setTextColor(0xFFB0BEC5);
        btnInsert.setBackgroundTintList(ColorStateList.valueOf(0xFF202028));
        btnInsert.setStrokeColor(ColorStateList.valueOf(0xFF32323C));
        btnInsert.setStrokeWidth(dpToPx(context, 1));
        btnInsert.setCornerRadius(dpToPx(context, 6));
        LinearLayout.LayoutParams lpInsert = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpInsert.setMarginStart(dpToPx(context, 6));
        btnInsert.setLayoutParams(lpInsert);
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
        btnRun.setIconSize(dpToPx(context, 13));
        btnRun.setIconTint(ColorStateList.valueOf(0xFFFFFFFF));
        btnRun.setTextSize(11);
        btnRun.setTextColor(0xFFFFFFFF);
        btnRun.setBackgroundTintList(ColorStateList.valueOf(0xFFFF3B30));
        btnRun.setCornerRadius(dpToPx(context, 6));
        LinearLayout.LayoutParams lpRun = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpRun.setMarginStart(dpToPx(context, 6));
        btnRun.setLayoutParams(lpRun);
        btnRun.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onExecuteCommand(command);
                dismiss();
            }
        });
        row.addView(btnRun);

        return row;
    }

    private static GradientDrawable createRoundedBackground(Context context, int fillColor, int strokeColor, int radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(dpToPx(context, radiusDp));
        gd.setColor(fillColor);
        if (strokeColor != 0) {
            gd.setStroke(dpToPx(context, 1), strokeColor);
        }
        return gd;
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
