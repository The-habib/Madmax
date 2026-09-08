package com.termux.app.madmax.ai.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Detailed explanation model for a shell command.
 */
public class AICommandExplanation {

    public enum RiskLevel {
        SAFE("Safe", "Normal operation with minimal risk of data loss.", 0xFF34C759),
        CAUTION("Caution", "Modifies files or network state. Review before executing.", 0xFFFF9500),
        DESTRUCTIVE("Destructive", "Potentially hazardous command that can overwrite or delete data.", 0xFFFF3B30);

        private final String mLabel;
        private final String mDescription;
        private final int mColor;

        RiskLevel(@NonNull String label, @NonNull String description, int color) {
            this.mLabel = label;
            this.mDescription = description;
            this.mColor = color;
        }

        @NonNull
        public String getLabel() {
            return mLabel;
        }

        @NonNull
        public String getDescription() {
            return mDescription;
        }

        public int getColor() {
            return mColor;
        }
    }

    private final String mRawCommand;
    private final String mBinaryName;
    private final String mSummary;
    private final Map<String, String> mFlagsBreakdown;
    private final List<String> mExamples;
    private final RiskLevel mRiskLevel;
    private final String mSafetyWarning;

    public AICommandExplanation(@NonNull String rawCommand,
                                @NonNull String binaryName,
                                @NonNull String summary,
                                @Nullable Map<String, String> flagsBreakdown,
                                @Nullable List<String> examples,
                                @NonNull RiskLevel riskLevel,
                                @Nullable String safetyWarning) {
        this.mRawCommand = rawCommand;
        this.mBinaryName = binaryName;
        this.mSummary = summary;
        this.mFlagsBreakdown = flagsBreakdown != null ? flagsBreakdown : Collections.emptyMap();
        this.mExamples = examples != null ? examples : Collections.emptyList();
        this.mRiskLevel = riskLevel;
        this.mSafetyWarning = safetyWarning;
    }

    @NonNull
    public String getRawCommand() {
        return mRawCommand;
    }

    @NonNull
    public String getBinaryName() {
        return mBinaryName;
    }

    @NonNull
    public String getSummary() {
        return mSummary;
    }

    @NonNull
    public Map<String, String> getFlagsBreakdown() {
        return mFlagsBreakdown;
    }

    @NonNull
    public List<String> getExamples() {
        return mExamples;
    }

    @NonNull
    public RiskLevel getRiskLevel() {
        return mRiskLevel;
    }

    @Nullable
    public String getSafetyWarning() {
        return mSafetyWarning;
    }
}
