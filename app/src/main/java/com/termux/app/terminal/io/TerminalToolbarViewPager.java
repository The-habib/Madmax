package com.termux.app.terminal.io;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.shared.termux.extrakeys.ExtraKeysView;
import com.termux.terminal.TerminalSession;

public class TerminalToolbarViewPager {

    public static class PageAdapter extends PagerAdapter {

        final TermuxActivity mActivity;
        String mSavedTextInput;

        public PageAdapter(TermuxActivity activity, String savedTextInput) {
            this.mActivity = activity;
            this.mSavedTextInput = savedTextInput;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View layout;
            if (position == 0) {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_extra_keys, collection, false);
                ExtraKeysView extraKeysView = (ExtraKeysView) layout;
                extraKeysView.setExtraKeysViewClient(mActivity.getTermuxTerminalExtraKeys());
                extraKeysView.setButtonTextAllCaps(mActivity.getProperties().shouldExtraKeysTextBeAllCaps());
                mActivity.setExtraKeysView(extraKeysView);
                extraKeysView.reload(mActivity.getTermuxTerminalExtraKeys().getExtraKeysInfo(),
                    mActivity.getTerminalToolbarDefaultHeight());

                // apply extra keys fix if enabled in prefs
                if (mActivity.getProperties().isUsingFullScreen() && mActivity.getProperties().isUsingFullScreenWorkAround()) {
                    FullScreenWorkAround.apply(mActivity);
                }

            } else if (position == 1) {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_quick_actions, collection, false);
                setupQuickActionsToolbar(layout);
            } else {
                layout = inflater.inflate(R.layout.view_terminal_toolbar_text_input, collection, false);
                final EditText editText = layout.findViewById(R.id.terminal_toolbar_text_input);

                if (mSavedTextInput != null) {
                    editText.setText(mSavedTextInput);
                    mSavedTextInput = null;
                }

                editText.setOnEditorActionListener((v, actionId, event) -> {
                    TerminalSession session = mActivity.getCurrentSession();
                    if (session != null) {
                        if (session.isRunning()) {
                            String textToSend = editText.getText().toString();
                            if (textToSend.length() == 0) textToSend = "\r";
                            session.write(textToSend);
                        } else {
                            mActivity.getTermuxTerminalSessionClient().removeFinishedSession(session);
                        }
                        editText.setText("");
                    }
                    return true;
                });
            }
            collection.addView(layout);
            return layout;
        }

        private void setupQuickActionsToolbar(View layout) {
            View btnMenu = layout.findViewById(R.id.btn_toolbar_quick_menu);
            if (btnMenu != null) {
                btnMenu.setOnClickListener(v -> mActivity.showQuickActionsBottomSheet());
            }

            View btnPalette = layout.findViewById(R.id.btn_toolbar_palette);
            if (btnPalette != null) {
                btnPalette.setOnClickListener(v -> mActivity.showCommandPalette());
            }

            View btnTheme = layout.findViewById(R.id.btn_toolbar_theme);
            if (btnTheme != null) {
                btnTheme.setOnClickListener(v -> mActivity.showTerminalThemesBottomSheet());
            }

            View btnAi = layout.findViewById(R.id.btn_toolbar_ai);
            if (btnAi != null) {
                btnAi.setOnClickListener(v -> mActivity.showAIWorkspaceBottomSheet());
            }

            View btnPaste = layout.findViewById(R.id.btn_toolbar_paste);
            if (btnPaste != null) {
                btnPaste.setOnClickListener(v -> {
                    if (mActivity.getTermuxTerminalSessionClient() != null) {
                        mActivity.getTermuxTerminalSessionClient().onPasteTextFromClipboard(null);
                    }
                });
            }

            View btnClear = layout.findViewById(R.id.btn_toolbar_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    TerminalSession session = mActivity.getCurrentSession();
                    if (session != null && session.isRunning()) {
                        session.write("clear\r");
                    }
                });
            }

            View btnCtrlC = layout.findViewById(R.id.btn_toolbar_ctrl_c);
            if (btnCtrlC != null) {
                btnCtrlC.setOnClickListener(v -> {
                    TerminalSession session = mActivity.getCurrentSession();
                    if (session != null && session.isRunning()) {
                        session.write("\u0003");
                    }
                });
            }

            View btnSnippets = layout.findViewById(R.id.btn_toolbar_snippets);
            if (btnSnippets != null) {
                btnSnippets.setOnClickListener(v -> mActivity.showQuickActionsBottomSheet(com.termux.app.madmax.ui.actions.QuickActionsBottomSheet.TAB_SNIPPETS));
            }

            View btnZoomIn = layout.findViewById(R.id.btn_toolbar_zoom_in);
            if (btnZoomIn != null) {
                btnZoomIn.setOnClickListener(v -> {
                    if (mActivity.getTermuxTerminalViewClient() != null) {
                        mActivity.getTermuxTerminalViewClient().changeFontSize(true);
                    }
                });
            }

            View btnZoomOut = layout.findViewById(R.id.btn_toolbar_zoom_out);
            if (btnZoomOut != null) {
                btnZoomOut.setOnClickListener(v -> {
                    if (mActivity.getTermuxTerminalViewClient() != null) {
                        mActivity.getTermuxTerminalViewClient().changeFontSize(false);
                    }
                });
            }

            View btnKbd = layout.findViewById(R.id.btn_toolbar_keyboard);
            if (btnKbd != null) {
                btnKbd.setOnClickListener(v -> {
                    if (mActivity.getTermuxTerminalViewClient() != null) {
                        mActivity.getTermuxTerminalViewClient().onToggleSoftKeyboardRequest();
                    }
                });
            }

            View btnCopy = layout.findViewById(R.id.btn_toolbar_copy_all);
            if (btnCopy != null) {
                btnCopy.setOnClickListener(v -> {
                    TerminalSession session = mActivity.getCurrentSession();
                    if (session != null && session.getEmulator() != null && session.getEmulator().getScreen() != null) {
                        String transcript = session.getEmulator().getScreen().getTranscriptText();
                        if (transcript != null && !transcript.isEmpty()) {
                            android.content.ClipboardManager cm = (android.content.ClipboardManager) mActivity.getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                cm.setPrimaryClip(android.content.ClipData.newPlainText("Terminal Transcript", transcript));
                                android.widget.Toast.makeText(mActivity, "Terminal transcript copied.", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
            collection.removeView((View) view);
        }

    }



    public static class OnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        final TermuxActivity mActivity;
        final ViewPager mTerminalToolbarViewPager;

        public OnPageChangeListener(TermuxActivity activity, ViewPager viewPager) {
            this.mActivity = activity;
            this.mTerminalToolbarViewPager = viewPager;
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0 || position == 1) {
                mActivity.getTerminalView().requestFocus();
            } else {
                final EditText editText = mTerminalToolbarViewPager.findViewById(R.id.terminal_toolbar_text_input);
                if (editText != null) editText.requestFocus();
            }
        }

    }

}
