package com.termux.app.madmax.ui.theme;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

public class TerminalThemeTest {

    @Test
    public void testCatalogContainsEssentialThemes() {
        List<TerminalTheme> themes = TerminalThemeCatalog.getAllThemes();
        Assert.assertNotNull(themes);
        Assert.assertTrue("Catalog should have at least 8 themes", themes.size() >= 8);

        TerminalTheme madmax = TerminalThemeCatalog.getThemeById("madmax_dark");
        Assert.assertNotNull("MadMax Cyber Dark theme must exist", madmax);
        Assert.assertEquals("MadMax Cyber Dark", madmax.getDisplayName());

        TerminalTheme dracula = TerminalThemeCatalog.getThemeById("dracula");
        Assert.assertNotNull("Dracula theme must exist", dracula);

        TerminalTheme nord = TerminalThemeCatalog.getThemeById("nord");
        Assert.assertNotNull("Nord theme must exist", nord);

        TerminalTheme catppuccin = TerminalThemeCatalog.getThemeById("catppuccin_mocha");
        Assert.assertNotNull("Catppuccin Mocha theme must exist", catppuccin);

        TerminalTheme oneDark = TerminalThemeCatalog.getThemeById("one_dark");
        Assert.assertNotNull("One Dark Pro theme must exist", oneDark);

        TerminalTheme solarized = TerminalThemeCatalog.getThemeById("solarized_dark");
        Assert.assertNotNull("Solarized Dark theme must exist", solarized);

        TerminalTheme monokai = TerminalThemeCatalog.getThemeById("monokai_pro");
        Assert.assertNotNull("Monokai Pro theme must exist", monokai);

        TerminalTheme classic = TerminalThemeCatalog.getThemeById("termux_classic");
        Assert.assertNotNull("Termux Classic AMOLED theme must exist", classic);
    }

    @Test
    public void testThemePropertiesSerialization() {
        TerminalTheme theme = TerminalThemeCatalog.getThemeById("madmax_dark");
        Assert.assertNotNull(theme);

        Properties props = theme.toProperties();
        Assert.assertNotNull(props);
        Assert.assertEquals(theme.getBackground(), props.getProperty("background"));
        Assert.assertEquals(theme.getForeground(), props.getProperty("foreground"));
        Assert.assertEquals(theme.getCursor(), props.getProperty("cursor"));

        for (int i = 0; i < 16; i++) {
            Assert.assertEquals(theme.getAnsiColor(i), props.getProperty("color" + i));
        }
    }

    @Test
    public void testThemeAnsiBoundsSafety() {
        TerminalTheme theme = TerminalThemeCatalog.getThemeById("madmax_dark");
        Assert.assertNotNull(theme);

        Assert.assertNotNull(theme.getAnsiColor(0));
        Assert.assertNotNull(theme.getAnsiColor(15));
        // Out of bounds should return foreground safely without throwing
        Assert.assertEquals(theme.getForeground(), theme.getAnsiColor(-1));
        Assert.assertEquals(theme.getForeground(), theme.getAnsiColor(99));
    }
}
