package com.termux.app.madmax.ui.actions;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SnippetCatalogTest {

    @Test
    public void testCategoriesArePopulated() {
        List<String> categories = SnippetCatalog.getCategories();
        Assert.assertNotNull(categories);
        Assert.assertTrue(categories.size() >= 5);
        Assert.assertTrue(categories.contains(SnippetCatalog.CAT_ALL));
        Assert.assertTrue(categories.contains(SnippetCatalog.CAT_PACKAGES));
        Assert.assertTrue(categories.contains(SnippetCatalog.CAT_SYSTEM));
        Assert.assertTrue(categories.contains(SnippetCatalog.CAT_GIT));
        Assert.assertTrue(categories.contains(SnippetCatalog.CAT_NETWORK));
        Assert.assertTrue(categories.contains(SnippetCatalog.CAT_DEV));
    }

    @Test
    public void testSnippetsCatalogContent() {
        List<TerminalSnippet> all = SnippetCatalog.getAllSnippets();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.size() >= 15);

        for (TerminalSnippet s : all) {
            Assert.assertNotNull("Snippet title must not be null", s.getTitle());
            Assert.assertFalse("Snippet title must not be empty", s.getTitle().trim().isEmpty());
            Assert.assertNotNull("Snippet description must not be null", s.getDescription());
            Assert.assertNotNull("Snippet command must not be null", s.getCommand());
            Assert.assertFalse("Snippet command must not be empty", s.getCommand().trim().isEmpty());
            Assert.assertNotNull("Snippet category must not be null", s.getCategory());
        }
    }

    @Test
    public void testCategoryFiltering() {
        List<TerminalSnippet> gitSnippets = SnippetCatalog.getSnippetsByCategory(SnippetCatalog.CAT_GIT);
        Assert.assertFalse(gitSnippets.isEmpty());
        for (TerminalSnippet s : gitSnippets) {
            Assert.assertEquals(SnippetCatalog.CAT_GIT, s.getCategory());
        }

        List<TerminalSnippet> pkgSnippets = SnippetCatalog.getSnippetsByCategory(SnippetCatalog.CAT_PACKAGES);
        Assert.assertFalse(pkgSnippets.isEmpty());
        for (TerminalSnippet s : pkgSnippets) {
            Assert.assertEquals(SnippetCatalog.CAT_PACKAGES, s.getCategory());
        }

        List<TerminalSnippet> allViaFilter = SnippetCatalog.getSnippetsByCategory(SnippetCatalog.CAT_ALL);
        Assert.assertEquals(SnippetCatalog.getAllSnippets().size(), allViaFilter.size());
    }

    @Test
    public void testCriticalSnippetsPresence() {
        List<TerminalSnippet> all = SnippetCatalog.getAllSnippets();
        boolean foundPkgUpdate = false;
        boolean foundGitStatus = false;
        boolean foundLs = false;
        boolean foundPing = false;

        for (TerminalSnippet s : all) {
            if (s.getCommand().contains("pkg update")) foundPkgUpdate = true;
            if (s.getCommand().contains("git status")) foundGitStatus = true;
            if (s.getCommand().contains("ls -lah")) foundLs = true;
            if (s.getCommand().contains("ping -c")) foundPing = true;
        }

        Assert.assertTrue("pkg update snippet must exist", foundPkgUpdate);
        Assert.assertTrue("git status snippet must exist", foundGitStatus);
        Assert.assertTrue("ls -lah snippet must exist", foundLs);
        Assert.assertTrue("ping snippet must exist", foundPing);
    }
}
