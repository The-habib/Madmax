package com.termux.app.madmax.ui.palette;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.termux.R;
import com.termux.app.madmax.ui.actions.QuickActionsBottomSheet.TerminalActionCallback;
import com.termux.app.madmax.ui.actions.SnippetCatalog;
import com.termux.app.madmax.ui.actions.TerminalSnippet;

import java.util.ArrayList;
import java.util.List;

/**
 * Spotlight-style Command Palette modal for quick fuzzy searching and executing
 * terminal commands, actions, snippets, and tools.
 */
public class CommandPaletteBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "CommandPaletteBottomSheet";

    private TerminalActionCallback mCallback;
    private final List<CommandPaletteItem> mAllItems = new ArrayList<>();
    private final List<CommandPaletteItem> mFilteredItems = new ArrayList<>();
    private PaletteAdapter mAdapter;
    private TextView mEmptyView;
    private ImageButton mClearQueryBtn;

    public static CommandPaletteBottomSheet newInstance() {
        return new CommandPaletteBottomSheet();
    }

    public void setActionCallback(@Nullable TerminalActionCallback callback) {
        this.mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_command_palette, container, false);

        ListView listView = root.findViewById(R.id.palette_list_view);
        EditText searchInput = root.findViewById(R.id.palette_search_input);
        mEmptyView = root.findViewById(R.id.palette_empty_view);
        mClearQueryBtn = root.findViewById(R.id.btn_palette_clear_query);

        ImageButton closeBtn = root.findViewById(R.id.btn_close_palette);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> dismiss());
        }

        buildItemsList();
        mFilteredItems.clear();
        mFilteredItems.addAll(mAllItems);

        mAdapter = new PaletteAdapter();
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < mFilteredItems.size()) {
                CommandPaletteItem item = mFilteredItems.get(position);
                dismiss();
                item.getAction().run();
            }
        });

        if (mClearQueryBtn != null) {
            mClearQueryBtn.setOnClickListener(v -> searchInput.setText(""));
        }

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s != null ? s.toString().trim() : "";
                if (mClearQueryBtn != null) {
                    mClearQueryBtn.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                }
                filterItems(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return root;
    }

    private void filterItems(@NonNull String query) {
        mFilteredItems.clear();
        for (CommandPaletteItem item : mAllItems) {
            if (item.matches(query)) {
                mFilteredItems.add(item);
            }
        }
        mAdapter.notifyDataSetChanged();
        if (mEmptyView != null) {
            mEmptyView.setVisibility(mFilteredItems.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void buildItemsList() {
        mAllItems.clear();

        // 1. Core Terminal Actions
        mAllItems.add(new CommandPaletteItem("action_paste", "Paste from Clipboard", "Insert text currently stored in clipboard", CommandPaletteItem.Category.ACTION, R.drawable.ic_paste, () -> {
            if (mCallback != null) mCallback.onPasteFromClipboard();
        }));

        mAllItems.add(new CommandPaletteItem("action_clear", "Clear Terminal Screen", "Wipe active terminal display buffer", CommandPaletteItem.Category.ACTION, R.drawable.ic_clear_all, () -> {
            if (mCallback != null) mCallback.onClearScreen();
        }));

        mAllItems.add(new CommandPaletteItem("action_ctrl_c", "Interrupt Process (^C)", "Send SIGINT signal to stop current process", CommandPaletteItem.Category.ACTION, R.drawable.ic_stop, () -> {
            if (mCallback != null) mCallback.onSendInterrupt();
        }));

        mAllItems.add(new CommandPaletteItem("action_ctrl_z", "Suspend Process (^Z)", "Send SIGTSTP signal to suspend process to background", CommandPaletteItem.Category.ACTION, R.drawable.ic_pause, () -> {
            if (mCallback != null) mCallback.onSendSuspend();
        }));

        mAllItems.add(new CommandPaletteItem("action_eof", "Send EOF (^D)", "Send End-of-File or exit shell", CommandPaletteItem.Category.ACTION, R.drawable.ic_exit, () -> {
            if (mCallback != null) mCallback.onSendEof();
        }));

        mAllItems.add(new CommandPaletteItem("action_reset", "Reset Terminal State", "Reinitialize broken terminal escape codes", CommandPaletteItem.Category.ACTION, R.drawable.ic_reset, () -> {
            if (mCallback != null) mCallback.onResetTerminal();
        }));

        mAllItems.add(new CommandPaletteItem("action_scroll_top", "Scroll to Buffer Top", "Jump to top of terminal scrollback", CommandPaletteItem.Category.ACTION, R.drawable.ic_scroll_top, () -> {
            if (mCallback != null) mCallback.onScrollToTop();
        }));

        mAllItems.add(new CommandPaletteItem("action_scroll_bottom", "Scroll to Buffer Bottom", "Jump to active prompt and latest output", CommandPaletteItem.Category.ACTION, R.drawable.ic_scroll_bottom, () -> {
            if (mCallback != null) mCallback.onScrollToBottom();
        }));

        // 2. Accessibility & Display Actions
        mAllItems.add(new CommandPaletteItem("action_font_in", "Increase Font Size (A+)", "Scale up terminal text size for readability", CommandPaletteItem.Category.ACTION, R.drawable.ic_zoom_in, () -> {
            if (mCallback != null) mCallback.onChangeFontSize(true);
        }));

        mAllItems.add(new CommandPaletteItem("action_font_out", "Decrease Font Size (A-)", "Scale down terminal text size", CommandPaletteItem.Category.ACTION, R.drawable.ic_zoom_out, () -> {
            if (mCallback != null) mCallback.onChangeFontSize(false);
        }));

        mAllItems.add(new CommandPaletteItem("action_font_reset", "Reset Font Size", "Restore default terminal font size (14 pt)", CommandPaletteItem.Category.ACTION, R.drawable.ic_reset, () -> {
            if (mCallback != null) mCallback.onResetFontSize();
        }));

        mAllItems.add(new CommandPaletteItem("action_keyboard", "Toggle Software Keyboard", "Show or hide virtual on-screen keyboard", CommandPaletteItem.Category.ACTION, R.drawable.ic_keyboard, () -> {
            if (mCallback != null) mCallback.onToggleKeyboard();
        }));

        mAllItems.add(new CommandPaletteItem("action_wakelock", "Toggle CPU WakeLock", "Prevent Android CPU sleep during long jobs", CommandPaletteItem.Category.ACTION, R.drawable.ic_bolt, () -> {
            if (mCallback != null) mCallback.onToggleWakeLock();
        }));

        mAllItems.add(new CommandPaletteItem("action_transcript", "Copy Terminal Transcript", "Export entire screen scrollback to clipboard", CommandPaletteItem.Category.ACTION, R.drawable.ic_copy, () -> {
            if (mCallback != null) {
                String transcript = mCallback.onCaptureTranscript();
                if (transcript != null && !transcript.isEmpty() && getContext() != null) {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("Terminal Transcript", transcript));
                        Toast.makeText(getContext(), "Transcript copied.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }));

        // 3. AI & Diagnostics
        mAllItems.add(new CommandPaletteItem("ai_workspace", "AI Command Workspace", "Offline command intelligence, explain flags & synthesize scripts", CommandPaletteItem.Category.AI, R.drawable.ic_ai_workspace, () -> {
            if (mCallback != null) mCallback.onOpenAIWorkspace();
        }));

        // 4. Session Management
        mAllItems.add(new CommandPaletteItem("session_new", "New Terminal Session", "Launch a new interactive shell session", CommandPaletteItem.Category.SESSION, R.drawable.ic_new_session, () -> {
            if (mCallback != null) mCallback.onNewSession(false);
        }));

        mAllItems.add(new CommandPaletteItem("session_failsafe", "New Failsafe Session", "Launch failsafe shell without dotfiles", CommandPaletteItem.Category.SESSION, R.drawable.ic_lock, () -> {
            if (mCallback != null) mCallback.onNewSession(true);
        }));

        mAllItems.add(new CommandPaletteItem("session_rename", "Rename Current Session", "Assign custom label to active terminal window", CommandPaletteItem.Category.SESSION, R.drawable.ic_edit, () -> {
            if (mCallback != null) mCallback.onRenameSession();
        }));

        mAllItems.add(new CommandPaletteItem("session_drawer", "Open Session Drawer", "Open drawer to view and switch active sessions", CommandPaletteItem.Category.SESSION, R.drawable.ic_drawer, () -> {
            if (mCallback != null) mCallback.onOpenSessionDrawer();
        }));

        // 5. Curated CLI Snippets
        for (TerminalSnippet s : SnippetCatalog.getAllSnippets()) {
            mAllItems.add(new CommandPaletteItem("snip_" + s.getTitle().toLowerCase().replace(" ", "_"), s.getTitle(), s.getCommand(), CommandPaletteItem.Category.SNIPPET, R.drawable.ic_snippets, () -> {
                if (mCallback != null) mCallback.onExecuteCommand(s.getCommand());
            }));
        }
    }

    private class PaletteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFilteredItems.size();
        }

        @Override
        public CommandPaletteItem getItem(int position) {
            return mFilteredItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_command_palette, parent, false);
            }

            CommandPaletteItem item = getItem(position);

            MaterialCardView card = view.findViewById(R.id.palette_item_card);
            FrameLayout iconBadge = view.findViewById(R.id.palette_icon_badge);
            ImageView iconView = view.findViewById(R.id.palette_item_icon);
            TextView titleView = view.findViewById(R.id.palette_item_title);
            TextView subtitleView = view.findViewById(R.id.palette_item_subtitle);
            TextView categoryView = view.findViewById(R.id.palette_item_category);

            if (titleView != null) titleView.setText(item.getTitle());
            if (subtitleView != null) subtitleView.setText(item.getSubtitle());

            if (categoryView != null) {
                categoryView.setText(item.getCategory().getLabel());
                categoryView.setTextColor(item.getCategory().getColor());
                categoryView.setBackground(createCategoryPill(parent.getContext(), item.getCategory().getColor()));
            }

            if (iconView != null) {
                iconView.setImageResource(item.getIconRes());
                iconView.setColorFilter(item.getCategory().getColor());
            }

            if (iconBadge != null) {
                iconBadge.setBackground(createIconBadgeBackground(parent.getContext(), item.getCategory().getColor()));
            }

            if (card != null) {
                card.setOnClickListener(v -> {
                    dismiss();
                    item.getAction().run();
                });
            }

            return view;
        }
    }

    private static GradientDrawable createCategoryPill(Context context, int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(dpToPx(context, 4));
        gd.setColor((color & 0x00FFFFFF) | 0x20000000);
        gd.setStroke(dpToPx(context, 1), (color & 0x00FFFFFF) | 0x40000000);
        return gd;
    }

    private static GradientDrawable createIconBadgeBackground(Context context, int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(dpToPx(context, 8));
        gd.setColor((color & 0x00FFFFFF) | 0x18000000);
        gd.setStroke(dpToPx(context, 1), (color & 0x00FFFFFF) | 0x30000000);
        return gd;
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
