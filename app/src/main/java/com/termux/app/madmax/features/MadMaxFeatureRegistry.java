package com.termux.app.madmax.features;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.termux.app.madmax.core.MadMaxConstants;
import com.termux.shared.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe feature registry managing runtime feature toggles and flags in MadMax.
 */
public class MadMaxFeatureRegistry {

    private static final String LOG_TAG = "MadMaxFeatureRegistry";
    private static volatile MadMaxFeatureRegistry sInstance;

    private final SharedPreferences mPrefs;
    private final ConcurrentHashMap<String, Boolean> mFeatureCache = new ConcurrentHashMap<>();
    private final List<FeatureFlagListener> mListeners = Collections.synchronizedList(new ArrayList<>());

    private MadMaxFeatureRegistry(@NonNull Context context) {
        mPrefs = context.getApplicationContext().getSharedPreferences(
            MadMaxConstants.MADMAX_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        );
        loadCache();
    }

    public static MadMaxFeatureRegistry getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (MadMaxFeatureRegistry.class) {
                if (sInstance == null) {
                    sInstance = new MadMaxFeatureRegistry(context);
                }
            }
        }
        return sInstance;
    }

    private void loadCache() {
        for (MadMaxFeature feature : MadMaxFeature.values()) {
            String key = getPrefKey(feature);
            boolean isEnabled = mPrefs.getBoolean(key, feature.getDefaultValue());
            mFeatureCache.put(feature.getKey(), isEnabled);
        }
    }

    private String getPrefKey(MadMaxFeature feature) {
        return MadMaxConstants.PREF_FEATURE_FLAG_PREFIX + feature.getKey();
    }

    public boolean isEnabled(@NonNull MadMaxFeature feature) {
        Boolean val = mFeatureCache.get(feature.getKey());
        if (val != null) {
            return val;
        }
        boolean isEnabled = mPrefs.getBoolean(getPrefKey(feature), feature.getDefaultValue());
        mFeatureCache.put(feature.getKey(), isEnabled);
        return isEnabled;
    }

    public void setEnabled(@NonNull MadMaxFeature feature, boolean enabled) {
        mFeatureCache.put(feature.getKey(), enabled);
        mPrefs.edit().putBoolean(getPrefKey(feature), enabled).apply();
        Logger.logInfo(LOG_TAG, "Feature [" + feature.getKey() + "] set to " + enabled);
        notifyListeners(feature, enabled);
    }

    public void addListener(@NonNull FeatureFlagListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeListener(@NonNull FeatureFlagListener listener) {
        mListeners.remove(listener);
    }

    private void notifyListeners(MadMaxFeature feature, boolean isEnabled) {
        synchronized (mListeners) {
            for (FeatureFlagListener listener : mListeners) {
                try {
                    listener.onFeatureStateChanged(feature, isEnabled);
                } catch (Exception e) {
                    Logger.logError(LOG_TAG, "Error notifying feature flag listener: " + e.getMessage());
                }
            }
        }
    }
}
