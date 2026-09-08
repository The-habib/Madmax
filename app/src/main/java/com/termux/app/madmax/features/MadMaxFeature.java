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

    AI_WORKSPACE(
        "ai_workspace",
        "AI Command Workspace",
        "Interactive Material 3 AI assistant panel for command intelligence.",
        true,
        RiskLevel.LOW
    ),

    COMMAND_EXPLAIN(
        "command_explain",
        "Command Explanation",
        "Detailed binary and flag decomposition with safety risk assessment.",
        true,
        RiskLevel.LOW
    ),

    COMMAND_GENERATE(
        "command_generate",
        "Natural Language Command Generation",
        "Generate bash commands from plain English prompts.",
        true,
        RiskLevel.LOW
    ),

    ERROR_ANALYZER(
        "error_analyzer",
        "Automated Error Diagnosis",
        "Intelligent terminal stderr classification and 1-tap safe remediation.",
        true,
        RiskLevel.LOW
    ),

    GITHUB_WORKSPACE(
        "github_workspace",
        "GitHub Workspace Hub",
        "Repository manager, branch tracker, quick clone, and Codespaces launcher.",
        true,
        RiskLevel.LOW
    ),

    PLUGINS_HUB(
        "plugins_hub",
        "Modular Plugin System (Phase 3 Preview)",
        "Decoupled extension plugin runner and service bridge.",
        false,
        RiskLevel.MODERATE
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
