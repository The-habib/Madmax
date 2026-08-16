package com.termux.app.madmax.features;

import androidx.annotation.NonNull;

/**
 * Definition of all dynamic features and module flags available in MadMax.
 */
public enum MadMaxFeature {

    MATERIAL_YOU(
        "material_you",
        "Material You Dynamic Colors",
        "Extract dynamic color palettes from Android 12+ wallpaper.",
        true,
        RiskLevel.LOW
    ),

    MODERN_DRAWER(
        "modern_drawer",
        "Material 3 Session Drawer",
        "Enhanced visual session list with process status badges.",
        true,
        RiskLevel.LOW
    ),

    DEVELOPER_DASHBOARD(
        "developer_dashboard",
        "Developer Diagnostics Dashboard",
        "Hardware, PTY, memory, and SDK diagnostic inspector.",
        true,
        RiskLevel.LOW
    ),

    AI_ASSISTANT(
        "ai_assistant",
        "AI Terminal Assistant (Phase 2 Preview)",
        "Contextual CLI command assistance and error diagnostics.",
        false,
        RiskLevel.MODERATE
    ),

    PLUGINS_HUB(
        "plugins_hub",
        "Modular Plugin System (Phase 3 Preview)",
        "Decoupled extension plugin runner and service bridge.",
        false,
        RiskLevel.MODERATE
    ),

    GITHUB_SYNC(
        "github_sync",
        "GitHub Workspace Sync (Phase 2 Preview)",
        "Integrated GitHub CLI workspace and repo management.",
        false,
        RiskLevel.LOW
    );

    public enum RiskLevel {
        LOW,
        MODERATE,
        HIGH
    }

    private final String mKey;
    private final String mTitle;
    private final String mDescription;
    private final boolean mDefaultValue;
    private final RiskLevel mRiskLevel;

    MadMaxFeature(@NonNull String key, @NonNull String title, @NonNull String description, boolean defaultValue, @NonNull RiskLevel riskLevel) {
        this.mKey = key;
        this.mTitle = title;
        this.mDescription = description;
        this.mDefaultValue = defaultValue;
        this.mRiskLevel = riskLevel;
    }

    @NonNull
    public String getKey() {
        return mKey;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    public boolean getDefaultValue() {
        return mDefaultValue;
    }

    @NonNull
    public RiskLevel getRiskLevel() {
        return mRiskLevel;
    }
}
