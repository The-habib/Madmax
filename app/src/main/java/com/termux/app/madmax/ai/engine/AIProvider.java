package com.termux.app.madmax.ai.engine;

import androidx.annotation.NonNull;

import com.termux.app.madmax.ai.model.AICommandExplanation;
import com.termux.app.madmax.ai.model.AICommandGeneration;
import com.termux.app.madmax.ai.model.AIErrorDiagnosis;
import com.termux.app.madmax.ai.model.AIProviderType;

/**
 * Common async interface implemented by all AI Command Intelligence providers.
 */
public interface AIProvider {

    interface Callback<T> {
        void onSuccess(@NonNull T result);
        void onError(@NonNull Throwable error);
    }

    @NonNull
    AIProviderType getProviderType();

    boolean isConfigured();

    void explainCommand(@NonNull String command, @NonNull Callback<AICommandExplanation> callback);

    void generateCommand(@NonNull String prompt, @NonNull Callback<AICommandGeneration> callback);

    void diagnoseError(@NonNull String rawError, @NonNull Callback<AIErrorDiagnosis> callback);
}
