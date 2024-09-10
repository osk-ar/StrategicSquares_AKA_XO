package com.example.tictactoe;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tictactoe.logic.FireStoreClass;
import com.example.tictactoe.model.SharedPrefsHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    public static boolean isOnlineRoom = false;
    public static boolean isGameStarted = false;
    int language, theme;
    public static MediaPlayer bg_mediaPlayer, clk_mediaPlayer;
    public static Map<String, String> Userdata = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize theme
        theme = SharedPrefsHelper.getTheme(this);
        setMode(theme);

        // initialize language
        language = SharedPrefsHelper.getLanguage(this);
        setLanguage(this, language);

        // initialize mediaPlayer
        bg_mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        if (SharedPrefsHelper.backgroundSoundIsEnabled(this)) {
            bg_mediaPlayer.start();
            bg_mediaPlayer.setLooping(true);
        }

        // initialize profile
        Userdata.put(FireStoreClass.USERNAME_KEY, "Guest");
        Userdata.put(FireStoreClass.POINTS_KEY, "0");


        // IDK what is this   --aka--   default activity settings
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragmentContainerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // declare fragmentManager for navigation
        fragmentManager = getSupportFragmentManager();

    }

    // Navigation functions
    public void navigateFragment(Fragment destination) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, destination)
                .addToBackStack(null)
                .commit();

    }

    public void clearOneNavigateFragment(Fragment destination) {
        if (!fragmentManager.isStateSaved()) {
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, destination)
                .addToBackStack(null)
                .commit();
    }

    public void clearAllNavigateFragment(Fragment destination) {
        if (!fragmentManager.isStateSaved()) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, destination)
                .commit();

    }

    public void showDialogFragment(Fragment destination) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, 0)
                .add(R.id.fragmentContainerView, destination)
                .addToBackStack(null)
                .commit();

    }

    //  Language functions
    private void setLanguage(Context context, int language) {
        switch (language) {
            case 0:
                setLocale(context, "en");
                break;
            case 1:
                setLocale(context, "ar");
                break;
            case 2:
                setLocale(context, "es");
                break;
            case 3:
                setLocale(context, "de");
                break;
            default:
                setLocale(context, "ja");

        }
    }

    private void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, dm);
    }

    //  Theme function
    private void setMode(int mode) {
        switch (mode) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    // Sounds function
    public static void startBgSounds() {
        bg_mediaPlayer.start();
    }

    public static void stopBgSounds() {
        bg_mediaPlayer.pause();
    }

    public static void playClickSound(Context context) {
        if (!SharedPrefsHelper.clickingSoundIsEnabled(context)) {
            return;
        }

        clk_mediaPlayer = MediaPlayer.create(context, R.raw.click_sound);
        clk_mediaPlayer.start();
        clk_mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }


    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bg_mediaPlayer != null) {
            bg_mediaPlayer.stop();
            bg_mediaPlayer.release();
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}