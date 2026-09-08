package com.termux.app.madmax.ai.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Model representing the automated diagnosis of a terminal error.
 */
public class AIErrorDiagnosis {

    public enum ErrorCategory {
        COMMAND_NOT_FOUND("Command Not Found"),
        PERMISSION_DENIED("Permission Denied"),
        PACKAGE_LOCK("Package Manager Locked"),
        PORT_CONFLICT("Port / Address Conflict"),
        GIT_ERROR("Git State / Conflict"),
        DEPENDENCY_MISSING("Missing Module / Library"),
        STORAGE_FULL("Disk / Storage Full"),
        NETWORK_UNREACHABLE("Network / DNS Failure"),
        SYNTAX_ERROR("Shell Syntax Error"),
        GENERAL_ERROR("General Process Error");

        private final String mDisplayName;

        ErrorCategory(@NonNull String displayName) {
            this.mDisplayName = displayName;
        }

        @NonNull
        public String getDisplayName() {
            return mDisplayName;
        }
    }

    private final String mRawError;
    private final ErrorCategory mCategory;
    private final String mRootCause;
    private final String mRemedyExplanation;
    private final String mSuggestedFixCommand;
    private final List<String> mSecondarySuggestions;
    private final boolean mIsAutomatedFixSafe;

    public AIErrorDiagnosis(@NonNull String rawError,
                            @NonNull ErrorCategory category,
                            @NonNull String rootCause,
                            @NonNull String remedyExplanation,
                            @Nullable String suggestedFixCommand,
                            @Nullable List<String> secondarySuggestions,
                            boolean isAutomatedFixSafe) {
        this.mRawError = rawError;
        this.mCategory = category;
        this.mRootCause = rootCause;
        this.mRemedyExplanation = remedyExplanation;
        this.mSuggestedFixCommand = suggestedFixCommand != null ? suggestedFixCommand : "";
        this.mSecondarySuggestions = secondarySuggestions != null ? secondarySuggestions : Collections.emptyList();
        this.mIsAutomatedFixSafe = isAutomatedFixSafe;
    }

    @NonNull
    public String getRawError() {
        return mRawError;
    }

    @NonNull
    public ErrorCategory getCategory() {
        return mCategory;
    }

    @NonNull
    public String getRootCause() {
        return mRootCause;
    }

    @NonNull
    public String getRemedyExplanation() {
        return mRemedyExplanation;
    }

    @NonNull
    public String getSuggestedFixCommand() {
        return mSuggestedFixCommand;
    }

    @NonNull
    public List<String> getSecondarySuggestions() {
        return mSecondarySuggestions;
    }

    public boolean isAutomatedFixSafe() {
        return mIsAutomatedFixSafe;
    }
}
