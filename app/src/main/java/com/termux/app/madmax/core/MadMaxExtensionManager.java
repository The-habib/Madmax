package com.termux.app.madmax.core;

import android.app.Application;

import androidx.annotation.NonNull;

import com.termux.app.madmax.features.MadMaxFeatureRegistry;
import com.termux.app.madmax.ui.theme.MadMaxThemeManager;
import com.termux.shared.logger.Logger;

/**
 * Main entry point for the MadMax Extension Layer.
 * Initialized during Application.onCreate().
 */
public final class MadMaxExtensionManager {

    private static final String LOG_TAG = "MadMaxExtensionManager";
    private static boolean sInitialized = false;

    private MadMaxExtensionManager() {}

    /**
     * Initializes all MadMax extension subsystems.
     */
    public static synchronized void init(@NonNull Application application) {
        if (sInitialized) return;

        Logger.logInfo(LOG_TAG, "Initializing MadMax Extension Layer v" + MadMaxConstants.MADMAX_VERSION);

        // 1. Initialize Feature Flags Registry
        MadMaxFeatureRegistry.getInstance(application);

        // 2. Initialize Material 3 / Dynamic Colors
        MadMaxThemeManager.initialize(application);

        // 3. Initialize AI Workspace Manager
        com.termux.app.madmax.ai.core.AIWorkspaceManager.initialize(application);

        sInitialized = true;
        Logger.logInfo(LOG_TAG, "MadMax Extension Layer initialized successfully.");
    }
}
