package com.example.tictactoe.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AuthClass;
import com.example.tictactoe.logic.FireStoreClass;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import carbon.widget.Button;

public class ProfileFragment extends Fragment {
    ImageButton back_btn;
    TextView name_tv, email_tv, points_tv;
    Button login_btn, signup_btn, logout_btn;
    FrameLayout loading_frame;
    ConstraintLayout main_frame;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        loading_frame = rootView.findViewById(R.id.profile_loading_frame);
        main_frame = rootView.findViewById(R.id.profile_main_frame);

        back_btn = rootView.findViewById(R.id.profile_back_btn);

        name_tv = rootView.findViewById(R.id.profile_name);
        email_tv = rootView.findViewById(R.id.profile_email);
        points_tv = rootView.findViewById(R.id.profile_points);

        login_btn = rootView.findViewById(R.id.profilePage_login_btn);
        signup_btn = rootView.findViewById(R.id.profilePage_signup_btn);
        logout_btn = rootView.findViewById(R.id.profilePage_logout_btn);

        back_btn.setOnClickListener(v -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).onBackPressed();
        });

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        login_btn.setOnClickListener(v -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).navigateFragment(new LogInFragment());
        });
        signup_btn.setOnClickListener(v -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).navigateFragment(new SignUpFragment());
        });
        logout_btn.setOnClickListener(v -> {
            MainActivity.playClickSound(getContext());
            AuthClass.logOut();
            ((MainActivity) requireActivity()).navigateFragment(new LogInFragment());
        });


        MainActivity.Userdata.put(FireStoreClass.USERNAME_KEY, "Guest");
        MainActivity.Userdata.put(FireStoreClass.POINTS_KEY, "0");
        name_tv.setText("" + MainActivity.Userdata.get(FireStoreClass.USERNAME_KEY));
        points_tv.setText("" + MainActivity.Userdata.get(FireStoreClass.POINTS_KEY));

        if (AuthClass.isSignedIn()) {
            FirebaseUser user = AuthClass.getUser();
            login_btn.setVisibility(View.INVISIBLE);
            signup_btn.setVisibility(View.INVISIBLE);
            logout_btn.setVisibility(View.VISIBLE);
            email_tv.setText(user.getEmail());

            FireStoreClass.getUserData(AuthClass.getUser().getUid(), new FireStoreClass.UserDataListener() {
                @Override
                public void onSuccess(Map<String, String> userData) {
                    MainActivity.Userdata.put(FireStoreClass.USERNAME_KEY, userData.get(FireStoreClass.USERNAME_KEY));
                    MainActivity.Userdata.put(FireStoreClass.POINTS_KEY, userData.get(FireStoreClass.POINTS_KEY));
                    name_tv.setText("" + MainActivity.Userdata.get(FireStoreClass.USERNAME_KEY));
                    points_tv.setText("" + MainActivity.Userdata.get(FireStoreClass.POINTS_KEY));
                    main_frame.setVisibility(View.VISIBLE);
                    loading_frame.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onFailure(Exception e) {
                    MainActivity.showToast(requireContext(), "Error occurred: " + e);
                }
            });
        }
        else {
            main_frame.setVisibility(View.VISIBLE);
            loading_frame.setVisibility(View.INVISIBLE);
        }



    }
}