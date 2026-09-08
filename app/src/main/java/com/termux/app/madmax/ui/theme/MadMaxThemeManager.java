package com.termux.app.madmax.ui.theme;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.termux.app.madmax.core.MadMaxConstants;
import com.termux.app.madmax.features.MadMaxFeature;
import com.termux.app.madmax.features.MadMaxFeatureRegistry;
import com.termux.shared.logger.Logger;

/**
 * Manages Material 3 theming and Android 12+ Monet dynamic color extraction.
 */
public final class MadMaxThemeManager {

    private static final String LOG_TAG = "MadMaxThemeManager";

    private MadMaxThemeManager() {}

    /**
     * Initializes dynamic color theming for the application if supported and enabled.
     */
    public static void initialize(@NonNull Application application) {
        if (!DynamicColors.isDynamicColorAvailable()) {
            Logger.logInfo(LOG_TAG, "Dynamic colors not available on this device (requires Android 12+).");
            return;
        }

        MadMaxFeatureRegistry registry = MadMaxFeatureRegistry.getInstance(application);
        boolean isDynamicColorsEnabled = registry.isEnabled(MadMaxFeature.MATERIAL_YOU);

        if (isDynamicColorsEnabled) {
            DynamicColorsOptions options = new DynamicColorsOptions.Builder()
                .setPrecondition((activity, theme) -> registry.isEnabled(MadMaxFeature.MATERIAL_YOU))
                .build();
            DynamicColors.applyToActivitiesIfAvailable(application, options);
            Logger.logInfo(LOG_TAG, "Dynamic Monet Colors applied to all activities.");
        }
    }
}
