package com.termux.app.madmax.ai.services;

import android.content.Context;
import androidx.annotation.NonNull;

import com.termux.app.madmax.ai.engine.AIProvider;
import com.termux.app.madmax.ai.engine.AIProviderManager;
import com.termux.app.madmax.ai.model.AICommandExplanation;
import com.termux.app.madmax.ai.model.AICommandGeneration;
import com.termux.app.madmax.ai.model.AIErrorDiagnosis;
import com.termux.app.madmax.ai.model.AIHistoryItem;
import com.termux.app.madmax.ai.model.AIProviderType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * High-level orchestration service for MadMax Command Intelligence.
 */
public class AICommandService {

    private static volatile AICommandService sInstance;

    private final Context mContext;
    private final AIProviderManager mProviderManager;
    private final AIHistoryManager mHistoryManager;

    private final AtomicInteger mTotalQueries = new AtomicInteger(0);
    private final AtomicLong mTotalLatencyMs = new AtomicLong(0);

    private AICommandService(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        this.mProviderManager = AIProviderManager.getInstance(mContext);
        this.mHistoryManager = AIHistoryManager.getInstance(mContext);
    }

    public static AICommandService getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (AICommandService.class) {
                if (sInstance == null) {
                    sInstance = new AICommandService(context);
                }
            }
        }
        return sInstance;
    }

    public void explainCommand(@NonNull String rawCommand, @NonNull AIProvider.Callback<AICommandExplanation> callback) {
        final long startTime = System.currentTimeMillis();
        mProviderManager.getActiveProvider().explainCommand(rawCommand, new AIProvider.Callback<AICommandExplanation>() {
            @Override
            public void onSuccess(@NonNull AICommandExplanation result) {
                long duration = System.currentTimeMillis() - startTime;
                recordMetrics(duration);

                mHistoryManager.addHistoryItem(
                    AIHistoryItem.Type.EXPLAIN,
                    result.getRawCommand(),
                    result.getRawCommand(),
                    result.getSummary()
                );
                callback.onSuccess(result);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void generateCommand(@NonNull String prompt, @NonNull AIProvider.Callback<AICommandGeneration> callback) {
        final long startTime = System.currentTimeMillis();
        mProviderManager.getActiveProvider().generateCommand(prompt, new AIProvider.Callback<AICommandGeneration>() {
            @Override
            public void onSuccess(@NonNull AICommandGeneration result) {
                long duration = System.currentTimeMillis() - startTime;
                recordMetrics(duration);

                mHistoryManager.addHistoryItem(
                    AIHistoryItem.Type.GENERATE,
                    result.getPrompt(),
                    result.getGeneratedCommand(),
                    result.getExplanation()
                );
                callback.onSuccess(result);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                callback.onError(error);
            }
        });
    }

    public void diagnoseError(@NonNull String rawError, @NonNull AIProvider.Callback<AIErrorDiagnosis> callback) {
        final long startTime = System.currentTimeMillis();
        mProviderManager.getActiveProvider().diagnoseError(rawError, new AIProvider.Callback<AIErrorDiagnosis>() {
            @Override
            public void onSuccess(@NonNull AIErrorDiagnosis result) {
                long duration = System.currentTimeMillis() - startTime;
                recordMetrics(duration);

                mHistoryManager.addHistoryItem(
                    AIHistoryItem.Type.DIAGNOSE,
                    result.getRawError().length() > 60 ? result.getRawError().substring(0, 57) + "..." : result.getRawError(),
                    result.getSuggestedFixCommand(),
                    result.getRootCause()
                );
                callback.onSuccess(result);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                callback.onError(error);
            }
        });
    }

    private void recordMetrics(long latencyMs) {
        mTotalQueries.incrementAndGet();
        mTotalLatencyMs.addAndGet(latencyMs);
    }

    public int getTotalQueries() {
        return mTotalQueries.get();
    }

    public long getAverageLatencyMs() {
        int count = mTotalQueries.get();
        return count == 0 ? 0 : mTotalLatencyMs.get() / count;
    }

    @NonNull
    public AIProviderType getActiveProviderType() {
        return mProviderManager.getActiveProviderType();
    }
}
