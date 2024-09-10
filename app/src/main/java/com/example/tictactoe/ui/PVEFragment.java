package com.example.tictactoe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AnimationsClass;

import carbon.widget.Button;

public class PVEFragment extends Fragment {

    private EditText PlayerName;
    private TextView AiName;
    private Button easy, mid, hard, start;
    private ImageButton backButton;

    public short level = 1;
    private int on_color;
    private int off_color;

    public PVEFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_p_v_e, container, false);

        on_color = ContextCompat.getColor(requireContext(), R.color.stroke_on_color);
        off_color = ContextCompat.getColor(requireContext(), R.color.stroke_off_color);

        // Declare buttons
        backButton = rootView.findViewById(R.id.pve_back_btn);
        easy = rootView.findViewById(R.id.easy_level);
        mid = rootView.findViewById(R.id.medium_level);
        hard = rootView.findViewById(R.id.hard_level);
        start = rootView.findViewById(R.id.start_pve_btn);

        // Button functions
        backButton.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).onBackPressed();
        });

        easy.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            level = 1;
            setButtonStroke(requireContext(), easy, on_color, 2);
            setButtonStroke(requireContext(), mid, off_color, 1);
            setButtonStroke(requireContext(), hard, off_color, 1);
            mid.invalidate();
            hard.invalidate();
        });
        mid.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            level = 2;
            setButtonStroke(requireContext(), easy, off_color, 1);
            setButtonStroke(requireContext(), mid, on_color, 2);
            setButtonStroke(requireContext(), hard, off_color, 1);
            easy.invalidate();
            hard.invalidate();
        });
        hard.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            level = 3;
            setButtonStroke(requireContext(), easy, off_color, 1);
            setButtonStroke(requireContext(), mid, off_color, 1);
            setButtonStroke(requireContext(), hard, on_color, 2);
            easy.invalidate();
            mid.invalidate();
        });

        start.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            GameFragment gameFragment = new GameFragment();
            gameFragment.setArguments(createBundle());
            ((MainActivity) requireActivity()).navigateFragment(gameFragment);
        });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PlayerName = view.findViewById(R.id.P_name_pve);
        PlayerName.setOnEditorActionListener((v, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                PlayerName.clearFocus();
                // Hide the keyboard
                InputMethodManager imm = requireContext().getSystemService(InputMethodManager.class);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return true; // Consume the event
            }
            return false; // Not consuming the event}
        });

        AiName = view.findViewById(R.id.AI_name_pve);

    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("player1", PlayerName.getText().toString());
        bundle.putString("player2", AiName.getText().toString());
        bundle.putInt("aiLevel", level);

        return bundle;
    }

    // Helper function to set button stroke
    private void setButtonStroke(Context context, Button button, int color, int width) {
        button.setStroke(color);
        button.setStrokeWidth(AnimationsClass.dpToPx(width, context));
    }
}