package com.termux.app.madmax.ui.actions;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Built-in catalog of curated, high-utility terminal snippets for quick mobile execution.
 */
public class SnippetCatalog {

    public static final String CAT_ALL = "All";
    public static final String CAT_PACKAGES = "Packages";
    public static final String CAT_SYSTEM = "System";
    public static final String CAT_GIT = "Git";
    public static final String CAT_NETWORK = "Network";
    public static final String CAT_DEV = "Dev Tools";

    private static final List<TerminalSnippet> ALL_SNIPPETS = new ArrayList<>();

    static {
        // Packages
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_PACKAGES, "Update & Upgrade",
            "Update repository indices and upgrade all installed packages",
            "pkg update && pkg upgrade -y", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_PACKAGES, "Install Essential Tools",
            "Install git, curl, wget, jq, tmux, and htop",
            "pkg install git curl wget jq tmux htop -y", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_PACKAGES, "Search Package",
            "Search for an available package in repositories",
            "pkg search ", false));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_PACKAGES, "List Installed Packages",
            "List all packages currently installed",
            "pkg list-installed", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_PACKAGES, "Clean Package Cache",
            "Clear cached package archives and free disk storage",
            "pkg clean && apt autoremove -y", true));

        // System
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_SYSTEM, "Detailed File List",
            "List all files including hidden with human sizes",
            "ls -lah", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_SYSTEM, "Disk Space Usage",
            "Show available storage across mounted file systems",
            "df -h", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_SYSTEM, "Memory / RAM Usage",
            "Display free and used memory in megabytes",
            "free -m", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_SYSTEM, "System Architecture",
            "Display Linux kernel version, machine architecture and OS info",
            "uname -a", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_SYSTEM, "Process Snapshot",
            "Print active running processes snapshot sorted by resource usage",
            "top -b -n 1", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_SYSTEM, "System Uptime & Load",
            "Show system uptime, active users and load averages",
            "uptime", true));

        // Git
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_GIT, "Git Status",
            "Inspect current branch state and staged/unstaged changes",
            "git status", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_GIT, "Git Recent Log",
            "Show the last 10 commits concisely on single lines",
            "git log --oneline -n 10", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_GIT, "Git Diff",
            "Inspect differences in working tree files",
            "git diff", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_GIT, "Git Pull",
            "Fetch and integrate remote changes into current branch",
            "git pull", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_GIT, "Git Branches",
            "List all local and tracking remote branches",
            "git branch -a", true));

        // Network
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_NETWORK, "IP Addresses & Links",
            "Display IP addresses and network interface properties",
            "ip addr", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_NETWORK, "Ping Google DNS",
            "Send 4 ICMP echo packets to test internet connectivity",
            "ping -c 4 8.8.8.8", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_NETWORK, "HTTP Response Headers",
            "Inspect server headers without downloading response body",
            "curl -I https://google.com", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_NETWORK, "Listening Ports (ss)",
            "Display listening TCP and UDP sockets with port numbers",
            "ss -tulnp", true));

        // Dev Tools
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_DEV, "Python 3 REPL",
            "Launch interactive Python 3 shell interpreter",
            "python3", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_DEV, "Node.js Version",
            "Print installed Node.js runtime version",
            "node -v", true));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_DEV, "Compress to Tarball",
            "Archive current folder into compressed gzip archive",
            "tar -czvf archive.tar.gz .", false));
        ALL_SNIPPETS.add(new TerminalSnippet(CAT_DEV, "Extract Tarball",
            "Extract compressed gzip archive in current directory",
            "tar -xzvf archive.tar.gz", false));
    }

    @NonNull
    public static List<TerminalSnippet> getAllSnippets() {
        return Collections.unmodifiableList(ALL_SNIPPETS);
    }

    @NonNull
    public static List<TerminalSnippet> getSnippetsByCategory(@NonNull String category) {
        if (CAT_ALL.equalsIgnoreCase(category)) {
            return getAllSnippets();
        }
        List<TerminalSnippet> filtered = new ArrayList<>();
        for (TerminalSnippet s : ALL_SNIPPETS) {
            if (s.getCategory().equalsIgnoreCase(category)) {
                filtered.add(s);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    @NonNull
    public static List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add(CAT_ALL);
        categories.add(CAT_PACKAGES);
        categories.add(CAT_SYSTEM);
        categories.add(CAT_GIT);
        categories.add(CAT_NETWORK);
        categories.add(CAT_DEV);
        return categories;
    }
}
