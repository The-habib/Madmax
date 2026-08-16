package com.termux.app.madmax.dev;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.termux.R;
import com.termux.app.madmax.features.MadMaxFeature;
import com.termux.app.madmax.features.MadMaxFeatureRegistry;
import com.termux.shared.activity.media.AppCompatActivityUtils;
import com.termux.shared.theme.NightMode;

/**
 * Diagnostic & Developer Hub for MadMax.
 * Displays real-time SDK, git commit, ABI, hardware, and feature flags state.
 */
public class DeveloperDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivityUtils.setNightMode(this, NightMode.getAppNightMode().getName(), true);
        setContentView(R.layout.activity_developer_dashboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        populateMetrics();
    }

    private void populateMetrics() {
        SystemMetricsCollector.SystemReport report = SystemMetricsCollector.collect(this);

        TextView versionView = findViewById(R.id.text_version_info);
        if (versionView != null) {
            String text = "App Version:   " + report.appVersion + " (Build " + report.appVersionCode + ")\n" +
                          "Git Commit:    " + report.gitCommit + "\n" +
                          "Build Type:    " + report.buildType + "\n" +
                          "Variant:       " + report.packageVariant;
            versionView.setText(text);
        }

        TextView sdkView = findViewById(R.id.text_sdk_info);
        if (sdkView != null) {
            String text = "Target SDK:    API " + report.targetSdkVersion + " (W^X Protected)\n" +
                          "Compile SDK:   API " + report.compileSdkVersion + "\n" +
                          "Min SDK:       API " + report.minSdkVersion + "\n" +
                          "Android OS:    " + report.androidRelease + " (API " + report.androidSdkInt + ")";
            sdkView.setText(text);
        }

        TextView hwView = findViewById(R.id.text_hardware_info);
        if (hwView != null) {
            String text = "Device:        " + report.deviceManufacturer + " " + report.deviceModel + "\n" +
                          "Primary ABI:   " + report.primaryAbi + "\n" +
                          "Supported:     " + report.supportedAbis + "\n" +
                          "Available RAM: " + report.availableRam + " / " + report.totalRam + "\n" +
                          "Low Memory:    " + report.isLowMemory;
            hwView.setText(text);
        }

        TextView flagsView = findViewById(R.id.text_feature_flags);
        if (flagsView != null) {
            MadMaxFeatureRegistry registry = MadMaxFeatureRegistry.getInstance(this);
            StringBuilder sb = new StringBuilder();
            for (MadMaxFeature feature : MadMaxFeature.values()) {
                boolean enabled = registry.isEnabled(feature);
                sb.append(enabled ? "[✓] " : "[ ] ")
                  .append(feature.getKey())
                  .append(" (").append(feature.getRiskLevel().name()).append(")\n");
            }
            flagsView.setText(sb.toString().trim());
        }

        MaterialButton copyBtn = findViewById(R.id.btn_copy_report);
        if (copyBtn != null) {
            copyBtn.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("MadMax Diagnostics", report.toMarkdown());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Diagnostics copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
