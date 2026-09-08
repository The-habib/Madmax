package com.termux.app.madmax.ui.actions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.termux.app.madmax.ai.engine.AIErrorAnalyzer;
import com.termux.app.madmax.ai.model.AIErrorDiagnosis;

import java.util.List;

/**
 * Material 3 Quick Actions and Accessibility Bottom Sheet Dialog.
 * Provides unified terminal shortcuts, accessibility font zoom, common CLI snippets,
 * AI error diagnostics, and session management.
 */
public class QuickActionsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "QuickActionsBottomSheet";
    private static final String ARG_INITIAL_TAB = "arg_initial_tab";

    public static final int TAB_CONTROLS = 0;
    public static final int TAB_ACCESSIBILITY = 1;
    public static final int TAB_SNIPPETS = 2;
    public static final int TAB_AI = 3;
    public static final int TAB_SESSIONS = 4;

    public interface TerminalActionCallback {
        default void onInsertText(@NonNull String text) {}
        default void onExecuteCommand(@NonNull String command) {}
        default void onPasteFromClipboard() {}
        default void onSendInterrupt() {}
        default void onSendSuspend() {}
        default void onSendEof() {}
        default void onClearScreen() {}
        default void onResetTerminal() {}
        default void onChangeFontSize(boolean increase) {}
        default void onResetFontSize() {}
        default int getCurrentFontSize() { return 14; }
        default void onToggleKeyboard() {}
        default void onToggleFullscreen() {}
        default void onToggleWakeLock() {}
        default boolean isWakeLockHeld() { return false; }
        default void onNewSession(boolean failsafe) {}
        default void onRenameSession() {}
        default void onOpenSessionDrawer() {}
        default void onScrollToTop() {}
        default void onScrollToBottom() {}
        @Nullable default String onCaptureTranscript() { return null; }
        default void onOpenAIWorkspace() {}
    }

    private TerminalActionCallback mCallback;
    private FrameLayout mTabContainer;
    private TabLayout mTabLayout;
    private int mCurrentTab = TAB_CONTROLS;
    private String mSelectedSnippetCategory = SnippetCatalog.CAT_ALL;

    public static QuickActionsBottomSheet newInstance() {
        return newInstance(TAB_CONTROLS);
    }

    public static QuickActionsBottomSheet newInstance(int initialTab) {
        QuickActionsBottomSheet sheet = new QuickActionsBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_INITIAL_TAB, initialTab);
        sheet.setArguments(args);
        return sheet;
    }

    public void setActionCallback(@Nullable TerminalActionCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentTab = getArguments().getInt(ARG_INITIAL_TAB, TAB_CONTROLS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_quick_actions, container, false);

        mTabContainer = root.findViewById(R.id.quick_actions_tab_container);
        mTabLayout = root.findViewById(R.id.quick_actions_tab_layout);

        ImageButton closeBtn = root.findViewById(R.id.btn_close_quick_actions);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> dismiss());
        }

        setupTabs();
        return root;
    }

    private void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setText("Controls").setIcon(R.drawable.ic_quick_actions));
        mTabLayout.addTab(mTabLayout.newTab().setText("Accessibility").setIcon(R.drawable.ic_zoom_in));
        mTabLayout.addTab(mTabLayout.newTab().setText("Snippets").setIcon(R.drawable.ic_snippets));
        mTabLayout.addTab(mTabLayout.newTab().setText("AI & Health").setIcon(R.drawable.ic_ai_workspace));
        mTabLayout.addTab(mTabLayout.newTab().setText("Sessions").setIcon(R.drawable.ic_new_session));

        TabLayout.Tab targetTab = mTabLayout.getTabAt(mCurrentTab);
        if (targetTab != null) {
            targetTab.select();
        }

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
        if (mTabContainer == null || getContext() == null) return;
        mTabContainer.removeAllViews();
        switch (mCurrentTab) {
            case TAB_CONTROLS:
                renderControlsTab();
                break;
            case TAB_ACCESSIBILITY:
                renderAccessibilityTab();
                break;
            case TAB_SNIPPETS:
                renderSnippetsTab();
                break;
            case TAB_AI:
                renderAiTab();
                break;
            case TAB_SESSIONS:
                renderSessionsTab();
                break;
        }
    }

    // =========================================================================
    // TAB 0: TERMINAL CONTROLS
    // =========================================================================
    private void renderControlsTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Terminal Signals & Operations"));
        layout.addView(createSectionSubtext(context, "Direct keyboard signals and buffer manipulation"));

        // Row 1: Paste & Clear
        LinearLayout row1 = createGridRow(context);
        row1.addView(createActionCard(context, "📋 Paste Clipboard", "Paste text from Android clipboard", () -> {
            if (mCallback != null) mCallback.onPasteFromClipboard();
            dismiss();
        }));
        row1.addView(createActionCard(context, "🧹 Clear Screen", "Run clear to wipe screen buffer", () -> {
            if (mCallback != null) mCallback.onClearScreen();
            dismiss();
        }));
        layout.addView(row1);

        // Row 2: Ctrl+C & Ctrl+Z
        LinearLayout row2 = createGridRow(context);
        row2.addView(createActionCard(context, "🛑 Interrupt (^C)", "Send SIGINT to halt process", () -> {
            if (mCallback != null) mCallback.onSendInterrupt();
            dismiss();
        }));
        row2.addView(createActionCard(context, "⏸️ Suspend (^Z)", "Send SIGTSTP to background job", () -> {
            if (mCallback != null) mCallback.onSendSuspend();
            dismiss();
        }));
        layout.addView(row2);

        // Row 3: Ctrl+D & Reset
        LinearLayout row3 = createGridRow(context);
        row3.addView(createActionCard(context, "🚪 Send EOF (^D)", "Send End-Of-File / Exit Shell", () -> {
            if (mCallback != null) mCallback.onSendEof();
            dismiss();
        }));
        row3.addView(createActionCard(context, "🔄 Reset Terminal", "Reinitialize garbled terminal state", () -> {
            if (mCallback != null) mCallback.onResetTerminal();
            dismiss();
        }));
        layout.addView(row3);

        // Row 4: Scroll Top & Scroll Bottom
        LinearLayout row4 = createGridRow(context);
        row4.addView(createActionCard(context, "🔝 Scroll to Top", "Jump to top of scroll buffer", () -> {
            if (mCallback != null) mCallback.onScrollToTop();
            dismiss();
        }));
        row4.addView(createActionCard(context, "🔚 Scroll to Bottom", "Jump to latest command output", () -> {
            if (mCallback != null) mCallback.onScrollToBottom();
            dismiss();
        }));
        layout.addView(row4);

        mTabContainer.addView(layout);
    }

    // =========================================================================
    // TAB 1: ACCESSIBILITY & DISPLAY
    // =========================================================================
    private void renderAccessibilityTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Visual & Accessibility Controls"));
        layout.addView(createSectionSubtext(context, "Adjust font size, toggle fullscreen, and manage screen wake state"));

        // Font Size Card
        MaterialCardView fontCard = createCard(context);
        LinearLayout fontLayout = createCardInnerLayout(context);

        TextView fontTitle = new TextView(context);
        fontTitle.setText("Terminal Font Size (Zoom)");
        fontTitle.setTextSize(14);
        fontTitle.setTypeface(null, Typeface.BOLD);
        fontTitle.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        fontLayout.addView(fontTitle);

        LinearLayout fontControlRow = new LinearLayout(context);
        fontControlRow.setOrientation(LinearLayout.HORIZONTAL);
        fontControlRow.setGravity(Gravity.CENTER_VERTICAL);
        fontControlRow.setPadding(0, 12, 0, 8);

        MaterialButton btnDecrease = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnDecrease.setText("A -");
        btnDecrease.setTextSize(14);
        btnDecrease.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        final TextView fontSizeBadge = new TextView(context);
        int currentSize = mCallback != null ? mCallback.getCurrentFontSize() : 14;
        fontSizeBadge.setText(currentSize + " pt");
        fontSizeBadge.setTextSize(16);
        fontSizeBadge.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        fontSizeBadge.setGravity(Gravity.CENTER);
        fontSizeBadge.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
        fontSizeBadge.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorPrimary));

        MaterialButton btnIncrease = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnIncrease.setText("A +");
        btnIncrease.setTextSize(14);
        btnIncrease.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        btnDecrease.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onChangeFontSize(false);
                fontSizeBadge.setText(mCallback.getCurrentFontSize() + " pt");
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onChangeFontSize(true);
                fontSizeBadge.setText(mCallback.getCurrentFontSize() + " pt");
            }
        });

        fontControlRow.addView(btnDecrease);
        fontControlRow.addView(fontSizeBadge);
        fontControlRow.addView(btnIncrease);
        fontLayout.addView(fontControlRow);

        MaterialButton btnResetFont = new MaterialButton(context, null, com.google.android.material.R.attr.borderlessButtonStyle);
        btnResetFont.setText("Reset to Default Size");
        btnResetFont.setTextSize(12);
        btnResetFont.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onResetFontSize();
                fontSizeBadge.setText(mCallback.getCurrentFontSize() + " pt");
            }
        });
        fontLayout.addView(btnResetFont);

        fontCard.addView(fontLayout);
        layout.addView(fontCard);

        // Hardware / Screen Options Card
        MaterialCardView screenCard = createCard(context);
        LinearLayout screenLayout = createCardInnerLayout(context);

        TextView screenTitle = new TextView(context);
        screenTitle.setText("Display & Input Settings");
        screenTitle.setTextSize(14);
        screenTitle.setTypeface(null, Typeface.BOLD);
        screenTitle.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        screenLayout.addView(screenTitle);

        MaterialButton btnKeyboard = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnKeyboard.setText("⌨️ Toggle Software Keyboard");
        btnKeyboard.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        btnKeyboard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) btnKeyboard.getLayoutParams()).topMargin = 12;
        btnKeyboard.setOnClickListener(v -> {
            if (mCallback != null) mCallback.onToggleKeyboard();
            dismiss();
        });
        screenLayout.addView(btnKeyboard);

        boolean wakeHeld = mCallback != null && mCallback.isWakeLockHeld();
        MaterialButton btnWakeLock = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnWakeLock.setText(wakeHeld ? "⚡ Release WakeLock (Allow CPU Sleep)" : "⚡ Acquire WakeLock (Keep CPU Awake)");
        btnWakeLock.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        btnWakeLock.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) btnWakeLock.getLayoutParams()).topMargin = 8;
        btnWakeLock.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onToggleWakeLock();
                boolean nowHeld = mCallback.isWakeLockHeld();
                btnWakeLock.setText(nowHeld ? "⚡ Release WakeLock (Allow CPU Sleep)" : "⚡ Acquire WakeLock (Keep CPU Awake)");
                Toast.makeText(context, nowHeld ? "WakeLock acquired: CPU will stay awake." : "WakeLock released.", Toast.LENGTH_SHORT).show();
            }
        });
        screenLayout.addView(btnWakeLock);

        screenCard.addView(screenLayout);
        layout.addView(screenCard);

        mTabContainer.addView(layout);
    }

    // =========================================================================
    // TAB 2: COMMAND SNIPPETS & SHORTCUTS
    // =========================================================================
    private void renderSnippetsTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Curated CLI Snippets"));
        layout.addView(createSectionSubtext(context, "Tap 'Run' to execute immediately or 'Insert' to edit first"));

        // Category Filter Chips
        ChipGroup chipGroup = new ChipGroup(context);
        chipGroup.setSingleSelection(true);
        chipGroup.setPadding(0, 4, 0, 8);

        List<String> categories = SnippetCatalog.getCategories();
        for (String cat : categories) {
            Chip chip = new Chip(context);
            chip.setText(cat);
            chip.setCheckable(true);
            if (cat.equalsIgnoreCase(mSelectedSnippetCategory)) {
                chip.setChecked(true);
            }
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mSelectedSnippetCategory = cat;
                    renderCurrentTab();
                }
            });
            chipGroup.addView(chip);
        }
        layout.addView(chipGroup);

        List<TerminalSnippet> snippets = SnippetCatalog.getSnippetsByCategory(mSelectedSnippetCategory);
        for (TerminalSnippet snippet : snippets) {
            MaterialCardView card = createSnippetCard(context, snippet);
            layout.addView(card);
        }

        mTabContainer.addView(layout);
    }

    private MaterialCardView createSnippetCard(Context context, TerminalSnippet snippet) {
        MaterialCardView card = createCard(context);
        LinearLayout inner = createCardInnerLayout(context);

        TextView title = new TextView(context);
        title.setText(snippet.getTitle());
        title.setTextSize(14);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        inner.addView(title);

        TextView desc = new TextView(context);
        desc.setText(snippet.getDescription());
        desc.setTextSize(12);
        desc.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        desc.setPadding(0, 2, 0, 8);
        inner.addView(desc);

        // Command display
        TextView cmdView = new TextView(context);
        cmdView.setText(snippet.getCommand());
        cmdView.setTypeface(Typeface.MONOSPACE);
        cmdView.setTextSize(12);
        cmdView.setTextColor(0xFF80DEEA);
        cmdView.setBackgroundColor(0x33000000);
        cmdView.setPadding(16, 12, 16, 12);
        inner.addView(cmdView);

        // Buttons row: Copy, Insert, Run
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.END);
        btnRow.setPadding(0, 8, 0, 0);

        MaterialButton btnCopy = new MaterialButton(context, null, com.google.android.material.R.attr.borderlessButtonStyle);
        btnCopy.setText("Copy");
        btnCopy.setTextSize(11);
        btnCopy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("MadMax Snippet", snippet.getCommand()));
                Toast.makeText(context, "Snippet copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
        btnRow.addView(btnCopy);

        MaterialButton btnInsert = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnInsert.setText("Insert");
        btnInsert.setTextSize(11);
        LinearLayout.LayoutParams lpInsert = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpInsert.setMarginStart(8);
        btnInsert.setLayoutParams(lpInsert);
        btnInsert.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onInsertText(snippet.getCommand());
                dismiss();
            }
        });
        btnRow.addView(btnInsert);

        MaterialButton btnRun = new MaterialButton(context);
        btnRun.setText("Run");
        btnRun.setTextSize(11);
        LinearLayout.LayoutParams lpRun = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpRun.setMarginStart(8);
        btnRun.setLayoutParams(lpRun);
        btnRun.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onExecuteCommand(snippet.getCommand());
                dismiss();
            }
        });
        btnRow.addView(btnRun);

        inner.addView(btnRow);
        card.addView(inner);
        return card;
    }

    // =========================================================================
    // TAB 3: AI & HEALTH DIAGNOSTICS
    // =========================================================================
    private void renderAiTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "AI Command Intelligence & Diagnostics"));
        layout.addView(createSectionSubtext(context, "Offline analysis, flag decomposition, and automated error recovery"));

        // Card 1: AI Workspace
        MaterialCardView aiCard = createCard(context);
        LinearLayout aiInner = createCardInnerLayout(context);

        TextView aiTitle = new TextView(context);
        aiTitle.setText("🤖 AI Command Workspace");
        aiTitle.setTextSize(14);
        aiTitle.setTypeface(null, Typeface.BOLD);
        aiTitle.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        aiInner.addView(aiTitle);

        TextView aiDesc = new TextView(context);
        aiDesc.setText("Decompose shell commands, generate complex syntax, and query offline intelligence for 70+ utilities.");
        aiDesc.setTextSize(12);
        aiDesc.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        aiDesc.setPadding(0, 4, 0, 12);
        aiInner.addView(aiDesc);

        MaterialButton btnOpenAi = new MaterialButton(context);
        btnOpenAi.setText("Open AI Workspace");
        btnOpenAi.setIconResource(R.drawable.ic_ai_workspace);
        btnOpenAi.setIconSize(36);
        btnOpenAi.setOnClickListener(v -> {
            dismiss();
            if (mCallback != null) {
                mCallback.onOpenAIWorkspace();
            }
        });
        aiInner.addView(btnOpenAi);
        aiCard.addView(aiInner);
        layout.addView(aiCard);

        // Card 2: Scan Terminal Screen for Errors
        MaterialCardView diagCard = createCard(context);
        LinearLayout diagInner = createCardInnerLayout(context);

        TextView diagTitle = new TextView(context);
        diagTitle.setText("🩺 Terminal Screen Health Diagnostic");
        diagTitle.setTextSize(14);
        diagTitle.setTypeface(null, Typeface.BOLD);
        diagTitle.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        diagInner.addView(diagTitle);

        TextView diagDesc = new TextView(context);
        diagDesc.setText("Scan current terminal screen output for errors (command not found, permission denied, package lock, git conflicts).");
        diagDesc.setTextSize(12);
        diagDesc.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        diagDesc.setPadding(0, 4, 0, 12);
        diagInner.addView(diagDesc);

        LinearLayout diagResultContainer = createBaseVerticalLayout(context);

        MaterialButton btnScan = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnScan.setText("Scan Terminal Screen");
        btnScan.setIconResource(R.drawable.ic_diagnose);
        btnScan.setIconSize(36);
        btnScan.setOnClickListener(v -> {
            diagResultContainer.removeAllViews();
            String transcript = mCallback != null ? mCallback.onCaptureTranscript() : null;
            if (transcript == null || transcript.trim().isEmpty()) {
                TextView empty = new TextView(context);
                empty.setText("Terminal screen is currently empty. Run commands to see diagnostics.");
                empty.setTextSize(12);
                empty.setPadding(0, 8, 0, 0);
                diagResultContainer.addView(empty);
                return;
            }

            AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose(transcript);
            renderDiagnosisResult(context, diagResultContainer, diag);
        });
        diagInner.addView(btnScan);
        diagInner.addView(diagResultContainer);
        diagCard.addView(diagInner);
        layout.addView(diagCard);

        mTabContainer.addView(layout);
    }

    private void renderDiagnosisResult(Context context, LinearLayout container, AIErrorDiagnosis diag) {
        TextView category = new TextView(context);
        category.setText("Detected: " + diag.getCategory().getDisplayName());
        category.setTextSize(13);
        category.setTypeface(null, Typeface.BOLD);
        category.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorPrimary));
        category.setPadding(0, 12, 0, 4);
        container.addView(category);

        TextView cause = new TextView(context);
        cause.setText("Root Cause: " + diag.getRootCause());
        cause.setTextSize(12);
        cause.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        container.addView(cause);

        TextView remedy = new TextView(context);
        remedy.setText("Remedy: " + diag.getRemedyExplanation());
        remedy.setTextSize(12);
        remedy.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        remedy.setPadding(0, 4, 0, 8);
        container.addView(remedy);

        if (!diag.getSuggestedFixCommand().isEmpty()) {
            TextView fixCmd = new TextView(context);
            fixCmd.setText(diag.getSuggestedFixCommand());
            fixCmd.setTypeface(Typeface.MONOSPACE);
            fixCmd.setTextSize(12);
            fixCmd.setTextColor(0xFF80DEEA);
            fixCmd.setBackgroundColor(0x33000000);
            fixCmd.setPadding(16, 12, 16, 12);
            container.addView(fixCmd);

            MaterialButton btnFix = new MaterialButton(context);
            btnFix.setText("Execute Fix");
            btnFix.setIconResource(R.drawable.ic_play);
            btnFix.setIconSize(32);
            btnFix.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ((LinearLayout.LayoutParams) btnFix.getLayoutParams()).topMargin = 8;
            btnFix.setOnClickListener(v -> {
                if (mCallback != null) {
                    mCallback.onExecuteCommand(diag.getSuggestedFixCommand());
                    dismiss();
                }
            });
            container.addView(btnFix);
        }
    }

    // =========================================================================
    // TAB 4: SESSION MANAGEMENT
    // =========================================================================
    private void renderSessionsTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Session Management"));
        layout.addView(createSectionSubtext(context, "Manage terminal windows, shells, and session transcripts"));

        LinearLayout row1 = createGridRow(context);
        row1.addView(createActionCard(context, "➕ New Session", "Open a new interactive shell session", () -> {
            if (mCallback != null) mCallback.onNewSession(false);
            dismiss();
        }));
        row1.addView(createActionCard(context, "🔒 Failsafe Session", "Start failsafe shell without dotfiles", () -> {
            if (mCallback != null) mCallback.onNewSession(true);
            dismiss();
        }));
        layout.addView(row1);

        LinearLayout row2 = createGridRow(context);
        row2.addView(createActionCard(context, "🏷️ Rename Session", "Assign custom label to current session", () -> {
            if (mCallback != null) mCallback.onRenameSession();
            dismiss();
        }));
        row2.addView(createActionCard(context, "📑 Session Drawer", "Open side drawer to switch sessions", () -> {
            if (mCallback != null) mCallback.onOpenSessionDrawer();
            dismiss();
        }));
        layout.addView(row2);

        MaterialCardView copyCard = createCard(context);
        LinearLayout copyInner = createCardInnerLayout(context);
        TextView copyTitle = new TextView(context);
        copyTitle.setText("📋 Full Screen Transcript Copy");
        copyTitle.setTextSize(14);
        copyTitle.setTypeface(null, Typeface.BOLD);
        copyTitle.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        copyInner.addView(copyTitle);

        TextView copyDesc = new TextView(context);
        copyDesc.setText("Copy entire terminal screen buffer to Android clipboard.");
        copyDesc.setTextSize(12);
        copyDesc.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        copyDesc.setPadding(0, 2, 0, 8);
        copyInner.addView(copyDesc);

        MaterialButton btnCopyAll = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnCopyAll.setText("Copy Transcript");
        btnCopyAll.setIconResource(R.drawable.ic_copy);
        btnCopyAll.setIconSize(32);
        btnCopyAll.setOnClickListener(v -> {
            String transcript = mCallback != null ? mCallback.onCaptureTranscript() : null;
            if (transcript != null && !transcript.isEmpty()) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    cm.setPrimaryClip(ClipData.newPlainText("Terminal Transcript", transcript));
                    Toast.makeText(context, "Terminal transcript copied to clipboard.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Terminal output is currently empty.", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
        copyInner.addView(btnCopyAll);
        copyCard.addView(copyInner);
        layout.addView(copyCard);

        mTabContainer.addView(layout);
    }

    // =========================================================================
    // UI BUILDER HELPERS
    // =========================================================================
    private LinearLayout createBaseVerticalLayout(Context context) {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return l;
    }

    private TextView createSectionHeader(Context context, String title) {
        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setTextSize(16);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        tv.setPadding(0, 4, 0, 2);
        return tv;
    }

    private TextView createSectionSubtext(Context context, String subtext) {
        TextView tv = new TextView(context);
        tv.setText(subtext);
        tv.setTextSize(12);
        tv.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        tv.setPadding(0, 0, 0, 12);
        return tv;
    }

    private LinearLayout createGridRow(Context context) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return row;
    }

    private MaterialCardView createCard(Context context) {
        MaterialCardView card = new MaterialCardView(context);
        card.setRadius(20);
        card.setCardElevation(0);
        card.setStrokeWidth(2);
        card.setStrokeColor(0x338E8E93);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 10;
        card.setLayoutParams(lp);
        return card;
    }

    private LinearLayout createCardInnerLayout(Context context) {
        LinearLayout inner = new LinearLayout(context);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(16, 14, 16, 14);
        return inner;
    }

    private View createActionCard(Context context, String title, String subtitle, Runnable action) {
        MaterialCardView card = new MaterialCardView(context);
        card.setRadius(16);
        card.setCardElevation(0);
        card.setStrokeWidth(2);
        card.setStrokeColor(0x338E8E93);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMarginEnd(6);
        lp.setMarginStart(6);
        lp.bottomMargin = 10;
        card.setLayoutParams(lp);

        LinearLayout inner = new LinearLayout(context);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(12, 12, 12, 12);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setTextSize(13);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(getColorFromAttr(context, com.google.android.material.R.attr.colorOnSurface));
        inner.addView(tvTitle);

        TextView tvSub = new TextView(context);
        tvSub.setText(subtitle);
        tvSub.setTextSize(11);
        tvSub.setTextColor(getColorFromAttr(context, android.R.attr.textColorSecondary));
        tvSub.setPadding(0, 2, 0, 0);
        inner.addView(tvSub);

        card.addView(inner);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> action.run());
        return card;
    }

    private int getColorFromAttr(Context context, int attr) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        if (context.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        }
        return ContextCompat.getColor(context, android.R.color.white);
    }
}
