package com.termux.app.madmax.core;

/**
 * Constants used throughout the MadMax extension layer.
 */
public final class MadMaxConstants {

    private MadMaxConstants() {}

    /** MadMax Application Branding */
    public static final String MADMAX_APP_NAME = "MadMax";
    public static final String MADMAX_VERSION = "1.1.0";
    public static final String MADMAX_REPO_URL = "https://github.com/The-habib/Madmax";

    /** Shared preferences file name for MadMax extension configs */
    public static final String MADMAX_PREFERENCES_NAME = "madmax_preferences";

    /** Feature flags SharedPreferences prefix */
    public static final String PREF_FEATURE_FLAG_PREFIX = "feature_flag_";

    /** Dynamic Monet Colors toggle preference key */
    public static final String PREF_KEY_DYNAMIC_COLORS = "madmax_dynamic_colors_enabled";

    /** Developer options preferences */
    public static final String PREF_KEY_DEV_DASHBOARD_ENABLED = "madmax_dev_dashboard_enabled";

    /** Action intent to launch Developer Dashboard */
    public static final String ACTION_LAUNCH_DEV_DASHBOARD = "com.termux.app.madmax.action.DEV_DASHBOARD";
}
