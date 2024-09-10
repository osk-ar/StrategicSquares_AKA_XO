package com.example.tictactoe.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AnimationsClass;
import com.example.tictactoe.logic.FireStoreClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class PrivateRoomFragment extends Fragment {
    private android.widget.TextView search_tv, player_name, roomCode;
    private android.widget.ImageButton backButton, copyButton;

    private int searchIndex;
    private Handler searchHandler;
    private Runnable searchRunnable;

    private String roomId;

    public PrivateRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomId = getArguments().getString("roomCode");
        }
        MainActivity.isOnlineRoom = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_private_room, container, false);

        player_name = rootView.findViewById(R.id.P_name_pr);
        player_name.setText(MainActivity.Userdata.get(FireStoreClass.USERNAME_KEY));

        // searching pulse animation
        AnimationsClass.searchAnimation(requireContext(), rootView, R.id.pr_animation_container);

        // text animation function
        {
            String s1 = getString(R.string.waiting_for_other_player);
            String[] textArray = {s1 + ".", s1 + "..", s1 + "..."};
            search_tv = rootView.findViewById(R.id.search_tv_pr);
            int textAnimDelay = 1000;
            searchIndex = 0;
            searchHandler = new Handler();
            searchRunnable = new Runnable() {
                @Override
                public void run() {
                    search_tv.setText(textArray[searchIndex]);
                    searchIndex = (searchIndex + 1) % textArray.length;

                    searchHandler.postDelayed(this, textAnimDelay);
                }
            };
            searchHandler.post(searchRunnable);
        }

        // declare roomCode
        roomCode = rootView.findViewById(R.id.pr_roomcode_tv);
        roomCode.setText(roomId);

        // Declare buttons
        backButton = rootView.findViewById(R.id.pr_back_btn);
        backButton.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            ((MainActivity) requireActivity()).onBackPressed();
        });

        copyButton = rootView.findViewById(R.id.pr_copy_btn);
        copyButton.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Room Code", roomCode.getText().toString());
            clipboard.setPrimaryClip(clip);

            // Optionally show a toast to indicate success
            Toast.makeText(requireActivity(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FireStoreClass.isPlayer2Joined(requireContext(), roomId, () -> {
            if (isAdded()) { // Check if the fragment is still added
                MainActivity.isGameStarted = true;
                OnlineGameFragment OGF = new OnlineGameFragment();
                OGF.setArguments(CreateOrJoinFragment.createBundle(roomId));
                ((MainActivity) requireActivity()).clearOneNavigateFragment(OGF);
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacks(searchRunnable);
        if(!MainActivity.isGameStarted && MainActivity.isOnlineRoom){
            FireStoreClass.deleteDocument(FireStoreClass.ROOMS_COLLECTION_KEY, roomId,
                    aVoid -> {
                        MainActivity.isOnlineRoom = false;
                    },
                    e -> {
                        Toast.makeText(getContext(), "Error deleting room", Toast.LENGTH_SHORT).show();
                    });
        }



    }
}

