package com.termux.app.madmax.features;

import androidx.annotation.NonNull;

/**
 * Listener callback interface triggered when a feature flag state changes.
 */
public interface FeatureFlagListener {
    void onFeatureStateChanged(@NonNull MadMaxFeature feature, boolean isEnabled);
}
