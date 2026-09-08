package com.termux.app.madmax.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.termux.app.madmax.ai.engine.AIErrorAnalyzer;
import com.termux.app.madmax.ai.model.AIErrorDiagnosis;

import org.junit.Test;

/**
 * Unit tests for AIErrorAnalyzer regex classification.
 */
public class AIErrorAnalyzerTest {

    @Test
    public void testCommandNotFoundDiagnosis() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("bash: git: command not found");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.COMMAND_NOT_FOUND, diag.getCategory());
        assertTrue(diag.getSuggestedFixCommand().contains("pkg install git"));
        assertTrue(diag.isAutomatedFixSafe());
    }

    @Test
    public void testCommandNotFoundMappedPackages() {
        AIErrorDiagnosis gccDiag = AIErrorAnalyzer.diagnose("bash: gcc: command not found");
        assertNotNull(gccDiag);
        assertTrue(gccDiag.getSuggestedFixCommand().contains("clang"));

        AIErrorDiagnosis sshDiag = AIErrorAnalyzer.diagnose("bash: ssh: command not found");
        assertNotNull(sshDiag);
        assertTrue(sshDiag.getSuggestedFixCommand().contains("openssh"));
    }

    @Test
    public void testPermissionDeniedDiagnosis() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("bash: ./script.sh: Permission denied");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.PERMISSION_DENIED, diag.getCategory());
        assertTrue(diag.getSuggestedFixCommand().contains("chmod +x"));
    }

    @Test
    public void testDpkgLockDiagnosis() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("E: Could not get lock /var/lib/dpkg/lock - open (11: Resource temporarily unavailable)");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.PACKAGE_LOCK, diag.getCategory());
        assertTrue(diag.getSuggestedFixCommand().contains("killall"));
    }

    @Test
    public void testAddressAlreadyInUseDiagnosis() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("Error: listen EADDRINUSE: address already in use :::3000");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.PORT_CONFLICT, diag.getCategory());
        assertTrue(diag.getSuggestedFixCommand().contains("3000"));
    }

    @Test
    public void testPythonModuleNotFound() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("ModuleNotFoundError: No module named 'requests'");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.DEPENDENCY_MISSING, diag.getCategory());
        assertEquals("pip install requests", diag.getSuggestedFixCommand());
    }

    @Test
    public void testGitMergeConflict() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("CONFLICT (content): Merge conflict in app/build.gradle\nAutomatic merge failed; fix conflicts and then commit the result.");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.GIT_ERROR, diag.getCategory());
        assertEquals("git merge --abort", diag.getSuggestedFixCommand());
    }

    @Test
    public void testShellSyntaxError() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("bash: syntax error near unexpected token `fi'");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.SYNTAX_ERROR, diag.getCategory());
    }

    @Test
    public void testDnsResolutionFailure() {
        AIErrorDiagnosis diag = AIErrorAnalyzer.diagnose("curl: (6) Could not resolve host: invalid.domain.xyz");
        assertNotNull(diag);
        assertEquals(AIErrorDiagnosis.ErrorCategory.NETWORK_UNREACHABLE, diag.getCategory());
    }
}
