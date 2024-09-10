package com.example.tictactoe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;

import carbon.widget.Button;


public class PVPFragment extends Fragment {
    EditText P1, P2;
    ImageButton backButton;
    Button start;


    public PVPFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_p_v_p, container, false);

        backButton = rootView.findViewById(R.id.pvp_back_btn);
        backButton.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).onBackPressed();
        });

        P1 = rootView.findViewById(R.id.P1_name_pvp);
        P2 = rootView.findViewById(R.id.P2_name_pvp);
        P1.setOnEditorActionListener((v, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                P1.clearFocus();
                // Hide the keyboard
                InputMethodManager imm = requireContext().getSystemService(InputMethodManager.class);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return true; // Consume the event
            }
            return false; // Not consuming the event}
        });
        P2.setOnEditorActionListener((v, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                P2.clearFocus();
                // Hide the keyboard
                InputMethodManager imm = requireContext().getSystemService(InputMethodManager.class);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return true; // Consume the event
            }
            return false; // Not consuming the event}
        });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        start = view.findViewById(R.id.start_pvp_btn);
        start.setOnClickListener(view1 -> {

            MainActivity.playClickSound(getContext());
            GameFragment gameFragment = new GameFragment();
            gameFragment.setArguments(createBundle());
            ((MainActivity) requireActivity()).navigateFragment(gameFragment);


        });
    }

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("player1", P1.getText().toString());
        bundle.putString("player2", P2.getText().toString());
        bundle.putInt("aiLevel", 0);

        return bundle;
    }

}