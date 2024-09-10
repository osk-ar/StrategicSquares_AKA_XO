package com.example.tictactoe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.model.SharedPrefsHelper;
import com.example.tictactoe.ui.adapters.LanguageRecyclerAdapter;
import com.example.tictactoe.ui.adapters.ThemeRecyclerAdapter;

import carbon.widget.Button;


public class SettingFragment extends Fragment {
    ImageButton backButton;
    RecyclerView language, theme;
    LanguageRecyclerAdapter languageAdapter;
    ThemeRecyclerAdapter themeAdapter;

    Button bg_on, bg_off, clk_on, clk_off;

    private int on_color, off_color;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        backButton = rootView.findViewById(R.id.setting_back_btn);
        backButton.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).onBackPressed();
        });

        // Declare the Language Recycler
        language = rootView.findViewById(R.id.language_recyclerView);
        languageAdapter = new LanguageRecyclerAdapter();
        language.setAdapter(languageAdapter);
        language.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Declare the Theme Recycler
        theme = rootView.findViewById(R.id.theme_recyclerView);
        themeAdapter = new ThemeRecyclerAdapter();
        theme.setAdapter(themeAdapter);
        theme.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Declaresound buttons
        bg_on = rootView.findViewById(R.id.background_sound_on_btn);
        bg_off = rootView.findViewById(R.id.background_sound_off_btn);
        clk_on = rootView.findViewById(R.id.clicking_sound_on_btn);
        clk_off = rootView.findViewById(R.id.clicking_sound_off_btn);

        on_color = ContextCompat.getColor(requireContext(), R.color.stroke_on_color);
        off_color = ContextCompat.getColor(requireContext(), R.color.stroke_off_color);

        // Set initial stroke of sound buttons
        updateButtonStates(bg_on, bg_off, SharedPrefsHelper.backgroundSoundIsEnabled(requireContext()));
        updateButtonStates(clk_on, clk_off, SharedPrefsHelper.clickingSoundIsEnabled(requireContext()));

        // Buttons functionality
        bg_on.setOnClickListener(view -> {
            SharedPrefsHelper.setBackgroundSoundIsEnabled(requireContext(), true);
            updateButtonStates(bg_on, bg_off, true);
            bg_off.invalidate();
            MainActivity.startBgSounds();
            MainActivity.playClickSound(getContext());
        });
        bg_off.setOnClickListener(view -> {
            SharedPrefsHelper.setBackgroundSoundIsEnabled(requireContext(), false);
            updateButtonStates(bg_on, bg_off, false);
            bg_on.invalidate();
            MainActivity.stopBgSounds();
            MainActivity.playClickSound(getContext());
        });

        clk_on.setOnClickListener(view -> {
            SharedPrefsHelper.setClickingSoundIsEnabled(requireContext(), true);
            updateButtonStates(clk_on, clk_off, true);
            clk_off.invalidate();
            MainActivity.playClickSound(getContext());
        });
        clk_off.setOnClickListener(view -> {
            SharedPrefsHelper.setClickingSoundIsEnabled(requireContext(), false);
            updateButtonStates(clk_on, clk_off, false);
            clk_on.invalidate();
        });


        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Helper function to set button stroke
    private void setButtonStroke(Button button, int color, int width) {
        button.setStroke(color);
        button.setStrokeWidth(width);
    }

    // Helper function to update button states
    private void updateButtonStates(Button onButton, Button offButton, boolean isEnabled) {
        setButtonStroke(onButton, isEnabled ? on_color : off_color, isEnabled ? 2 : 1);
        setButtonStroke(offButton, isEnabled ? off_color : on_color, isEnabled ? 1 : 2);
    }
}