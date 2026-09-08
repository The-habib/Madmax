package com.termux.app.madmax.ui.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.termux.app.TermuxActivity;
import com.termux.app.TermuxService;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalColors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Applies terminal color schemes dynamically in memory and persists them to ~/.termux/colors.properties.
 */
public final class TerminalThemeApplicator {

    private static final String LOG_TAG = "TerminalThemeApplicator";
    private static final String PREF_ACTIVE_THEME = "madmax_active_terminal_theme";

    private TerminalThemeApplicator() {}

    /**
     * Applies the given theme to all terminal sessions and saves to ~/.termux/colors.properties.
     */
    public static boolean applyTheme(@NonNull TermuxActivity activity, @NonNull TerminalTheme theme) {
        try {
            Properties props = theme.toProperties();

            // 1. Save to ~/.termux/colors.properties
            File colorsFile = TermuxConstants.TERMUX_COLOR_PROPERTIES_FILE;
            File parentDir = colorsFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (OutputStream out = new FileOutputStream(colorsFile)) {
                props.store(out, "MadMax Terminal Theme: " + theme.getDisplayName());
            }

            // 2. Update global color scheme in memory
            TerminalColors.COLOR_SCHEME.updateWith(props);

            // 3. Reset colors for all running sessions
            TermuxService service = activity.getTermuxService();
            if (service != null) {
                int size = service.getTermuxSessionsSize();
                for (int i = 0; i < size; i++) {
                    TermuxSession ts = service.getTermuxSession(i);
                    if (ts != null && ts.getTerminalSession() != null && ts.getTerminalSession().getEmulator() != null) {
                        ts.getTerminalSession().getEmulator().mColors.reset();
                    }
                }
            }

            // 4. Update activity background & redraw
            if (activity.getTermuxTerminalSessionClient() != null) {
                activity.getTermuxTerminalSessionClient().updateBackgroundColor();
            }
            if (activity.getTerminalView() != null) {
                activity.getTerminalView().onScreenUpdated();
            }

            // 5. Persist preference
            SharedPreferences prefs = activity.getSharedPreferences("madmax_theme_prefs", Context.MODE_PRIVATE);
            prefs.edit().putString(PREF_ACTIVE_THEME, theme.getId()).apply();

            Toast.makeText(activity, "Applied theme: " + theme.getDisplayName(), Toast.LENGTH_SHORT).show();
            Logger.logInfo(LOG_TAG, "Successfully applied terminal theme: " + theme.getId());
            return true;
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Failed to apply terminal theme: " + theme.getId(), e);
            Toast.makeText(activity, "Failed to apply theme: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @NonNull
    public static String getActiveThemeId(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences("madmax_theme_prefs", Context.MODE_PRIVATE);
        return prefs.getString(PREF_ACTIVE_THEME, "madmax_dark");
    }
}
