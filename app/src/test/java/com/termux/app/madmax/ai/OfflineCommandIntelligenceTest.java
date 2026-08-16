package com.termux.app.madmax.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.termux.app.madmax.ai.engine.OfflineCommandIntelligence;
import com.termux.app.madmax.ai.model.AICommandExplanation;
import com.termux.app.madmax.ai.model.AICommandGeneration;

import org.junit.Test;

/**
 * Unit tests for OfflineCommandIntelligence engine.
 */
public class OfflineCommandIntelligenceTest {

    @Test
    public void testExplainStandardCommand() {
        AICommandExplanation explanation = OfflineCommandIntelligence.explainCommand("tar -czvf archive.tar.gz folder/");
        assertNotNull(explanation);
        assertEquals("tar", explanation.getBinaryName());
        assertTrue(explanation.getSummary().contains("Archive"));
        assertTrue(explanation.getFlagsBreakdown().containsKey("-c"));
        assertTrue(explanation.getFlagsBreakdown().containsKey("-z"));
        assertTrue(explanation.getFlagsBreakdown().containsKey("-v"));
        assertTrue(explanation.getFlagsBreakdown().containsKey("-f"));
        assertEquals(AICommandExplanation.RiskLevel.SAFE, explanation.getRiskLevel());
    }

    @Test
    public void testDestructiveCommandDetection() {
        AICommandExplanation destructive = OfflineCommandIntelligence.explainCommand("rm -rf /");
        assertNotNull(destructive);
        assertEquals(AICommandExplanation.RiskLevel.DESTRUCTIVE, destructive.getRiskLevel());
        assertNotNull(destructive.getSafetyWarning());
        assertTrue(destructive.getSafetyWarning().contains("CRITICAL DANGER"));
    }

    @Test
    public void testForkBombDetection() {
        AICommandExplanation forkBomb = OfflineCommandIntelligence.explainCommand(":(){ :|:& };:");
        assertNotNull(forkBomb);
        assertEquals(AICommandExplanation.RiskLevel.DESTRUCTIVE, forkBomb.getRiskLevel());
        assertNotNull(forkBomb.getSafetyWarning());
    }

    @Test
    public void testGenerateCommandFromPrompt() {
        AICommandGeneration gen = OfflineCommandIntelligence.generateCommand("find all large files over 100mb");
        assertNotNull(gen);
        assertTrue(gen.getGeneratedCommand().contains("find"));
        assertTrue(gen.getGeneratedCommand().contains("+100M"));
        assertTrue(gen.getConfidence() >= 0.8f);
    }

    @Test
    public void testGenerateKillPortCommand() {
        AICommandGeneration gen = OfflineCommandIntelligence.generateCommand("kill process on port 8080");
        assertNotNull(gen);
        assertTrue(gen.getGeneratedCommand().contains("8080"));
        assertTrue(gen.getGeneratedCommand().contains("kill -9"));
    }
}
