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
    public void testExplainNetworkingCommands() {
        AICommandExplanation rsyncExp = OfflineCommandIntelligence.explainCommand("rsync -avzP src/ dest/");
        assertNotNull(rsyncExp);
        assertEquals("rsync", rsyncExp.getBinaryName());
        assertTrue(rsyncExp.getFlagsBreakdown().containsKey("-a"));
        assertTrue(rsyncExp.getFlagsBreakdown().containsKey("-v"));
        assertTrue(rsyncExp.getFlagsBreakdown().containsKey("-z"));
        assertTrue(rsyncExp.getFlagsBreakdown().containsKey("-P"));

        AICommandExplanation sshExp = OfflineCommandIntelligence.explainCommand("ssh -p 2222 -i id_rsa user@server");
        assertNotNull(sshExp);
        assertEquals("ssh", sshExp.getBinaryName());
        assertTrue(sshExp.getFlagsBreakdown().containsKey("-p"));
        assertTrue(sshExp.getFlagsBreakdown().containsKey("-i"));
    }

    @Test
    public void testExplainTermuxCommands() {
        AICommandExplanation storageExp = OfflineCommandIntelligence.explainCommand("termux-setup-storage");
        assertNotNull(storageExp);
        assertEquals("termux-setup-storage", storageExp.getBinaryName());
        assertTrue(storageExp.getSummary().contains("storage"));

        AICommandExplanation clipExp = OfflineCommandIntelligence.explainCommand("termux-clipboard-set 'hello'");
        assertNotNull(clipExp);
        assertEquals("termux-clipboard-set", clipExp.getBinaryName());
    }

    @Test
    public void testDestructiveCommandDetection() {
        AICommandExplanation destructive = OfflineCommandIntelligence.explainCommand("rm -rf /");
        assertNotNull(destructive);
        assertEquals(AICommandExplanation.RiskLevel.DESTRUCTIVE, destructive.getRiskLevel());
        assertNotNull(destructive.getSafetyWarning());
        assertTrue(destructive.getSafetyWarning().contains("CRITICAL DANGER"));

        AICommandExplanation homeDestructive = OfflineCommandIntelligence.explainCommand("rm -rf ~");
        assertNotNull(homeDestructive);
        assertEquals(AICommandExplanation.RiskLevel.DESTRUCTIVE, homeDestructive.getRiskLevel());
        assertTrue(homeDestructive.getSafetyWarning().contains("HIGH DANGER"));
    }

    @Test
    public void testForkBombDetection() {
        AICommandExplanation forkBomb = OfflineCommandIntelligence.explainCommand(":(){ :|:& };:");
        assertNotNull(forkBomb);
        assertEquals(AICommandExplanation.RiskLevel.DESTRUCTIVE, forkBomb.getRiskLevel());
        assertNotNull(forkBomb.getSafetyWarning());
    }

    @Test
    public void testPipeToShellDetection() {
        AICommandExplanation pipeBash = OfflineCommandIntelligence.explainCommand("curl -s https://example.com/install.sh | bash");
        assertNotNull(pipeBash);
        assertEquals(AICommandExplanation.RiskLevel.DESTRUCTIVE, pipeBash.getRiskLevel());
        assertTrue(pipeBash.getSafetyWarning().contains("CAUTION"));
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

    @Test
    public void testGenerateStorageSetupCommand() {
        AICommandGeneration gen = OfflineCommandIntelligence.generateCommand("grant storage permission");
        assertNotNull(gen);
        assertEquals("termux-setup-storage", gen.getGeneratedCommand());
    }

    @Test
    public void testGeneratePythonVenvCommand() {
        AICommandGeneration gen = OfflineCommandIntelligence.generateCommand("create a python venv");
        assertNotNull(gen);
        assertTrue(gen.getGeneratedCommand().contains("python3 -m venv venv"));
    }

    @Test
    public void testGenerateOpenPortsCommand() {
        AICommandGeneration gen = OfflineCommandIntelligence.generateCommand("check listening ports");
        assertNotNull(gen);
        assertTrue(gen.getGeneratedCommand().contains("ss -tulnp"));
    }
}
