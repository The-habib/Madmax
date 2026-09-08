package com.termux.app.madmax.ai.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Model representing a GitHub repository item in the workspace dashboard.
 */
public class GitHubRepoItem {

    private final String mName;
    private final String mFullName;
    private final String mDescription;
    private final String mDefaultBranch;
    private final String mHtmlUrl;
    private final String mCloneUrl;
    private final boolean mIsPrivate;
    private final int mStars;
    private final String mLastCommit;

    public GitHubRepoItem(@NonNull String name,
                          @NonNull String fullName,
                          @NonNull String description,
                          @NonNull String defaultBranch,
                          @NonNull String htmlUrl,
                          @NonNull String cloneUrl,
                          boolean isPrivate,
                          int stars,
                          @Nullable String lastCommit) {
        this.mName = name;
        this.mFullName = fullName;
        this.mDescription = description;
        this.mDefaultBranch = defaultBranch;
        this.mHtmlUrl = htmlUrl;
        this.mCloneUrl = cloneUrl;
        this.mIsPrivate = isPrivate;
        this.mStars = stars;
        this.mLastCommit = lastCommit != null ? lastCommit : "";
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getFullName() {
        return mFullName;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public String getDefaultBranch() {
        return mDefaultBranch;
    }

    @NonNull
    public String getHtmlUrl() {
        return mHtmlUrl;
    }

    @NonNull
    public String getCloneUrl() {
        return mCloneUrl;
    }

    public boolean isPrivate() {
        return mIsPrivate;
    }

    public int getStars() {
        return mStars;
    }

    @NonNull
    public String getLastCommit() {
        return mLastCommit;
    }
}
