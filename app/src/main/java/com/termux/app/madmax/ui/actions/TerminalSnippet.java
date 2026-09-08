package com.termux.app.madmax.ui.actions;

import androidx.annotation.NonNull;

/**
 * Model representing a reusable terminal snippet or shortcut command.
 */
public class TerminalSnippet {

    private final String mCategory;
    private final String mTitle;
    private final String mDescription;
    private final String mCommand;
    private final boolean mIsSafeToRunDirectly;

    public TerminalSnippet(@NonNull String category,
                           @NonNull String title,
                           @NonNull String description,
                           @NonNull String command,
                           boolean isSafeToRunDirectly) {
        this.mCategory = category;
        this.mTitle = title;
        this.mDescription = description;
        this.mCommand = command;
        this.mIsSafeToRunDirectly = isSafeToRunDirectly;
    }

    @NonNull
    public String getCategory() {
        return mCategory;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public String getCommand() {
        return mCommand;
    }

    public boolean isSafeToRunDirectly() {
        return mIsSafeToRunDirectly;
    }
}
