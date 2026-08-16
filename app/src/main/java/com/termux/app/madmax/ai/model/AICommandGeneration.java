package com.termux.app.madmax.ai.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Model representing a generated shell command from natural language.
 */
public class AICommandGeneration {

    private final String mPrompt;
    private final String mGeneratedCommand;
    private final String mExplanation;
    private final float mConfidence;
    private final List<String> mAlternativeCommands;
    private final AICommandExplanation.RiskLevel mRiskLevel;

    public AICommandGeneration(@NonNull String prompt,
                               @NonNull String generatedCommand,
                               @NonNull String explanation,
                               float confidence,
                               @Nullable List<String> alternativeCommands,
                               @NonNull AICommandExplanation.RiskLevel riskLevel) {
        this.mPrompt = prompt;
        this.mGeneratedCommand = generatedCommand;
        this.mExplanation = explanation;
        this.mConfidence = confidence;
        this.mAlternativeCommands = alternativeCommands != null ? alternativeCommands : Collections.emptyList();
        this.mRiskLevel = riskLevel;
    }

    @NonNull
    public String getPrompt() {
        return mPrompt;
    }

    @NonNull
    public String getGeneratedCommand() {
        return mGeneratedCommand;
    }

    @NonNull
    public String getExplanation() {
        return mExplanation;
    }

    public float getConfidence() {
        return mConfidence;
    }

    @NonNull
    public List<String> getAlternativeCommands() {
        return mAlternativeCommands;
    }

    @NonNull
    public AICommandExplanation.RiskLevel getRiskLevel() {
        return mRiskLevel;
    }
}
