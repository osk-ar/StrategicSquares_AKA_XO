package com.example.tictactoe.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AnimationsClass;
import com.example.tictactoe.logic.AuthClass;
import com.example.tictactoe.logic.FireStoreClass;

import java.util.Map;

import carbon.widget.Button;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        AnimationsClass.waveAnimation(rootView, requireContext());

        Button pvp = rootView.findViewById(R.id.pvp_home_btn);
        Button pve = rootView.findViewById(R.id.pve_home_btn);
        Button comp = rootView.findViewById(R.id.competitive_home_btn);
        Button privateRoom = rootView.findViewById(R.id.privateRoom_home_btn);
        Button setting = rootView.findViewById(R.id.settings_home_btn);
        Button profile = rootView.findViewById(R.id.profile_home_btn);

        View.OnClickListener navigationListener = view -> {
            MainActivity activity = (MainActivity) requireActivity();
            MainActivity.playClickSound(getContext());

            if (view.getId() == R.id.pvp_home_btn) {
                activity.navigateFragment(new PVPFragment());
            } else if (view.getId() == R.id.pve_home_btn) {
                activity.navigateFragment(new PVEFragment());
            } else if (view.getId() == R.id.competitive_home_btn) {
                if (validUser(requireContext())) {
                    activity.navigateFragment(new CompFragment());
                } else {
                    MainActivity.showToast(requireContext(), "Check internet connection & Login to unlock");
                }
            } else if (view.getId() == R.id.privateRoom_home_btn) {
                if (validUser(requireContext())) {
                    activity.showDialogFragment(new CreateOrJoinFragment());
                } else {
                    MainActivity.showToast(requireContext(), "Check internet connection & Login to unlock");
                }
            } else if (view.getId() == R.id.settings_home_btn) {
                activity.navigateFragment(new SettingFragment());
            } else if (view.getId() == R.id.profile_home_btn) {
                activity.navigateFragment(new ProfileFragment());
            }
        };

        pvp.setOnClickListener(navigationListener);
        pve.setOnClickListener(navigationListener);
        comp.setOnClickListener(navigationListener);
        privateRoom.setOnClickListener(navigationListener);
        setting.setOnClickListener(navigationListener);
        profile.setOnClickListener(navigationListener);

        return rootView;
    }

    private boolean validUser(Context context) {
        return AuthClass.isSignedIn() && isNetworkConnected(context);
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}