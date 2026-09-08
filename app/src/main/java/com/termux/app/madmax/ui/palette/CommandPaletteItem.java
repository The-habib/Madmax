package com.termux.app.madmax.ui.palette;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * Model representing an interactive action in the MadMax Command Palette.
 */
public class CommandPaletteItem {

    public enum Category {
        ACTION("ACTION", 0xFF80DEEA),
        AI("AI", 0xFFCE93D8),
        SNIPPET("SNIPPET", 0xFFFFD54F),
        SESSION("SESSION", 0xFF34C759),
        SETTINGS("SETTINGS", 0xFFB0BEC5);

        private final String label;
        private final int color;

        Category(String label, int color) {
            this.label = label;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public int getColor() {
            return color;
        }
    }

    private final String id;
    private final String title;
    private final String subtitle;
    private final Category category;
    private final int iconRes;
    private final Runnable action;

    public CommandPaletteItem(@NonNull String id,
                              @NonNull String title,
                              @NonNull String subtitle,
                              @NonNull Category category,
                              @DrawableRes int iconRes,
                              @NonNull Runnable action) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.iconRes = iconRes;
        this.action = action;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getSubtitle() {
        return subtitle;
    }

    @NonNull
    public Category getCategory() {
        return category;
    }

    @DrawableRes
    public int getIconRes() {
        return iconRes;
    }

    @NonNull
    public Runnable getAction() {
        return action;
    }

    public boolean matches(@NonNull String query) {
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) return true;
        return title.toLowerCase().contains(q)
            || subtitle.toLowerCase().contains(q)
            || category.getLabel().toLowerCase().contains(q);
    }
}
