package com.termux.app.madmax.ai.services;

import android.content.Context;
import androidx.annotation.NonNull;

import com.termux.app.madmax.ai.model.AIHistoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread-safe history manager with persistent JSON disk caching.
 */
public class AIHistoryManager {

    private static final String HISTORY_FILENAME = "madmax_ai_history.json";
    private static final int MAX_HISTORY_ITEMS = 100;

    private static volatile AIHistoryManager sInstance;
    private final Context mContext;
    private final List<AIHistoryItem> mHistoryList = new ArrayList<>();
    private final Object mLock = new Object();

    private AIHistoryManager(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        loadHistoryFromDisk();
    }

    public static AIHistoryManager getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (AIHistoryManager.class) {
                if (sInstance == null) {
                    sInstance = new AIHistoryManager(context);
                }
            }
        }
        return sInstance;
    }

    public void addHistoryItem(@NonNull AIHistoryItem.Type type,
                               @NonNull String query,
                               @NonNull String resultCommand,
                               @NonNull String summary) {
        synchronized (mLock) {
            AIHistoryItem item = new AIHistoryItem(type, query, resultCommand, summary);
            mHistoryList.add(0, item); // Add to head (newest first)
            if (mHistoryList.size() > MAX_HISTORY_ITEMS) {
                mHistoryList.remove(mHistoryList.size() - 1);
            }
            saveHistoryToDisk();
        }
    }

    @NonNull
    public List<AIHistoryItem> getHistory() {
        synchronized (mLock) {
            return Collections.unmodifiableList(new ArrayList<>(mHistoryList));
        }
    }

    @NonNull
    public List<AIHistoryItem> searchHistory(@NonNull String query) {
        String q = query.toLowerCase().trim();
        List<AIHistoryItem> results = new ArrayList<>();
        synchronized (mLock) {
            for (AIHistoryItem item : mHistoryList) {
                if (item.getQuery().toLowerCase().contains(q) ||
                    item.getResultCommand().toLowerCase().contains(q) ||
                    item.getSummary().toLowerCase().contains(q)) {
                    results.add(item);
                }
            }
        }
        return results;
    }

    public void clearHistory() {
        synchronized (mLock) {
            mHistoryList.clear();
            saveHistoryToDisk();
        }
    }

    public int getHistoryCount() {
        synchronized (mLock) {
            return mHistoryList.size();
        }
    }

    private void loadHistoryFromDisk() {
        synchronized (mLock) {
            mHistoryList.clear();
            File file = new File(mContext.getFilesDir(), HISTORY_FILENAME);
            if (!file.exists()) {
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = new byte[(int) file.length()];
                int read = fis.read(data);
                if (read > 0) {
                    String jsonStr = new String(data, 0, read, StandardCharsets.UTF_8);
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String id = obj.optString("id");
                        String typeStr = obj.optString("type", AIHistoryItem.Type.GENERATE.name());
                        AIHistoryItem.Type type = AIHistoryItem.Type.valueOf(typeStr);
                        String query = obj.optString("query");
                        String resultCommand = obj.optString("resultCommand");
                        String summary = obj.optString("summary");
                        long timestamp = obj.optLong("timestamp", System.currentTimeMillis());

                        mHistoryList.add(new AIHistoryItem(id, type, query, resultCommand, summary, timestamp));
                    }
                }
            } catch (Exception ignored) {
                // Recover gracefully on corruption
            }
        }
    }

    private void saveHistoryToDisk() {
        synchronized (mLock) {
            JSONArray jsonArray = new JSONArray();
            for (AIHistoryItem item : mHistoryList) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("id", item.getId());
                    obj.put("type", item.getType().name());
                    obj.put("query", item.getQuery());
                    obj.put("resultCommand", item.getResultCommand());
                    obj.put("summary", item.getSummary());
                    obj.put("timestamp", item.getTimestamp());
                    jsonArray.put(obj);
                } catch (JSONException ignored) {}
            }

            File file = new File(mContext.getFilesDir(), HISTORY_FILENAME);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException ignored) {}
        }
    }
}
