package com.termux.app.madmax.ai.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.termux.app.madmax.ai.model.AICommandExplanation;
import com.termux.app.madmax.ai.model.AICommandGeneration;
import com.termux.app.madmax.ai.model.AIErrorDiagnosis;
import com.termux.app.madmax.ai.model.AIProviderType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages AI provider instances, routing requests to the appropriate engine.
 */
public class AIProviderManager {

    private static volatile AIProviderManager sInstance;

    private final Context mContext;
    private final ExecutorService mExecutor;
    private final Handler mMainHandler;
    private AIProviderType mActiveType = AIProviderType.OFFLINE_HEURISTIC;

    private final AIProvider mOfflineProvider;
    private final AIProvider mMockProvider;

    private AIProviderManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mExecutor = Executors.newFixedThreadPool(2);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mOfflineProvider = new OfflineAIProvider(mExecutor, mMainHandler);
        this.mMockProvider = new MockAIProvider(mExecutor, mMainHandler);
    }

    public static AIProviderManager getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (AIProviderManager.class) {
                if (sInstance == null) {
                    sInstance = new AIProviderManager(context);
                }
            }
        }
        return sInstance;
    }

    @NonNull
    public AIProviderType getActiveProviderType() {
        return mActiveType;
    }

    public void setActiveProviderType(@NonNull AIProviderType type) {
        this.mActiveType = type;
    }

    @NonNull
    public AIProvider getActiveProvider() {
        switch (mActiveType) {
            case MOCK:
                return mMockProvider;
            case OFFLINE_HEURISTIC:
            default:
                return mOfflineProvider;
        }
    }

    // --- Offline Provider Implementation ---
    private static class OfflineAIProvider implements AIProvider {
        private final ExecutorService mExec;
        private final Handler mHandler;

        OfflineAIProvider(ExecutorService exec, Handler handler) {
            this.mExec = exec;
            this.mHandler = handler;
        }

        @NonNull
        @Override
        public AIProviderType getProviderType() {
            return AIProviderType.OFFLINE_HEURISTIC;
        }

        @Override
        public boolean isConfigured() {
            return true;
        }

        @Override
        public void explainCommand(@NonNull String command, @NonNull Callback<AICommandExplanation> callback) {
            mExec.execute(() -> {
                AICommandExplanation explanation = OfflineCommandIntelligence.explainCommand(command);
                mHandler.post(() -> callback.onSuccess(explanation));
            });
        }

        @Override
        public void generateCommand(@NonNull String prompt, @NonNull Callback<AICommandGeneration> callback) {
            mExec.execute(() -> {
                AICommandGeneration generation = OfflineCommandIntelligence.generateCommand(prompt);
                mHandler.post(() -> callback.onSuccess(generation));
            });
        }

        @Override
        public void diagnoseError(@NonNull String rawError, @NonNull Callback<AIErrorDiagnosis> callback) {
            mExec.execute(() -> {
                AIErrorDiagnosis diagnosis = AIErrorAnalyzer.diagnose(rawError);
                mHandler.post(() -> callback.onSuccess(diagnosis));
            });
        }
    }

    // --- Mock Simulation Provider ---
    private static class MockAIProvider implements AIProvider {
        private final ExecutorService mExec;
        private final Handler mHandler;

        MockAIProvider(ExecutorService exec, Handler handler) {
            this.mExec = exec;
            this.mHandler = handler;
        }

        @NonNull
        @Override
        public AIProviderType getProviderType() {
            return AIProviderType.MOCK;
        }

        @Override
        public boolean isConfigured() {
            return true;
        }

        @Override
        public void explainCommand(@NonNull String command, @NonNull Callback<AICommandExplanation> callback) {
            mExec.execute(() -> {
                try {
                    Thread.sleep(400); // Simulate network latency
                } catch (InterruptedException ignored) {}
                AICommandExplanation explanation = OfflineCommandIntelligence.explainCommand(command);
                mHandler.post(() -> callback.onSuccess(explanation));
            });
        }

        @Override
        public void generateCommand(@NonNull String prompt, @NonNull Callback<AICommandGeneration> callback) {
            mExec.execute(() -> {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException ignored) {}
                AICommandGeneration generation = OfflineCommandIntelligence.generateCommand(prompt);
                mHandler.post(() -> callback.onSuccess(generation));
            });
        }

        @Override
        public void diagnoseError(@NonNull String rawError, @NonNull Callback<AIErrorDiagnosis> callback) {
            mExec.execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
                AIErrorDiagnosis diagnosis = AIErrorAnalyzer.diagnose(rawError);
                mHandler.post(() -> callback.onSuccess(diagnosis));
            });
        }
    }
}
