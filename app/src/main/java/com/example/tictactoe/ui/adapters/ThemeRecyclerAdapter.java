package com.example.tictactoe.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.model.SharedPrefsHelper;


public class ThemeRecyclerAdapter extends RecyclerView.Adapter<ThemeRecyclerAdapter.ThemeViewHolder> {

    private final String[] THEME_MODES = {"light", "dark", "system"}; // Language codes
    private String[] THEME_TEXT = {"light", "dark", "system"}; // Language codes

    // Constructor
    public ThemeRecyclerAdapter() {
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_button_layout, parent, false);
        return new ThemeViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        String mode = THEME_MODES[position];
        THEME_TEXT[0] = holder.itemView.getContext().getString(R.string.light);
        THEME_TEXT[1] = holder.itemView.getContext().getString(R.string.dark);
        THEME_TEXT[2] = holder.itemView.getContext().getString(R.string.system);
        String text = THEME_TEXT[position];
        holder.themeButton.setText(text);
        holder.themeButton.setOnClickListener(view -> {
            MainActivity.playClickSound(view.getContext());
            setMode(mode);
            SharedPrefsHelper.setTheme(holder.itemView.getContext(), position);
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return THEME_MODES.length;
    }

    // Provide a reference to the views for each data item
    public static class ThemeViewHolder extends RecyclerView.ViewHolder {
        public Button themeButton;

        public ThemeViewHolder(View view) {
            super(view);
            themeButton = view.findViewById(R.id.setting_btn);

        }
    }

    private void setMode(String mode) {

        switch (mode) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
