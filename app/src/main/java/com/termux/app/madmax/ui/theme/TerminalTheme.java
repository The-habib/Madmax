package com.termux.app.madmax.ui.theme;

import androidx.annotation.NonNull;

import java.util.Properties;

/**
 * Encapsulates a complete 16-color ANSI terminal theme + background, foreground, and cursor.
 */
public class TerminalTheme {

    private final String id;
    private final String displayName;
    private final String description;
    private final String background;
    private final String foreground;
    private final String cursor;
    private final String[] ansiColors; // 16 colors: 0..15

    public TerminalTheme(@NonNull String id,
                         @NonNull String displayName,
                         @NonNull String description,
                         @NonNull String background,
                         @NonNull String foreground,
                         @NonNull String cursor,
                         @NonNull String[] ansiColors) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.background = background;
        this.foreground = foreground;
        this.cursor = cursor;
        this.ansiColors = ansiColors;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getBackground() {
        return background;
    }

    @NonNull
    public String getForeground() {
        return foreground;
    }

    @NonNull
    public String getCursor() {
        return cursor;
    }

    @NonNull
    public String[] getAnsiColors() {
        return ansiColors;
    }

    @NonNull
    public String getAnsiColor(int index) {
        if (ansiColors != null && index >= 0 && index < ansiColors.length) {
            return ansiColors[index];
        }
        return foreground;
    }

    @NonNull
    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("background", background);
        props.setProperty("foreground", foreground);
        props.setProperty("cursor", cursor);
        for (int i = 0; i < ansiColors.length && i < 16; i++) {
            props.setProperty("color" + i, ansiColors[i]);
        }
        return props;
    }
}
