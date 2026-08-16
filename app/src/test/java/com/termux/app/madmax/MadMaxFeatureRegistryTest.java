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

        Assert.assertNotNull(MadMaxFeature.AI_ASSISTANT);
        Assert.assertFalse(MadMaxFeature.AI_ASSISTANT.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.PLUGINS_HUB);
        Assert.assertFalse(MadMaxFeature.PLUGINS_HUB.getDefaultValue());

        Assert.assertNotNull(MadMaxFeature.GITHUB_SYNC);
        Assert.assertFalse(MadMaxFeature.GITHUB_SYNC.getDefaultValue());
    }

    @Test
    public void testFeatureRiskLevels() {
        Assert.assertEquals(MadMaxFeature.RiskLevel.LOW, MadMaxFeature.MATERIAL_YOU.getRiskLevel());
        Assert.assertEquals(MadMaxFeature.RiskLevel.LOW, MadMaxFeature.DEVELOPER_DASHBOARD.getRiskLevel());
        Assert.assertEquals(MadMaxFeature.RiskLevel.MODERATE, MadMaxFeature.AI_ASSISTANT.getRiskLevel());
        Assert.assertEquals(MadMaxFeature.RiskLevel.MODERATE, MadMaxFeature.PLUGINS_HUB.getRiskLevel());
    }

    @Test
    public void testFeatureListenerCallback() {
        AtomicBoolean received = new AtomicBoolean(false);
        FeatureFlagListener listener = (feature, isEnabled) -> {
            if (feature == MadMaxFeature.AI_ASSISTANT && isEnabled) {
                received.set(true);
            }
        };

        listener.onFeatureStateChanged(MadMaxFeature.AI_ASSISTANT, true);
        Assert.assertTrue(received.get());
    }
}
