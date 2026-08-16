package com.termux.app.madmax;

import com.termux.app.madmax.dev.SystemMetricsCollector;

import org.junit.Assert;
import org.junit.Test;

public class SystemMetricsCollectorTest {

    @Test
    public void testSystemReportMarkdownFormatting() {
        SystemMetricsCollector.SystemReport report = new SystemMetricsCollector.SystemReport();
        report.appVersion = "1.1.0";
        report.appVersionCode = 110;
        report.gitCommit = SystemMetricsCollector.GIT_COMMIT_HASH;
        report.buildType = "debug";
        report.packageVariant = "apt-android-7";
        report.targetSdkVersion = 28;
        report.compileSdkVersion = 36;
        report.minSdkVersion = 21;
        report.androidRelease = "14";
        report.androidSdkInt = 34;
        report.deviceModel = "TestModel";
        report.deviceManufacturer = "TestMfg";
        report.primaryAbi = "arm64-v8a";
        report.supportedAbis = "[arm64-v8a, armeabi-v7a]";
        report.totalRam = "8.0 GB";
        report.availableRam = "4.5 GB";
        report.isLowMemory = false;

        String md = report.toMarkdown();
        Assert.assertNotNull(md);
        Assert.assertTrue(md.contains("1.1.0"));
        Assert.assertTrue(md.contains(SystemMetricsCollector.GIT_COMMIT_HASH));
        Assert.assertTrue(md.contains("Target SDK:** API 28"));
        Assert.assertTrue(md.contains("arm64-v8a"));
        Assert.assertTrue(md.contains("4.5 GB"));
    }
}
