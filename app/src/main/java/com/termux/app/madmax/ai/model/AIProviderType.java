package com.termux.app.madmax.ai.model;

import androidx.annotation.NonNull;

/**
 * Supported AI provider types for MadMax Command Intelligence.
 */
public enum AIProviderType {
    OFFLINE_HEURISTIC("offline", "Offline Built-in Engine", "Fast, private, zero-network rule engine with 100+ commands."),
    GEMINI("gemini", "Google Gemini Pro / Flash", "Cloud-assisted AI for complex scripting and error resolution."),
    OLLAMA("ollama", "Local Ollama (Termux / LAN)", "Private LLM running locally via termux-packages or LAN server."),
    OPENAI("openai", "OpenAI GPT-4o / GPT-4o-mini", "Cloud AI with deep shell knowledge and code understanding."),
    MOCK("mock", "Mock Simulation Provider", "Simulated AI responses for testing and development.");

    private final String mId;
    private final String mDisplayName;
    private final String mDescription;

    AIProviderType(@NonNull String id, @NonNull String displayName, @NonNull String description) {
        this.mId = id;
        this.mDisplayName = displayName;
        this.mDescription = description;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getDisplayName() {
        return mDisplayName;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public static AIProviderType fromId(@NonNull String id) {
        for (AIProviderType type : values()) {
            if (type.mId.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return OFFLINE_HEURISTIC;
    }
}
