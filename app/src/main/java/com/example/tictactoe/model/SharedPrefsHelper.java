package com.example.tictactoe.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_SOUNDS_ENABLED = "background_sounds_enabled";
    private static final String KEY_MUSIC_ENABLED = "clicking_sounds_enabled";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_THEME = "theme";

    public static boolean backgroundSoundIsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_SOUNDS_ENABLED, false); // Default to true
    }

    public static void setBackgroundSoundIsEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_SOUNDS_ENABLED, enabled).apply();
    }

    public static boolean clickingSoundIsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_MUSIC_ENABLED, true); // Default to true
    }

    public static void setClickingSoundIsEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply();
    }

    public static int getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_LANGUAGE, 0); // Default to 0 (e.g., English)
    }

    public static void setLanguage(Context context, int language) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_LANGUAGE, language).apply();
    }

    public static int getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, 2); // Default to 2 (e.g., System theme)
    }

    public static void setTheme(Context context, int theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME, theme).apply();
    }
}
