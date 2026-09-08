package com.termux.app.madmax.ui.theme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Curated catalog of developer terminal color schemes.
 */
public final class TerminalThemeCatalog {

    private static final List<TerminalTheme> THEMES = new ArrayList<>();

    static {
        // 1. MadMax Obsidian Cyber
        THEMES.add(new TerminalTheme(
            "madmax_dark",
            "MadMax Cyber Dark",
            "Signature obsidian dark theme with cyber red accents",
            "#0E0E12",
            "#F0F0F4",
            "#FF3B30",
            new String[]{
                "#1C1C22", "#FF3B30", "#34C759", "#FFD60A",
                "#0A84FF", "#BF5AF2", "#64D2FF", "#E5E5EA",
                "#48484A", "#FF453A", "#30D158", "#FFD60A",
                "#0A84FF", "#BF5AF2", "#64D2FF", "#FFFFFF"
            }
        ));

        // 2. Dracula
        THEMES.add(new TerminalTheme(
            "dracula",
            "Dracula",
            "Iconic vampire dark theme with purple and cyan accents",
            "#282A36",
            "#F8F8F2",
            "#F8F8F2",
            new String[]{
                "#21222C", "#FF5555", "#50FA7B", "#F1FA8C",
                "#BD93F9", "#FF79C6", "#8BE9FD", "#F8F8F2",
                "#6272A4", "#FF6E6E", "#69FF94", "#FFFFA5",
                "#D6ACFF", "#FF92DF", "#A4FFFF", "#FFFFFF"
            }
        ));

        // 3. Nord
        THEMES.add(new TerminalTheme(
            "nord",
            "Nord",
            "Arctic, north-bluish clean and calm developer palette",
            "#2E3440",
            "#D8DEE9",
            "#D8DEE9",
            new String[]{
                "#3B4252", "#BF616A", "#A3BE8C", "#EBCB8B",
                "#81A1C1", "#B48EAD", "#88C0D0", "#E5E9F0",
                "#4C566A", "#D08770", "#A3BE8C", "#EBCB8B",
                "#81A1C1", "#B48EAD", "#8FBCBB", "#ECEFF4"
            }
        ));

        // 4. Catppuccin Mocha
        THEMES.add(new TerminalTheme(
            "catppuccin_mocha",
            "Catppuccin Mocha",
            "Soothing modern pastel palette for high-focus coding",
            "#1E1E2E",
            "#CDD6F4",
            "#F5E0DC",
            new String[]{
                "#45475A", "#F38BA8", "#A6E3A1", "#F9E2AF",
                "#89B4FA", "#F5C2E7", "#94E2D5", "#BAC2DE",
                "#585B70", "#F38BA8", "#A6E3A1", "#F9E2AF",
                "#89B4FA", "#F5C2E7", "#94E2D5", "#A6ADC8"
            }
        ));

        // 5. One Dark Pro
        THEMES.add(new TerminalTheme(
            "one_dark",
            "One Dark Pro",
            "Atom and VS Code's iconic dark developer palette",
            "#282C34",
            "#ABB2BF",
            "#528BFF",
            new String[]{
                "#1E2127", "#E06C75", "#98C379", "#D19A66",
                "#61AFEF", "#C678DD", "#56B6C2", "#ABB2BF",
                "#5C6370", "#E06C75", "#98C379", "#E5C07B",
                "#61AFEF", "#C678DD", "#56B6C2", "#FFFFFF"
            }
        ));

        // 6. Solarized Dark
        THEMES.add(new TerminalTheme(
            "solarized_dark",
            "Solarized Dark",
            "Precision-calibrated dark palette designed for reduced eye strain",
            "#002B36",
            "#839496",
            "#93A1A1",
            new String[]{
                "#073642", "#DC322F", "#859900", "#B58900",
                "#268BD2", "#D33682", "#2AA198", "#EEE8D5",
                "#002B36", "#CB4B16", "#586E75", "#657B83",
                "#839496", "#6C71C4", "#93A1A1", "#FDF6E3"
            }
        ));

        // 7. Monokai Pro
        THEMES.add(new TerminalTheme(
            "monokai_pro",
            "Monokai Pro",
            "High contrast vibrant palette with vivid syntax highlights",
            "#2D2A2E",
            "#FCFCFA",
            "#FCFCFA",
            new String[]{
                "#403E41", "#FF6188", "#A9DC76", "#FFD866",
                "#FC9867", "#AB9DF2", "#78DCE8", "#FCFCFA",
                "#727072", "#FF6188", "#A9DC76", "#FFD866",
                "#FC9867", "#AB9DF2", "#78DCE8", "#FFFFFF"
            }
        ));

        // 8. Termux Classic (Pure AMOLED Black)
        THEMES.add(new TerminalTheme(
            "termux_classic",
            "Termux Classic AMOLED",
            "Pure OLED black background with standard ANSI terminal colors",
            "#000000",
            "#FFFFFF",
            "#FFFFFF",
            new String[]{
                "#000000", "#CD0000", "#00CD00", "#CDCD00",
                "#0000EE", "#CD00CD", "#00CDCD", "#E5E5E5",
                "#7F7F7F", "#FF0000", "#00FF00", "#FFFF00",
                "#5C5CFF", "#FF00FF", "#00FFFF", "#FFFFFF"
            }
        ));
    }

    private TerminalThemeCatalog() {}

    @NonNull
    public static List<TerminalTheme> getAllThemes() {
        return Collections.unmodifiableList(THEMES);
    }

    @Nullable
    public static TerminalTheme getThemeById(@NonNull String id) {
        for (TerminalTheme theme : THEMES) {
            if (theme.getId().equalsIgnoreCase(id)) {
                return theme;
            }
        }
        return null;
    }
}
