package com.termux.app.madmax.ui.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.termux.R;
import com.termux.app.TermuxActivity;

import java.util.List;

/**
 * Material 3 Bottom Sheet for live-previewing and switching terminal themes.
 */
public class TerminalThemeBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "TerminalThemeBottomSheet";

    private final List<TerminalTheme> mThemes = TerminalThemeCatalog.getAllThemes();
    private String mActiveThemeId;
    private ThemeAdapter mAdapter;

    public static TerminalThemeBottomSheet newInstance() {
        return new TerminalThemeBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_terminal_themes, container, false);

        ListView listView = root.findViewById(R.id.themes_list_view);
        ImageButton closeBtn = root.findViewById(R.id.btn_close_themes);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> dismiss());
        }

        mActiveThemeId = TerminalThemeApplicator.getActiveThemeId(requireContext());

        mAdapter = new ThemeAdapter();
        listView.setAdapter(mAdapter);

        return root;
    }

    private class ThemeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mThemes.size();
        }

        @Override
        public TerminalTheme getItem(int position) {
            return mThemes.get(position);
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_terminal_theme, parent, false);
            }

            TerminalTheme theme = getItem(position);
            boolean isActive = theme.getId().equalsIgnoreCase(mActiveThemeId);

            MaterialCardView card = view.findViewById(R.id.theme_item_card);
            FrameLayout previewBox = view.findViewById(R.id.theme_preview_box);
            TextView previewText1 = view.findViewById(R.id.theme_preview_text1);
            TextView previewText2 = view.findViewById(R.id.theme_preview_text2);
            TextView titleView = view.findViewById(R.id.theme_item_title);
            TextView descView = view.findViewById(R.id.theme_item_desc);
            TextView activeBadge = view.findViewById(R.id.theme_active_badge);
            ImageView selectedIcon = view.findViewById(R.id.theme_selected_icon);

            if (titleView != null) titleView.setText(theme.getDisplayName());
            if (descView != null) descView.setText(theme.getDescription());

            if (activeBadge != null) {
                activeBadge.setVisibility(isActive ? View.VISIBLE : View.GONE);
            }

            if (selectedIcon != null) {
                selectedIcon.setVisibility(isActive ? View.VISIBLE : View.GONE);
            }

            // Card highlight
            if (card != null) {
                card.setStrokeColor(isActive ? Color.parseColor("#FF3B30") : Color.parseColor("#24242C"));
                card.setStrokeWidth(dpToPx(parent.getContext(), isActive ? 1.5f : 1.0f));

                card.setOnClickListener(v -> {
                    if (getActivity() instanceof TermuxActivity) {
                        TerminalThemeApplicator.applyTheme((TermuxActivity) getActivity(), theme);
                        mActiveThemeId = theme.getId();
                        notifyDataSetChanged();
                    }
                });
            }

            // Preview Box styling
            if (previewBox != null) {
                GradientDrawable boxBg = new GradientDrawable();
                boxBg.setShape(GradientDrawable.RECTANGLE);
                boxBg.setCornerRadius(dpToPx(parent.getContext(), 6));
                try {
                    boxBg.setColor(Color.parseColor(theme.getBackground()));
                } catch (Exception ignored) {
                    boxBg.setColor(Color.BLACK);
                }
                boxBg.setStroke(dpToPx(parent.getContext(), 1), Color.parseColor("#33333E"));
                previewBox.setBackground(boxBg);
            }

            if (previewText1 != null) {
                try {
                    // ANSI Green or cursor color for prompt
                    previewText1.setTextColor(Color.parseColor(theme.getAnsiColor(2)));
                } catch (Exception ignored) {
                    previewText1.setTextColor(Color.GREEN);
                }
            }

            if (previewText2 != null) {
                try {
                    previewText2.setTextColor(Color.parseColor(theme.getForeground()));
                } catch (Exception ignored) {
                    previewText2.setTextColor(Color.WHITE);
                }
            }

            // Swatches
            int[] swatchIds = new int[]{
                R.id.swatch_1, R.id.swatch_2, R.id.swatch_3,
                R.id.swatch_4, R.id.swatch_5, R.id.swatch_6
            };
            int[] ansiIndices = new int[]{1, 2, 3, 4, 5, 6}; // Red, Green, Yellow, Blue, Magenta, Cyan

            for (int i = 0; i < swatchIds.length; i++) {
                View swatch = view.findViewById(swatchIds[i]);
                if (swatch != null) {
                    GradientDrawable circle = new GradientDrawable();
                    circle.setShape(GradientDrawable.OVAL);
                    try {
                        circle.setColor(Color.parseColor(theme.getAnsiColor(ansiIndices[i])));
                    } catch (Exception ignored) {
                        circle.setColor(Color.GRAY);
                    }
                    swatch.setBackground(circle);
                }
            }

            return view;
        }
    }

    private static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
