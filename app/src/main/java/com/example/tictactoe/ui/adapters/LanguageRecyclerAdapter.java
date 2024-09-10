package com.example.tictactoe.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.model.SharedPrefsHelper;

import java.util.Locale;


public class LanguageRecyclerAdapter extends RecyclerView.Adapter<LanguageRecyclerAdapter.LanguageViewHolder> {

    private static final String[] LANGUAGES = {"en", "ar", "es", "de", "ja"}; // Language codes

    // Constructor
    public LanguageRecyclerAdapter() {
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_button_layout, parent, false);
        return new LanguageViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        String languageCode = LANGUAGES[position];
        holder.languageButton.setText(getLanguageName(languageCode));
        holder.languageButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            MainActivity.playClickSound(context);
            setLocale(context, languageCode);
            SharedPrefsHelper.setLanguage(holder.itemView.getContext(), position);
            if (context instanceof Activity) {
                ((Activity) context).recreate();
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return LANGUAGES.length;
    }

    // Provide a reference to the views for each data item
    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        public Button languageButton;

        public LanguageViewHolder(View view) {
            super(view);
            languageButton = view.findViewById(R.id.setting_btn);
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

    private String getLanguageName(String languageCode) {

        switch (languageCode) {
            case "en":
                return "English";
            case "es":
                return "Español";
            case "ar":
                return "العربية";
            case "de":
                return "Deutsch";
            case "ja":
                return "日本語";
            default:
                return "Unknown";
        }
    }
}
