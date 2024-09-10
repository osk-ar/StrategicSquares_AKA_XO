package com.example.tictactoe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AuthClass;

import carbon.widget.Button;

public class SignUpFragment extends Fragment {
    private EditText name_et, email_et, password_et;
    private Button login_btn, signup_btn;
    private TextView guest_tv;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        name_et = rootView.findViewById(R.id.signup_name);
        email_et = rootView.findViewById(R.id.signup_email);
        password_et = rootView.findViewById(R.id.signup_password);
        signup_btn = rootView.findViewById(R.id.signupPage_signup_btn);
        login_btn = rootView.findViewById(R.id.signupPage_login_btn);
        guest_tv = rootView.findViewById(R.id.signup_guest_tv);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signup_btn.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            String name = name_et.getText().toString();
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your email.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your password.", Toast.LENGTH_SHORT).show();
                return;
            }
            AuthClass.signUp((MainActivity) requireActivity(), name, email, password);
        });
        login_btn.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).clearAllNavigateFragment(new LogInFragment());
        });
        guest_tv.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).clearAllNavigateFragment(new HomeFragment());
        });

    }
}