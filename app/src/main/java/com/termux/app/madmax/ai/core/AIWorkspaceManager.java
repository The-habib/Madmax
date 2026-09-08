package com.termux.app.madmax.ai.core;

import android.content.Context;
import androidx.annotation.NonNull;

import com.termux.app.madmax.ai.services.AICommandService;
import com.termux.app.madmax.ai.services.AIHistoryManager;
import com.termux.app.madmax.ai.services.GitHubWorkspaceManager;

/**
 * Main coordinator for MadMax AI Workspace subsystems.
 */
public class AIWorkspaceManager {

    private static volatile AIWorkspaceManager sInstance;

    private final Context mContext;
    private final AICommandService mCommandService;
    private final AIHistoryManager mHistoryManager;
    private final GitHubWorkspaceManager mGitHubWorkspaceManager;

    private AIWorkspaceManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mCommandService = AICommandService.getInstance(mContext);
        this.mHistoryManager = AIHistoryManager.getInstance(mContext);
        this.mGitHubWorkspaceManager = GitHubWorkspaceManager.getInstance(mContext);
    }

    public static AIWorkspaceManager getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (AIWorkspaceManager.class) {
                if (sInstance == null) {
                    sInstance = new AIWorkspaceManager(context);
                }
            }
        }
        return sInstance;
    }

    public static void initialize(@NonNull Context context) {
        getInstance(context);
    }

    @NonNull
    public AICommandService getCommandService() {
        return mCommandService;
    }

    @NonNull
    public AIHistoryManager getHistoryManager() {
        return mHistoryManager;
    }

    @NonNull
    public GitHubWorkspaceManager getGitHubWorkspaceManager() {
        return mGitHubWorkspaceManager;
    }
}
