package com.termux.app.madmax;

import com.termux.app.madmax.features.FeatureFlagListener;
import com.termux.app.madmax.features.MadMaxFeature;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class MadMaxFeatureRegistryTest {

    @Test
    public void testFeatureDefinitions() {
        // Verify default feature definitions
        Assert.assertNotNull(MadMaxFeature.MATERIAL_YOU);
        Assert.assertTrue(MadMaxFeature.MATERIAL_YOU.getDefaultValue());
        Assert.assertEquals("material_you", MadMaxFeature.MATERIAL_YOU.getKey());

        Assert.assertNotNull(MadMaxFeature.DEVELOPER_DASHBOARD);
        Assert.assertTrue(MadMaxFeature.DEVELOPER_DASHBOARD.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.AI_WORKSPACE);
        Assert.assertTrue(MadMaxFeature.AI_WORKSPACE.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.COMMAND_EXPLAIN);
        Assert.assertTrue(MadMaxFeature.COMMAND_EXPLAIN.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.COMMAND_GENERATE);
        Assert.assertTrue(MadMaxFeature.COMMAND_GENERATE.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.ERROR_ANALYZER);
        Assert.assertTrue(MadMaxFeature.ERROR_ANALYZER.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.GITHUB_WORKSPACE);
        Assert.assertTrue(MadMaxFeature.GITHUB_WORKSPACE.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.PLUGINS_HUB);
        Assert.assertFalse(MadMaxFeature.PLUGINS_HUB.getDefaultValue());
    }

    @Test
    public void testFeatureRiskLevels() {
        Assert.assertEquals(MadMaxFeature.RiskLevel.LOW, MadMaxFeature.MATERIAL_YOU.getRiskLevel());
        Assert.assertEquals(MadMaxFeature.RiskLevel.LOW, MadMaxFeature.DEVELOPER_DASHBOARD.getRiskLevel());
        Assert.assertEquals(MadMaxFeature.RiskLevel.LOW, MadMaxFeature.AI_WORKSPACE.getRiskLevel());
        Assert.assertEquals(MadMaxFeature.RiskLevel.MODERATE, MadMaxFeature.PLUGINS_HUB.getRiskLevel());
    }

    @Test
    public void testFeatureListenerCallback() {
        AtomicBoolean received = new AtomicBoolean(false);
        FeatureFlagListener listener = (feature, isEnabled) -> {
            if (feature == MadMaxFeature.AI_WORKSPACE && isEnabled) {
                received.set(true);
            }
        };

        listener.onFeatureStateChanged(MadMaxFeature.AI_WORKSPACE, true);
        Assert.assertTrue(received.get());
    }
}
