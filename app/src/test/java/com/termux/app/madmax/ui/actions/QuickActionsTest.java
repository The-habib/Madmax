package com.termux.app.madmax.ui.actions;

import com.termux.shared.termux.settings.properties.TermuxPropertyConstants;

import org.junit.Assert;
import org.junit.Test;

public class QuickActionsTest {

    @Test
    public void testTabConstants() {
        Assert.assertEquals(0, QuickActionsBottomSheet.TAB_CONTROLS);
        Assert.assertEquals(1, QuickActionsBottomSheet.TAB_ACCESSIBILITY);
        Assert.assertEquals(2, QuickActionsBottomSheet.TAB_SNIPPETS);
        Assert.assertEquals(3, QuickActionsBottomSheet.TAB_AI);
        Assert.assertEquals(4, QuickActionsBottomSheet.TAB_SESSIONS);
    }

    @Test
    public void testDefaultExtraKeysContainMenuAndPaste() {
        String defaultExtraKeys = TermuxPropertyConstants.DEFAULT_IVALUE_EXTRA_KEYS;
        Assert.assertNotNull(defaultExtraKeys);
        Assert.assertTrue("Default extra keys must contain MENU key", defaultExtraKeys.contains("MENU"));
        Assert.assertTrue("Default extra keys must contain AI popup", defaultExtraKeys.contains("AI"));
        Assert.assertTrue("Default extra keys must contain PASTE key", defaultExtraKeys.contains("PASTE"));
    }

    @Test
    public void testTerminalActionCallbackDefaultsDoNotThrow() {
        QuickActionsBottomSheet.TerminalActionCallback callback = new QuickActionsBottomSheet.TerminalActionCallback() {};
        callback.onInsertText("test");
        callback.onExecuteCommand("echo hi");
        callback.onPasteFromClipboard();
        callback.onSendInterrupt();
        callback.onSendSuspend();
        callback.onSendEof();
        callback.onClearScreen();
        callback.onResetTerminal();
        callback.onChangeFontSize(true);
        callback.onResetFontSize();
        callback.onToggleKeyboard();
        callback.onToggleFullscreen();
        callback.onToggleWakeLock();
        callback.onNewSession(false);
        callback.onRenameSession();
        callback.onOpenSessionDrawer();
        callback.onScrollToTop();
        callback.onScrollToBottom();
        callback.onOpenAIWorkspace();
        callback.onOpenCommandPalette();
        callback.onOpenTerminalThemes();
        Assert.assertEquals(14, callback.getCurrentFontSize());
        Assert.assertFalse(callback.isWakeLockHeld());
        Assert.assertNull(callback.onCaptureTranscript());
    }
}
