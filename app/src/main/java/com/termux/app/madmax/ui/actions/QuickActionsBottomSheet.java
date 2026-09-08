package com.termux.app.madmax.ui.actions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
 * Provides unified terminal shortcuts, accessibility font zoom with live preview,
 * curated CLI snippets, AI error diagnostics, and session management.
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

        layout.addView(createSectionHeader(context, "Signals & Terminal Operations"));
        layout.addView(createSectionSubtext(context, "Direct keyboard signals and buffer manipulation"));

        // Row 1: Paste & Clear
        LinearLayout row1 = createGridRow(context);
        row1.addView(createActionCard(context, R.drawable.ic_paste, 0xFF80DEEA, "Paste Clipboard", "Insert clipboard text", () -> {
            if (mCallback != null) mCallback.onPasteFromClipboard();
            dismiss();
        }));
        row1.addView(createActionCard(context, R.drawable.ic_clear_all, 0xFF4DD0E1, "Clear Screen", "Wipe terminal buffer", () -> {
            if (mCallback != null) mCallback.onClearScreen();
            dismiss();
        }));
        layout.addView(row1);

        // Row 2: Ctrl+C & Ctrl+Z
        LinearLayout row2 = createGridRow(context);
        row2.addView(createActionCard(context, R.drawable.ic_stop, 0xFFFF5252, "Interrupt (^C)", "Send SIGINT to process", () -> {
            if (mCallback != null) mCallback.onSendInterrupt();
            dismiss();
        }));
        row2.addView(createActionCard(context, R.drawable.ic_pause, 0xFFFFB74D, "Suspend (^Z)", "Send SIGTSTP to background", () -> {
            if (mCallback != null) mCallback.onSendSuspend();
            dismiss();
        }));
        layout.addView(row2);

        // Row 3: Ctrl+D & Reset
        LinearLayout row3 = createGridRow(context);
        row3.addView(createActionCard(context, R.drawable.ic_exit, 0xFF7986CB, "Send EOF (^D)", "End-Of-File / Exit Shell", () -> {
            if (mCallback != null) mCallback.onSendEof();
            dismiss();
        }));
        row3.addView(createActionCard(context, R.drawable.ic_reset, 0xFF90A4AE, "Reset Terminal", "Reinitialize terminal state", () -> {
            if (mCallback != null) mCallback.onResetTerminal();
            dismiss();
        }));
        layout.addView(row3);

        // Row 4: Scroll Top & Scroll Bottom
        LinearLayout row4 = createGridRow(context);
        row4.addView(createActionCard(context, R.drawable.ic_scroll_top, 0xFF81C784, "Scroll to Top", "Jump to buffer start", () -> {
            if (mCallback != null) mCallback.onScrollToTop();
            dismiss();
        }));
        row4.addView(createActionCard(context, R.drawable.ic_scroll_bottom, 0xFF81C784, "Scroll to Bottom", "Jump to latest command", () -> {
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
        layout.addView(createSectionSubtext(context, "Adjust font size, live preview text scale, and screen options"));

        // --- Font Size Card ---
        MaterialCardView fontCard = createCard(context);
        LinearLayout fontInner = createCardInnerLayout(context);

        // Header Row with Icon Badge
        LinearLayout fontHeaderRow = new LinearLayout(context);
        fontHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
        fontHeaderRow.setGravity(Gravity.CENTER_VERTICAL);
        fontHeaderRow.addView(createIconBadge(context, R.drawable.ic_zoom_in, 0xFFFF3B30, 36));

        LinearLayout fontTitleCol = new LinearLayout(context);
        fontTitleCol.setOrientation(LinearLayout.VERTICAL);
        fontTitleCol.setPadding(dpToPx(context, 12), 0, 0, 0);

        TextView fontTitle = new TextView(context);
        fontTitle.setText("Terminal Font Size");
        fontTitle.setTextSize(14);
        fontTitle.setTypeface(null, Typeface.BOLD);
        fontTitle.setTextColor(0xFFF2F2F5);
        fontTitleCol.addView(fontTitle);

        TextView fontSub = new TextView(context);
        fontSub.setText("Scale font size for optimal readability");
        fontSub.setTextSize(11);
        fontSub.setTextColor(0xFF8E8E93);
        fontTitleCol.addView(fontSub);
        fontHeaderRow.addView(fontTitleCol);
        fontInner.addView(fontHeaderRow);

        // Stepper Controller Container
        LinearLayout stepperContainer = new LinearLayout(context);
        stepperContainer.setOrientation(LinearLayout.HORIZONTAL);
        stepperContainer.setGravity(Gravity.CENTER_VERTICAL);
        stepperContainer.setBackground(createRoundedBackground(context, 0xFF141418, 0xFF2A2A34, 10));
        stepperContainer.setPadding(dpToPx(context, 8), dpToPx(context, 6), dpToPx(context, 8), dpToPx(context, 6));
        LinearLayout.LayoutParams stepperLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stepperLp.topMargin = dpToPx(context, 12);
        stepperContainer.setLayoutParams(stepperLp);

        // Button A-
        MaterialButton btnDecrease = new MaterialButton(context);
        btnDecrease.setIconResource(R.drawable.ic_zoom_out);
        btnDecrease.setIconSize(dpToPx(context, 18));
        btnDecrease.setIconTint(ColorStateList.valueOf(0xFFE0E0E0));
        btnDecrease.setText("A -");
        btnDecrease.setTextSize(12);
        btnDecrease.setTextColor(0xFFE0E0E0);
        btnDecrease.setBackgroundTintList(ColorStateList.valueOf(0xFF22222A));
        btnDecrease.setStrokeColor(ColorStateList.valueOf(0xFF33333E));
        btnDecrease.setStrokeWidth(dpToPx(context, 1));
        btnDecrease.setCornerRadius(dpToPx(context, 8));
        btnDecrease.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(context, 40), 1f));

        // Size Display Badge
        int currentSize = mCallback != null ? mCallback.getCurrentFontSize() : 14;
        final TextView fontSizeBadge = new TextView(context);
        fontSizeBadge.setText(currentSize + " pt");
        fontSizeBadge.setTextSize(17);
        fontSizeBadge.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        fontSizeBadge.setGravity(Gravity.CENTER);
        fontSizeBadge.setTextColor(0xFFFF3B30);
        fontSizeBadge.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));

        // Button A+
        MaterialButton btnIncrease = new MaterialButton(context);
        btnIncrease.setIconResource(R.drawable.ic_zoom_in);
        btnIncrease.setIconSize(dpToPx(context, 18));
        btnIncrease.setIconTint(ColorStateList.valueOf(0xFFE0E0E0));
        btnIncrease.setText("A +");
        btnIncrease.setTextSize(12);
        btnIncrease.setTextColor(0xFFE0E0E0);
        btnIncrease.setBackgroundTintList(ColorStateList.valueOf(0xFF22222A));
        btnIncrease.setStrokeColor(ColorStateList.valueOf(0xFF33333E));
        btnIncrease.setStrokeWidth(dpToPx(context, 1));
        btnIncrease.setCornerRadius(dpToPx(context, 8));
        btnIncrease.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(context, 40), 1f));

        stepperContainer.addView(btnDecrease);
        stepperContainer.addView(fontSizeBadge);
        stepperContainer.addView(btnIncrease);
        fontInner.addView(stepperContainer);

        // Live Preview Box
        LinearLayout previewContainer = new LinearLayout(context);
        previewContainer.setOrientation(LinearLayout.VERTICAL);
        previewContainer.setBackground(createRoundedBackground(context, 0xFF0A0A0D, 0xFF22222A, 8));
        previewContainer.setPadding(dpToPx(context, 12), dpToPx(context, 10), dpToPx(context, 12), dpToPx(context, 10));
        LinearLayout.LayoutParams prevLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prevLp.topMargin = dpToPx(context, 10);
        previewContainer.setLayoutParams(prevLp);

        TextView prevHeader = new TextView(context);
        prevHeader.setText("LIVE PREVIEW");
        prevHeader.setTextSize(10);
        prevHeader.setTypeface(null, Typeface.BOLD);
        prevHeader.setTextColor(0xFF6B6B76);
        prevHeader.setLetterSpacing(0.06f);
        previewContainer.addView(prevHeader);

        final TextView previewText = new TextView(context);
        previewText.setText("$ uname -m && whoami\naarch64\nu0_a123");
        previewText.setTextSize(Math.max(10, Math.min(22, currentSize)));
        previewText.setTypeface(Typeface.MONOSPACE);
        previewText.setTextColor(0xFFE0E0E6);
        previewText.setPadding(0, dpToPx(context, 4), 0, 0);
        previewContainer.addView(previewText);
        fontInner.addView(previewContainer);

        // Reset Button
        MaterialButton btnResetFont = new MaterialButton(context, null, com.google.android.material.R.attr.borderlessButtonStyle);
        btnResetFont.setText("Reset to Default (14 pt)");
        btnResetFont.setIconResource(R.drawable.ic_reset);
        btnResetFont.setIconSize(dpToPx(context, 14));
        btnResetFont.setIconTint(ColorStateList.valueOf(0xFF8E8E93));
        btnResetFont.setTextColor(0xFF8E8E93);
        btnResetFont.setTextSize(11);
        btnResetFont.setAllCaps(false);
        LinearLayout.LayoutParams resetLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        resetLp.gravity = Gravity.CENTER_HORIZONTAL;
        resetLp.topMargin = dpToPx(context, 4);
        btnResetFont.setLayoutParams(resetLp);

        btnDecrease.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onChangeFontSize(false);
                int updated = mCallback.getCurrentFontSize();
                fontSizeBadge.setText(updated + " pt");
                previewText.setTextSize(Math.max(10, Math.min(22, updated)));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onChangeFontSize(true);
                int updated = mCallback.getCurrentFontSize();
                fontSizeBadge.setText(updated + " pt");
                previewText.setTextSize(Math.max(10, Math.min(22, updated)));
            }
        });

        btnResetFont.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onResetFontSize();
                int updated = mCallback.getCurrentFontSize();
                fontSizeBadge.setText(updated + " pt");
                previewText.setTextSize(Math.max(10, Math.min(22, updated)));
            }
        });

        fontInner.addView(btnResetFont);
        fontCard.addView(fontInner);
        layout.addView(fontCard);

        // --- Hardware & Display Settings ---
        layout.addView(createSectionHeader(context, "Display & Input"));

        // Software Keyboard Card
        MaterialCardView kbdCard = createCard(context);
        LinearLayout kbdInner = createCardInnerLayout(context);
        LinearLayout kbdRow = new LinearLayout(context);
        kbdRow.setOrientation(LinearLayout.HORIZONTAL);
        kbdRow.setGravity(Gravity.CENTER_VERTICAL);
        kbdRow.addView(createIconBadge(context, R.drawable.ic_keyboard, 0xFFB0BEC5, 36));

        LinearLayout kbdTextCol = new LinearLayout(context);
        kbdTextCol.setOrientation(LinearLayout.VERTICAL);
        kbdTextCol.setPadding(dpToPx(context, 12), 0, 0, 0);
        kbdTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView kbdTitle = new TextView(context);
        kbdTitle.setText("Software Keyboard");
        kbdTitle.setTextSize(13);
        kbdTitle.setTypeface(null, Typeface.BOLD);
        kbdTitle.setTextColor(0xFFF2F2F5);
        kbdTextCol.addView(kbdTitle);

        TextView kbdSub = new TextView(context);
        kbdSub.setText("Toggle on-screen soft keyboard");
        kbdSub.setTextSize(11);
        kbdSub.setTextColor(0xFF8E8E93);
        kbdTextCol.addView(kbdSub);
        kbdRow.addView(kbdTextCol);

        MaterialButton btnToggleKbd = new MaterialButton(context);
        btnToggleKbd.setText("Toggle");
        btnToggleKbd.setTextSize(11);
        btnToggleKbd.setBackgroundTintList(ColorStateList.valueOf(0xFF24242C));
        btnToggleKbd.setStrokeColor(ColorStateList.valueOf(0xFF383844));
        btnToggleKbd.setStrokeWidth(dpToPx(context, 1));
        btnToggleKbd.setCornerRadius(dpToPx(context, 8));
        btnToggleKbd.setTextColor(0xFFF2F2F5);
        btnToggleKbd.setOnClickListener(v -> {
            if (mCallback != null) mCallback.onToggleKeyboard();
            dismiss();
        });
        kbdRow.addView(btnToggleKbd);
        kbdInner.addView(kbdRow);
        kbdCard.addView(kbdInner);
        layout.addView(kbdCard);

        // WakeLock Card
        MaterialCardView wakeCard = createCard(context);
        LinearLayout wakeInner = createCardInnerLayout(context);
        LinearLayout wakeRow = new LinearLayout(context);
        wakeRow.setOrientation(LinearLayout.HORIZONTAL);
        wakeRow.setGravity(Gravity.CENTER_VERTICAL);
        wakeRow.addView(createIconBadge(context, R.drawable.ic_bolt, 0xFFFFD54F, 36));

        LinearLayout wakeTextCol = new LinearLayout(context);
        wakeTextCol.setOrientation(LinearLayout.VERTICAL);
        wakeTextCol.setPadding(dpToPx(context, 12), 0, 0, 0);
        wakeTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView wakeTitle = new TextView(context);
        wakeTitle.setText("CPU WakeLock");
        wakeTitle.setTextSize(13);
        wakeTitle.setTypeface(null, Typeface.BOLD);
        wakeTitle.setTextColor(0xFFF2F2F5);
        wakeTextCol.addView(wakeTitle);

        TextView wakeSub = new TextView(context);
        wakeSub.setText("Keep CPU awake when screen is off");
        wakeSub.setTextSize(11);
        wakeSub.setTextColor(0xFF8E8E93);
        wakeTextCol.addView(wakeSub);
        wakeRow.addView(wakeTextCol);

        boolean wakeHeld = mCallback != null && mCallback.isWakeLockHeld();
        final TextView wakeStatusPill = new TextView(context);
        updateWakeLockPill(context, wakeStatusPill, wakeHeld);
        wakeRow.addView(wakeStatusPill);

        wakeCard.setClickable(true);
        wakeCard.setFocusable(true);
        wakeCard.setOnClickListener(v -> {
            if (mCallback != null) {
                mCallback.onToggleWakeLock();
                boolean nowHeld = mCallback.isWakeLockHeld();
                updateWakeLockPill(context, wakeStatusPill, nowHeld);
                Toast.makeText(context, nowHeld ? "WakeLock acquired: CPU will remain awake." : "WakeLock released.", Toast.LENGTH_SHORT).show();
            }
        });

        wakeInner.addView(wakeRow);
        wakeCard.addView(wakeInner);
        layout.addView(wakeCard);

        mTabContainer.addView(layout);
    }

    private void updateWakeLockPill(Context context, TextView pill, boolean held) {
        pill.setText(held ? "ACTIVE" : "OFF");
        pill.setTextSize(10);
        pill.setTypeface(null, Typeface.BOLD);
        pill.setGravity(Gravity.CENTER);
        int padH = dpToPx(context, 10);
        int padV = dpToPx(context, 4);
        pill.setPadding(padH, padV, padH, padV);

        int bgColor = held ? 0x2534C759 : 0x208E8E93;
        int strokeColor = held ? 0x6034C759 : 0x408E8E93;
        pill.setTextColor(held ? 0xFF34C759 : 0xFF8E8E93);
        pill.setBackground(createRoundedBackground(context, bgColor, strokeColor, 6));
    }

    // =========================================================================
    // TAB 2: COMMAND SNIPPETS & SHORTCUTS
    // =========================================================================
    private void renderSnippetsTab() {
        Context context = requireContext();
        LinearLayout layout = createBaseVerticalLayout(context);

        layout.addView(createSectionHeader(context, "Curated CLI Snippets"));
        layout.addView(createSectionSubtext(context, "Tap 'Run' to execute immediately or 'Insert' to edit first"));

        // Category Filter Chips in Horizontal Scroll
        HorizontalScrollView chipScroll = new HorizontalScrollView(context);
        chipScroll.setHorizontalScrollBarEnabled(false);
        chipScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ChipGroup chipGroup = new ChipGroup(context);
        chipGroup.setSingleSelection(true);
        chipGroup.setPadding(0, 0, 0, dpToPx(context, 8));

        List<String> categories = SnippetCatalog.getCategories();
        for (String cat : categories) {
            Chip chip = new Chip(context);
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setChipCornerRadius(dpToPx(context, 16));
            chip.setChipBackgroundColor(ColorStateList.valueOf(0xFF202028));
            chip.setChipStrokeColor(ColorStateList.valueOf(0xFF33333E));
            chip.setChipStrokeWidth(dpToPx(context, 1));
            chip.setTextColor(0xFFE0E0E6);
            chip.setTextSize(11);

            if (cat.equalsIgnoreCase(mSelectedSnippetCategory)) {
                chip.setChecked(true);
                chip.setChipBackgroundColor(ColorStateList.valueOf(0xFFFF3B30));
                chip.setTextColor(0xFFFFFFFF);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mSelectedSnippetCategory = cat;
                    renderCurrentTab();
                }
            });
            chipGroup.addView(chip);
        }
        chipScroll.addView(chipGroup);
        layout.addView(chipScroll);

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

        // Header Row: Title on Left, Category Tag on Right
        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(context);
        title.setText(snippet.getTitle());
        title.setTextSize(13);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFFF2F2F5);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        headerRow.addView(title);

        TextView categoryBadge = new TextView(context);
        categoryBadge.setText(snippet.getCategory().toUpperCase());
        categoryBadge.setTextSize(9);
        categoryBadge.setTypeface(null, Typeface.BOLD);
        categoryBadge.setTextColor(0xFFFF6B60);
        categoryBadge.setBackground(createRoundedBackground(context, 0x22FF3B30, 0x40FF3B30, 6));
        int padH = dpToPx(context, 6);
        int padV = dpToPx(context, 2);
        categoryBadge.setPadding(padH, padV, padH, padV);
        headerRow.addView(categoryBadge);
        inner.addView(headerRow);

        TextView desc = new TextView(context);
        desc.setText(snippet.getDescription());
        desc.setTextSize(11);
        desc.setTextColor(0xFF8E8E93);
        desc.setPadding(0, dpToPx(context, 2), 0, dpToPx(context, 8));
        inner.addView(desc);

        // Terminal Command Box
        HorizontalScrollView cmdScroll = new HorizontalScrollView(context);
        cmdScroll.setHorizontalScrollBarEnabled(false);
        cmdScroll.setBackground(createRoundedBackground(context, 0xFF09090C, 0xFF24242A, 8));

        LinearLayout cmdLayout = new LinearLayout(context);
        cmdLayout.setOrientation(LinearLayout.HORIZONTAL);
        cmdLayout.setGravity(Gravity.CENTER_VERTICAL);
        cmdLayout.setPadding(dpToPx(context, 12), dpToPx(context, 8), dpToPx(context, 12), dpToPx(context, 8));

        TextView promptSymbol = new TextView(context);
        promptSymbol.setText("$ ");
        promptSymbol.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        promptSymbol.setTextColor(0xFFFF3B30);
        promptSymbol.setTextSize(12);
        cmdLayout.addView(promptSymbol);

        TextView cmdView = new TextView(context);
        cmdView.setText(snippet.getCommand());
        cmdView.setTypeface(Typeface.MONOSPACE);
        cmdView.setTextSize(12);
        cmdView.setTextColor(0xFFECECEF);
        cmdLayout.addView(cmdView);

        cmdScroll.addView(cmdLayout);
        inner.addView(cmdScroll);

        // Buttons row: Copy, Insert, Run
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.END);
        btnRow.setPadding(0, dpToPx(context, 10), 0, 0);

        // Copy Button
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
                cm.setPrimaryClip(ClipData.newPlainText("MadMax Snippet", snippet.getCommand()));
                Toast.makeText(context, "Snippet copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });
        btnRow.addView(btnCopy);

        // Insert Button
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
                mCallback.onInsertText(snippet.getCommand());
                dismiss();
            }
        });
        btnRow.addView(btnInsert);

        // Run Button
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

        layout.addView(createSectionHeader(context, "AI Intelligence & Health"));
        layout.addView(createSectionSubtext(context, "Offline analysis, syntax intelligence, and error recovery"));

        // Card 1: AI Workspace
        MaterialCardView aiCard = createCard(context);
        LinearLayout aiInner = createCardInnerLayout(context);

        LinearLayout aiHeaderRow = new LinearLayout(context);
        aiHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
        aiHeaderRow.setGravity(Gravity.CENTER_VERTICAL);
        aiHeaderRow.addView(createIconBadge(context, R.drawable.ic_ai_workspace, 0xFFCE93D8, 36));

        LinearLayout aiTitleCol = new LinearLayout(context);
        aiTitleCol.setOrientation(LinearLayout.VERTICAL);
        aiTitleCol.setPadding(dpToPx(context, 12), 0, 0, 0);

        TextView aiTitle = new TextView(context);
        aiTitle.setText("AI Command Workspace");
        aiTitle.setTextSize(14);
        aiTitle.setTypeface(null, Typeface.BOLD);
        aiTitle.setTextColor(0xFFF2F2F5);
        aiTitleCol.addView(aiTitle);

        TextView aiSub = new TextView(context);
        aiSub.setText("Query offline intelligence for 70+ Unix utilities");
        aiSub.setTextSize(11);
        aiSub.setTextColor(0xFF8E8E93);
        aiTitleCol.addView(aiSub);
        aiHeaderRow.addView(aiTitleCol);
        aiInner.addView(aiHeaderRow);

        TextView aiDesc = new TextView(context);
        aiDesc.setText("Decompose shell commands, generate complex syntax, and query offline intelligence for bash, git, ffmpeg, curl, and network tools.");
        aiDesc.setTextSize(12);
        aiDesc.setTextColor(0xFFB0B0B8);
        aiDesc.setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 12));
        aiInner.addView(aiDesc);

        MaterialButton btnOpenAi = new MaterialButton(context);
        btnOpenAi.setText("Open AI Workspace");
        btnOpenAi.setIconResource(R.drawable.ic_ai_workspace);
        btnOpenAi.setIconSize(dpToPx(context, 18));
        btnOpenAi.setBackgroundTintList(ColorStateList.valueOf(0xFF8E24AA));
        btnOpenAi.setCornerRadius(dpToPx(context, 8));
        btnOpenAi.setOnClickListener(v -> {
            dismiss();
            if (mCallback != null) {
                mCallback.onOpenAIWorkspace();
            }
        });
        aiInner.addView(btnOpenAi);
        aiCard.addView(aiInner);
        layout.addView(aiCard);

        // Card 2: Terminal Screen Health Diagnostic
        MaterialCardView diagCard = createCard(context);
        LinearLayout diagInner = createCardInnerLayout(context);

        LinearLayout diagHeaderRow = new LinearLayout(context);
        diagHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
        diagHeaderRow.setGravity(Gravity.CENTER_VERTICAL);
        diagHeaderRow.addView(createIconBadge(context, R.drawable.ic_diagnose, 0xFF80DEEA, 36));

        LinearLayout diagTitleCol = new LinearLayout(context);
        diagTitleCol.setOrientation(LinearLayout.VERTICAL);
        diagTitleCol.setPadding(dpToPx(context, 12), 0, 0, 0);

        TextView diagTitle = new TextView(context);
        diagTitle.setText("Terminal Screen Diagnostic");
        diagTitle.setTextSize(14);
        diagTitle.setTypeface(null, Typeface.BOLD);
        diagTitle.setTextColor(0xFFF2F2F5);
        diagTitleCol.addView(diagTitle);

        TextView diagSub = new TextView(context);
        diagSub.setText("Scan current output for errors and fixes");
        diagSub.setTextSize(11);
        diagSub.setTextColor(0xFF8E8E93);
        diagTitleCol.addView(diagSub);
        diagHeaderRow.addView(diagTitleCol);
        diagInner.addView(diagHeaderRow);

        TextView diagDesc = new TextView(context);
        diagDesc.setText("Detect command not found, permission denied, package manager locks, and git conflicts directly from screen buffer.");
        diagDesc.setTextSize(12);
        diagDesc.setTextColor(0xFFB0B0B8);
        diagDesc.setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 12));
        diagInner.addView(diagDesc);

        LinearLayout diagResultContainer = createBaseVerticalLayout(context);

        MaterialButton btnScan = new MaterialButton(context);
        btnScan.setText("Scan Terminal Screen");
        btnScan.setIconResource(R.drawable.ic_diagnose);
        btnScan.setIconSize(dpToPx(context, 18));
        btnScan.setIconTint(ColorStateList.valueOf(0xFF80DEEA));
        btnScan.setTextColor(0xFF80DEEA);
        btnScan.setBackgroundTintList(ColorStateList.valueOf(0xFF1E2628));
        btnScan.setStrokeColor(ColorStateList.valueOf(0xFF2C3E42));
        btnScan.setStrokeWidth(dpToPx(context, 1));
        btnScan.setCornerRadius(dpToPx(context, 8));
        btnScan.setOnClickListener(v -> {
            diagResultContainer.removeAllViews();
            String transcript = mCallback != null ? mCallback.onCaptureTranscript() : null;
            if (transcript == null || transcript.trim().isEmpty()) {
                TextView empty = new TextView(context);
                empty.setText("Terminal screen is currently empty. Run commands to see diagnostics.");
                empty.setTextSize(12);
                empty.setTextColor(0xFF8E8E93);
                empty.setPadding(0, dpToPx(context, 8), 0, 0);
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
        MaterialCardView resCard = new MaterialCardView(context);
        resCard.setRadius(dpToPx(context, 10));
        resCard.setCardElevation(0);
        resCard.setStrokeWidth(dpToPx(context, 1));
        resCard.setStrokeColor(0xFF2E2E38);
        resCard.setCardBackgroundColor(0xFF131317);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.topMargin = dpToPx(context, 12);
        resCard.setLayoutParams(cardLp);

        LinearLayout resInner = new LinearLayout(context);
        resInner.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(context, 12);
        resInner.setPadding(pad, pad, pad, pad);

        // Status Badge
        TextView badge = new TextView(context);
        badge.setText("DETECTED: " + diag.getCategory().getDisplayName().toUpperCase());
        badge.setTextSize(10);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(0xFFFF5252);
        badge.setBackground(createRoundedBackground(context, 0x22FF5252, 0x50FF5252, 6));
        int padH = dpToPx(context, 8);
        int padV = dpToPx(context, 4);
        badge.setPadding(padH, padV, padH, padV);
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeLp.bottomMargin = dpToPx(context, 8);
        badge.setLayoutParams(badgeLp);
        resInner.addView(badge);

        // Cause
        TextView cause = new TextView(context);
        cause.setText("Root Cause: " + diag.getRootCause());
        cause.setTextSize(12);
        cause.setTypeface(null, Typeface.BOLD);
        cause.setTextColor(0xFFF2F2F5);
        resInner.addView(cause);

        // Remedy
        TextView remedy = new TextView(context);
        remedy.setText("Remedy: " + diag.getRemedyExplanation());
        remedy.setTextSize(12);
        remedy.setTextColor(0xFFB0B0B8);
        remedy.setPadding(0, dpToPx(context, 4), 0, dpToPx(context, 8));
        resInner.addView(remedy);

        if (!diag.getSuggestedFixCommand().isEmpty()) {
            HorizontalScrollView cmdScroll = new HorizontalScrollView(context);
            cmdScroll.setHorizontalScrollBarEnabled(false);
            cmdScroll.setBackground(createRoundedBackground(context, 0xFF09090C, 0xFF24242A, 6));

            LinearLayout cmdLayout = new LinearLayout(context);
            cmdLayout.setOrientation(LinearLayout.HORIZONTAL);
            cmdLayout.setGravity(Gravity.CENTER_VERTICAL);
            cmdLayout.setPadding(dpToPx(context, 12), dpToPx(context, 8), dpToPx(context, 12), dpToPx(context, 8));

            TextView prompt = new TextView(context);
            prompt.setText("$ ");
            prompt.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            prompt.setTextColor(0xFFFF3B30);
            cmdLayout.addView(prompt);

            TextView fixCmd = new TextView(context);
            fixCmd.setText(diag.getSuggestedFixCommand());
            fixCmd.setTypeface(Typeface.MONOSPACE);
            fixCmd.setTextSize(12);
            fixCmd.setTextColor(0xFF80DEEA);
            cmdLayout.addView(fixCmd);

            cmdScroll.addView(cmdLayout);
            resInner.addView(cmdScroll);

            MaterialButton btnFix = new MaterialButton(context);
            btnFix.setText("Execute Fix");
            btnFix.setIconResource(R.drawable.ic_play);
            btnFix.setIconSize(dpToPx(context, 14));
            btnFix.setBackgroundTintList(ColorStateList.valueOf(0xFFFF3B30));
            btnFix.setCornerRadius(dpToPx(context, 6));
            LinearLayout.LayoutParams fixLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fixLp.topMargin = dpToPx(context, 8);
            btnFix.setLayoutParams(fixLp);
            btnFix.setOnClickListener(v -> {
                if (mCallback != null) {
                    mCallback.onExecuteCommand(diag.getSuggestedFixCommand());
                    dismiss();
                }
            });
            resInner.addView(btnFix);
        }

        resCard.addView(resInner);
        container.addView(resCard);
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
        row1.addView(createActionCard(context, R.drawable.ic_new_session, 0xFF34C759, "New Session", "Interactive shell", () -> {
            if (mCallback != null) mCallback.onNewSession(false);
            dismiss();
        }));
        row1.addView(createActionCard(context, R.drawable.ic_lock, 0xFFFF9F0A, "Failsafe Shell", "Bypass rc files", () -> {
            if (mCallback != null) mCallback.onNewSession(true);
            dismiss();
        }));
        layout.addView(row1);

        LinearLayout row2 = createGridRow(context);
        row2.addView(createActionCard(context, R.drawable.ic_edit, 0xFFCE93D8, "Rename Session", "Custom window title", () -> {
            if (mCallback != null) mCallback.onRenameSession();
            dismiss();
        }));
        row2.addView(createActionCard(context, R.drawable.ic_drawer, 0xFF90CAF9, "Session Drawer", "Switch active sessions", () -> {
            if (mCallback != null) mCallback.onOpenSessionDrawer();
            dismiss();
        }));
        layout.addView(row2);

        // Full Screen Transcript Card
        MaterialCardView copyCard = createCard(context);
        LinearLayout copyInner = createCardInnerLayout(context);

        LinearLayout copyHeaderRow = new LinearLayout(context);
        copyHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
        copyHeaderRow.setGravity(Gravity.CENTER_VERTICAL);
        copyHeaderRow.addView(createIconBadge(context, R.drawable.ic_copy, 0xFF80DEEA, 36));

        LinearLayout copyTitleCol = new LinearLayout(context);
        copyTitleCol.setOrientation(LinearLayout.VERTICAL);
        copyTitleCol.setPadding(dpToPx(context, 12), 0, 0, 0);

        TextView copyTitle = new TextView(context);
        copyTitle.setText("Terminal Transcript");
        copyTitle.setTextSize(14);
        copyTitle.setTypeface(null, Typeface.BOLD);
        copyTitle.setTextColor(0xFFF2F2F5);
        copyTitleCol.addView(copyTitle);

        TextView copySub = new TextView(context);
        copySub.setText("Export full terminal scrollback buffer");
        copySub.setTextSize(11);
        copySub.setTextColor(0xFF8E8E93);
        copyTitleCol.addView(copySub);
        copyHeaderRow.addView(copyTitleCol);
        copyInner.addView(copyHeaderRow);

        TextView copyDesc = new TextView(context);
        copyDesc.setText("Copies the entire active terminal session output to the Android system clipboard.");
        copyDesc.setTextSize(12);
        copyDesc.setTextColor(0xFFB0B0B8);
        copyDesc.setPadding(0, dpToPx(context, 10), 0, dpToPx(context, 12));
        copyInner.addView(copyDesc);

        MaterialButton btnCopyAll = new MaterialButton(context);
        btnCopyAll.setText("Copy Transcript to Clipboard");
        btnCopyAll.setIconResource(R.drawable.ic_copy);
        btnCopyAll.setIconSize(dpToPx(context, 18));
        btnCopyAll.setIconTint(ColorStateList.valueOf(0xFF80DEEA));
        btnCopyAll.setTextColor(0xFF80DEEA);
        btnCopyAll.setBackgroundTintList(ColorStateList.valueOf(0xFF1E2628));
        btnCopyAll.setStrokeColor(ColorStateList.valueOf(0xFF2C3E42));
        btnCopyAll.setStrokeWidth(dpToPx(context, 1));
        btnCopyAll.setCornerRadius(dpToPx(context, 8));
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
        tv.setTextSize(15);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(0xFFF2F2F5);
        tv.setPadding(0, dpToPx(context, 4), 0, dpToPx(context, 2));
        return tv;
    }

    private TextView createSectionSubtext(Context context, String subtext) {
        TextView tv = new TextView(context);
        tv.setText(subtext);
        tv.setTextSize(11);
        tv.setTextColor(0xFF8E8E93);
        tv.setPadding(0, 0, 0, dpToPx(context, 12));
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
        card.setRadius(dpToPx(context, 14));
        card.setCardElevation(0);
        card.setStrokeWidth(dpToPx(context, 1));
        card.setStrokeColor(0xFF2D2D35);
        card.setCardBackgroundColor(0xFF1B1B20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(context, 10);
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

    private View createActionCard(Context context, int iconRes, int accentColor, String title, String subtitle, Runnable action) {
        MaterialCardView card = new MaterialCardView(context);
        card.setRadius(dpToPx(context, 14));
        card.setCardElevation(0);
        card.setStrokeWidth(dpToPx(context, 1));
        card.setStrokeColor(0xFF2C2C34);
        card.setCardBackgroundColor(0xFF1B1B20);
        card.setRippleColor(ColorStateList.valueOf(0x22FFFFFF));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMarginEnd(dpToPx(context, 4));
        lp.setMarginStart(dpToPx(context, 4));
        lp.bottomMargin = dpToPx(context, 8);
        card.setLayoutParams(lp);

        LinearLayout inner = new LinearLayout(context);
        inner.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(context, 12);
        inner.setPadding(pad, pad, pad, pad);

        // Icon Badge on Top
        View badge = createIconBadge(context, iconRes, accentColor, 34);
        inner.addView(badge);

        // Title
        TextView tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setTextSize(13);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(0xFFF2F2F5);
        tvTitle.setPadding(0, dpToPx(context, 8), 0, 0);
        inner.addView(tvTitle);

        // Subtitle
        TextView tvSub = new TextView(context);
        tvSub.setText(subtitle);
        tvSub.setTextSize(11);
        tvSub.setTextColor(0xFF8E8E93);
        tvSub.setPadding(0, dpToPx(context, 2), 0, 0);
        inner.addView(tvSub);

        card.addView(inner);
        card.setOnClickListener(v -> action.run());
        return card;
    }

    private static View createIconBadge(Context context, int iconRes, int tintColor, int sizeDp) {
        FrameLayout badge = new FrameLayout(context);
        int sizePx = dpToPx(context, sizeDp);
        badge.setLayoutParams(new LinearLayout.LayoutParams(sizePx, sizePx));

        int bgColor = (tintColor & 0x00FFFFFF) | 0x22000000;
        int strokeColor = (tintColor & 0x00FFFFFF) | 0x44000000;
        badge.setBackground(createRoundedBackground(context, bgColor, strokeColor, 10));

        ImageView iv = new ImageView(context);
        int iconSizePx = dpToPx(context, Math.round(sizeDp * 0.55f));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(iconSizePx, iconSizePx);
        lp.gravity = Gravity.CENTER;
        iv.setLayoutParams(lp);
        iv.setImageResource(iconRes);
        iv.setColorFilter(tintColor);
        badge.addView(iv);

        return badge;
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
