package com.termux.app.madmax.ui.palette;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class CommandPaletteTest {

    @Test
    public void testCommandPaletteItemCreation() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        CommandPaletteItem item = new CommandPaletteItem(
            "test_action",
            "Clear Screen",
            "Wipe terminal buffer",
            CommandPaletteItem.Category.ACTION,
            123,
            () -> triggered.set(true)
        );

        Assert.assertEquals("test_action", item.getId());
        Assert.assertEquals("Clear Screen", item.getTitle());
        Assert.assertEquals("Wipe terminal buffer", item.getSubtitle());
        Assert.assertEquals(CommandPaletteItem.Category.ACTION, item.getCategory());
        Assert.assertEquals(123, item.getIconRes());

        item.getAction().run();
        Assert.assertTrue("Action callback must run", triggered.get());
    }

    @Test
    public void testCommandPaletteItemMatching() {
        CommandPaletteItem item = new CommandPaletteItem(
            "git_status",
            "Git Status",
            "Show working tree status",
            CommandPaletteItem.Category.SNIPPET,
            0,
            () -> {}
        );

        // Empty query matches all
        Assert.assertTrue(item.matches(""));
        Assert.assertTrue(item.matches("   "));

        // Match on title
        Assert.assertTrue(item.matches("git"));
        Assert.assertTrue(item.matches("STATUS"));
        Assert.assertTrue(item.matches("git status"));

        // Match on subtitle
        Assert.assertTrue(item.matches("working tree"));
        Assert.assertTrue(item.matches("tree"));

        // No match
        Assert.assertFalse(item.matches("nonexistentxyz"));
    }

    @Test
    public void testCategoryProperties() {
        for (CommandPaletteItem.Category cat : CommandPaletteItem.Category.values()) {
            Assert.assertNotNull(cat.getLabel());
            Assert.assertFalse(cat.getLabel().isEmpty());
            Assert.assertNotEquals(0, cat.getColor());
        }
    }
}
