package com.termux.app.madmax.ai.engine;

import androidx.annotation.NonNull;

import com.termux.app.madmax.ai.model.AIErrorDiagnosis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent error analyzer for diagnosing terminal stderr and exit code errors.
 */
public class AIErrorAnalyzer {

    private static final Map<String, String> BINARY_TO_PKG = new HashMap<>();

    static {
        BINARY_TO_PKG.put("git", "git");
        BINARY_TO_PKG.put("python", "python");
        BINARY_TO_PKG.put("python3", "python");
        BINARY_TO_PKG.put("pip", "python");
        BINARY_TO_PKG.put("node", "nodejs");
        BINARY_TO_PKG.put("npm", "nodejs");
        BINARY_TO_PKG.put("npx", "nodejs");
        BINARY_TO_PKG.put("yarn", "yarn");
        BINARY_TO_PKG.put("gcc", "clang");
        BINARY_TO_PKG.put("g++", "clang");
        BINARY_TO_PKG.put("clang", "clang");
        BINARY_TO_PKG.put("rustc", "rust");
        BINARY_TO_PKG.put("cargo", "rust");
        BINARY_TO_PKG.put("ffmpeg", "ffmpeg");
        BINARY_TO_PKG.put("nmap", "nmap");
        BINARY_TO_PKG.put("htop", "htop");
        BINARY_TO_PKG.put("tmux", "tmux");
        BINARY_TO_PKG.put("jq", "jq");
        BINARY_TO_PKG.put("curl", "curl");
        BINARY_TO_PKG.put("wget", "wget");
        BINARY_TO_PKG.put("zip", "zip");
        BINARY_TO_PKG.put("unzip", "unzip");
        BINARY_TO_PKG.put("tree", "tree");
        BINARY_TO_PKG.put("zsh", "zsh");
        BINARY_TO_PKG.put("fish", "fish");
        BINARY_TO_PKG.put("nvim", "neovim");
        BINARY_TO_PKG.put("neovim", "neovim");
        BINARY_TO_PKG.put("vim", "vim");
        BINARY_TO_PKG.put("nano", "nano");
        BINARY_TO_PKG.put("sqlite3", "sqlite");
        BINARY_TO_PKG.put("ssh", "openssh");
        BINARY_TO_PKG.put("sshd", "openssh");
        BINARY_TO_PKG.put("make", "make");
        BINARY_TO_PKG.put("cmake", "cmake");
        BINARY_TO_PKG.put("gh", "gh");
        BINARY_TO_PKG.put("rsync", "rsync");
        BINARY_TO_PKG.put("tar", "tar");
    }

    /**
     * Analyzes raw terminal stderr text and produces an actionable diagnosis.
     */
    @NonNull
    public static AIErrorDiagnosis diagnose(@NonNull String rawError) {
        String err = rawError.trim();
        if (err.isEmpty()) {
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.GENERAL_ERROR,
                "No error message provided.",
                "Paste or capture the error output from the terminal to analyze.",
                "", null, false);
        }

        // 1. Command Not Found
        Matcher cmdNotFoundMatcher = Pattern.compile("(?i)(?:bash:\\s*)?([a-zA-Z0-9_-]+):\\s*command not found").matcher(err);
        if (cmdNotFoundMatcher.find()) {
            String missingCmd = cmdNotFoundMatcher.group(1);
            String pkg = BINARY_TO_PKG.get(missingCmd.toLowerCase());
            if (pkg == null) pkg = missingCmd;

            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.COMMAND_NOT_FOUND,
                "The binary '" + missingCmd + "' is not installed or not in your current PATH environment.",
                "Install the required package via Termux package manager.",
                "pkg install " + pkg,
                listOf("pkg search " + missingCmd, "echo $PATH", "which " + missingCmd),
                true);
        }

        // 2. Permission Denied
        if (err.contains("Permission denied") || err.contains("EACCES")) {
            Matcher fileMatcher = Pattern.compile("(?i)(?:bash:\\s*)?([^:]+):\\s*Permission denied").matcher(err);
            String targetFile = fileMatcher.find() ? fileMatcher.group(1) : "the target file";

            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.PERMISSION_DENIED,
                "The shell lacks execution or read/write permissions for " + targetFile + ", or Android storage permission is required.",
                "Grant executable permissions using 'chmod +x' or request Android shared storage access.",
                "chmod +x " + (targetFile.startsWith("/") || targetFile.startsWith(".") ? targetFile : "./" + targetFile),
                listOf("termux-setup-storage", "ls -l " + targetFile, "whoami"),
                true);
        }

        // 3. dpkg / apt lock
        if (err.contains("Could not get lock") || err.contains("dpkg/lock") || err.contains("is another process using it?")) {
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.PACKAGE_LOCK,
                "Another process is currently holding the APT / DPKG database lock.",
                "Ensure no other terminal session is installing packages, or terminate stale package manager processes.",
                "killall -9 apt apt-get dpkg || true",
                listOf("ps aux | grep -E 'apt|dpkg'", "rm -f $PREFIX/var/lib/dpkg/lock*"),
                true);
        }

        // 4. Address already in use / Port conflict
        if (err.contains("Address already in use") || err.contains("EADDRINUSE") || err.contains("bind: Address already in use")) {
            Matcher portMatcher = Pattern.compile("(?i)(?:port|:)\\s*(\\d{2,5})").matcher(err);
            String port = portMatcher.find() ? portMatcher.group(1) : "8080";

            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.PORT_CONFLICT,
                "Network port " + port + " is already occupied by another running server or background process.",
                "Identify and terminate the process listening on port " + port + ".",
                "kill -9 $(lsof -t -i:" + port + ")",
                listOf("lsof -i :" + port, "netstat -tulnp | grep :" + port),
                true);
        }

        // 5. Python Missing Module
        Matcher pythonModuleMatcher = Pattern.compile("(?i)ModuleNotFoundError:\\s*No module named\\s*['\"]([^'\"]+)['\"]").matcher(err);
        if (pythonModuleMatcher.find()) {
            String module = pythonModuleMatcher.group(1);
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.DEPENDENCY_MISSING,
                "Python module '" + module + "' is not installed in the active environment.",
                "Install the missing dependency via pip.",
                "pip install " + module,
                listOf("pip list", "python -m pip install --upgrade pip"),
                true);
        }

        // 6. Node Missing Module
        Matcher nodeModuleMatcher = Pattern.compile("(?i)Cannot find module\\s*['\"]([^'\"]+)['\"]").matcher(err);
        if (nodeModuleMatcher.find()) {
            String module = nodeModuleMatcher.group(1);
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.DEPENDENCY_MISSING,
                "Node.js package '" + module + "' is missing in node_modules.",
                "Install the dependency via npm.",
                "npm install " + module,
                listOf("npm install", "yarn install"),
                true);
        }

        // 7. Git: Not a git repository
        if (err.contains("fatal: not a git repository") || err.contains("not a git repository (or any of the parent directories)")) {
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.GIT_ERROR,
                "The current working directory is not initialized as a Git repository.",
                "Initialize a new repository or navigate to an existing Git workspace.",
                "git init",
                listOf("pwd", "ls -la"),
                true);
        }

        // 8. No space left on device
        if (err.contains("No space left on device") || err.contains("ENOSPC")) {
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.STORAGE_FULL,
                "Device internal storage or app cache partition is completely full.",
                "Clean package archives, temporary build files, and old logs.",
                "pkg clean && apt clean && rm -rf $PREFIX/tmp/*",
                listOf("df -h", "du -sh $PREFIX/var/cache/*"),
                true);
        }

        // 9. Connection refused
        if (err.contains("Connection refused") || err.contains("ECONNREFUSED")) {
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.NETWORK_UNREACHABLE,
                "The remote host rejected the network connection or the target server service is not running.",
                "Verify the server is actively running and listening on the specified host and port.",
                "curl -Iv http://localhost:8080",
                listOf("ping -c 3 127.0.0.1", "ss -tuln"),
                false);
        }

        // 10. Segmentation fault
        if (err.contains("Segmentation fault") || err.contains("SIGSEGV")) {
            return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.GENERAL_ERROR,
                "Process crashed due to invalid memory access or binary ABI incompatibility.",
                "Check for compatible binary architecture (e.g. arm64 vs arm32) or recompile with debugging symbols.",
                "uname -m",
                listOf("dmesg | tail -n 20", "gdb --args ./my_binary"),
                false);
        }

        // Generic fallback
        return new AIErrorDiagnosis(err, AIErrorDiagnosis.ErrorCategory.GENERAL_ERROR,
            "An unclassified execution error occurred.",
            "Review command arguments, inspect system logs, or verify input data.",
            "", listOf("man command", "dmesg | tail"), false);
    }

    private static List<String> listOf(String... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
