package com.termux.app.madmax.ai.model;

import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Model representing an item in the AI Command History.
 */
public class AIHistoryItem {

    public enum Type {
        EXPLAIN("Explain", 0xFF007AFF),
        GENERATE("Generate", 0xFF34C759),
        DIAGNOSE("Diagnose", 0xFFFF9500);

        private final String mLabel;
        private final int mColor;

        Type(@NonNull String label, int color) {
            this.mLabel = label;
            this.mColor = color;
        }

        @NonNull
        public String getLabel() {
            return mLabel;
        }

        public int getColor() {
            return mColor;
        }
    }

    private final String mId;
    private final Type mType;
    private final String mQuery;
    private final String mResultCommand;
    private final String mSummary;
    private final long mTimestamp;

    public AIHistoryItem(@NonNull Type type,
                         @NonNull String query,
                         @NonNull String resultCommand,
                         @NonNull String summary) {
        this(UUID.randomUUID().toString(), type, query, resultCommand, summary, System.currentTimeMillis());
    }

    public AIHistoryItem(@NonNull String id,
                         @NonNull Type type,
                         @NonNull String query,
                         @NonNull String resultCommand,
                         @NonNull String summary,
                         long timestamp) {
        this.mId = id;
        this.mType = type;
        this.mQuery = query;
        this.mResultCommand = resultCommand;
        this.mSummary = summary;
        this.mTimestamp = timestamp;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Type getType() {
        return mType;
    }

    @NonNull
    public String getQuery() {
        return mQuery;
    }

    @NonNull
    public String getResultCommand() {
        return mResultCommand;
    }

    @NonNull
    public String getSummary() {
        return mSummary;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
}
