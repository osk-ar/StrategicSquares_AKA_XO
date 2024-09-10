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

public class LogInFragment extends Fragment {
    private EditText email_et, password_et;
    private Button login_btn, signup_btn;
    private TextView guest_tv;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);
        email_et = rootView.findViewById(R.id.login_email);
        password_et = rootView.findViewById(R.id.login_password);
        login_btn = rootView.findViewById(R.id.loginPage_login_btn);
        signup_btn = rootView.findViewById(R.id.loginPage_signup_btn);
        guest_tv = rootView.findViewById(R.id.login_guest_tv);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        login_btn.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            String email = email_et.getText().toString();
            String password = password_et.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your email.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your password.", Toast.LENGTH_SHORT).show();
                return;
            }
            AuthClass.logIn((MainActivity) requireActivity(), email, password);
        });
        signup_btn.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).clearAllNavigateFragment(new SignUpFragment());
        });
        guest_tv.setOnClickListener(view1 -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).clearAllNavigateFragment(new HomeFragment());
        });

        // Initial page
        if (AuthClass.isSignedIn()) {
            ((MainActivity) requireActivity()).clearAllNavigateFragment(new HomeFragment());
        }


    }
}