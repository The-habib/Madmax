package com.termux.app.madmax.ai.services;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.app.madmax.ai.model.GitHubRepoItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages GitHub workspace intelligence, local repo detection, and quick shortcuts.
 */
public class GitHubWorkspaceManager {

    private static volatile GitHubWorkspaceManager sInstance;
    private final Context mContext;

    private GitHubWorkspaceManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static GitHubWorkspaceManager getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (GitHubWorkspaceManager.class) {
                if (sInstance == null) {
                    sInstance = new GitHubWorkspaceManager(context);
                }
            }
        }
        return sInstance;
    }

    @NonNull
    public List<GitHubRepoItem> getPopularRepositories() {
        List<GitHubRepoItem> list = new ArrayList<>();
        list.add(new GitHubRepoItem(
            "Madmax",
            "The-habib/Madmax",
            "Production-grade, AI-native fork of Termux with Material 3 theming.",
            "dev",
            "https://github.com/The-habib/Madmax",
            "https://github.com/The-habib/Madmax.git",
            false,
            42,
            "f0b9ad94"
        ));
        list.add(new GitHubRepoItem(
            "termux-packages",
            "termux/termux-packages",
            "Build scripts and patches for packages available in Termux.",
            "master",
            "https://github.com/termux/termux-packages",
            "https://github.com/termux/termux-packages.git",
            false,
            12400,
            "HEAD"
        ));
        list.add(new GitHubRepoItem(
            "termux-api",
            "termux/termux-api",
            "Termux add-on app exposing Android APIs to commandline scripts.",
            "master",
            "https://github.com/termux/termux-api",
            "https://github.com/termux/termux-api.git",
            false,
            3800,
            "HEAD"
        ));
        return Collections.unmodifiableList(list);
    }

    @NonNull
    public String generateCloneCommand(@NonNull String repoUrl) {
        return "git clone " + repoUrl;
    }

    @NonNull
    public String generateCodespaceLaunchUrl(@NonNull String repoFullName) {
        return "https://github.com/codespaces/new?repo=" + repoFullName;
    }

    @Nullable
    public String detectLocalGitBranch(@NonNull String directoryPath) {
        File headFile = new File(directoryPath, ".git/HEAD");
        if (!headFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(headFile))) {
            String line = reader.readLine();
            if (line != null && line.startsWith("ref: refs/heads/")) {
                return line.substring("ref: refs/heads/".length()).trim();
            } else if (line != null) {
                return line.substring(0, Math.min(line.length(), 7));
            }
        } catch (Exception ignored) {}
        return null;
    }
}
