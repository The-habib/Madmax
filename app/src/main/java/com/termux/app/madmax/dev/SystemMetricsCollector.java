package com.termux.app.madmax.dev;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.termux.BuildConfig;
import com.termux.app.madmax.core.MadMaxConstants;
import com.termux.shared.android.AndroidUtils;
import com.termux.shared.android.PackageUtils;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Collects system, hardware, runtime, and build diagnostics for the MadMax Developer Dashboard.
 */
public final class SystemMetricsCollector {

    public static final String GIT_COMMIT_HASH = "d2aa4986";

    private SystemMetricsCollector() {}

    public static class SystemReport {
        public String appVersion;
        public int appVersionCode;
        public String gitCommit;
        public String buildType;
        public String packageVariant;

        public int targetSdkVersion;
        public int compileSdkVersion;
        public int minSdkVersion;
        public String androidRelease;
        public int androidSdkInt;

        public String deviceModel;
        public String deviceManufacturer;
        public String primaryAbi;
        public String supportedAbis;

        public String totalRam;
        public String availableRam;
        public boolean isLowMemory;
        public String aiProvider;
        public int aiTotalQueries;
        public long aiAvgLatencyMs;
        public int aiHistoryCount;

        @NonNull
        public String toMarkdown() {
            return "### MadMax Diagnostics Report\n" +
                "- **App Version:** " + appVersion + " (Build " + appVersionCode + ")\n" +
                "- **Git Commit:** `" + gitCommit + "`\n" +
                "- **Build Type:** " + buildType + " (" + packageVariant + ")\n" +
                "- **Target SDK:** API " + targetSdkVersion + " (Protected W^X mode)\n" +
                "- **Compile SDK:** API " + compileSdkVersion + "\n" +
                "- **Android OS:** " + androidRelease + " (API " + androidSdkInt + ")\n" +
                "- **Device:** " + deviceManufacturer + " " + deviceModel + "\n" +
                "- **Primary ABI:** `" + primaryAbi + "`\n" +
                "- **Supported ABIs:** " + supportedAbis + "\n" +
                "- **Memory:** " + availableRam + " available / " + totalRam + " total (LowMem: " + isLowMemory + ")\n" +
                "- **AI Engine:** " + aiProvider + " (" + aiTotalQueries + " queries, avg " + aiAvgLatencyMs + "ms, " + aiHistoryCount + " cached items)\n";
        }
    }

    @NonNull
    public static SystemReport collect(@NonNull Context context) {
        SystemReport report = new SystemReport();

        report.appVersion = BuildConfig.VERSION_NAME;
        report.appVersionCode = BuildConfig.VERSION_CODE;
        report.gitCommit = GIT_COMMIT_HASH;
        report.buildType = BuildConfig.BUILD_TYPE;
        report.packageVariant = BuildConfig.TERMUX_PACKAGE_VARIANT;

        report.targetSdkVersion = PackageUtils.getTargetSDKForPackage(context);
        report.compileSdkVersion = 36;
        report.minSdkVersion = 21;
        report.androidRelease = Build.VERSION.RELEASE;
        report.androidSdkInt = Build.VERSION.SDK_INT;

        report.deviceModel = Build.MODEL;
        report.deviceManufacturer = Build.MANUFACTURER;
        report.primaryAbi = Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "unknown";
        report.supportedAbis = Arrays.toString(Build.SUPPORTED_ABIS);

        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (actManager != null) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            DecimalFormat df = new DecimalFormat("#.##");
            report.totalRam = df.format(memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)) + " GB";
            report.availableRam = df.format(memInfo.availMem / (1024.0 * 1024.0 * 1024.0)) + " GB";
            report.isLowMemory = memInfo.lowMemory;
        } else {
            report.totalRam = "Unknown";
            report.availableRam = "Unknown";
            report.isLowMemory = false;
        }

        try {
            com.termux.app.madmax.ai.core.AIWorkspaceManager aiManager = com.termux.app.madmax.ai.core.AIWorkspaceManager.getInstance(context);
            report.aiProvider = aiManager.getCommandService().getActiveProviderType().getDisplayName();
            report.aiTotalQueries = aiManager.getCommandService().getTotalQueries();
            report.aiAvgLatencyMs = aiManager.getCommandService().getAverageLatencyMs();
            report.aiHistoryCount = aiManager.getHistoryManager().getHistoryCount();
        } catch (Exception e) {
            report.aiProvider = "Offline Engine";
            report.aiTotalQueries = 0;
            report.aiAvgLatencyMs = 0;
            report.aiHistoryCount = 0;
        }

        return report;
    }
}
