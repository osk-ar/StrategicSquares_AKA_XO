package com.example.tictactoe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.FireStoreClass;

import carbon.widget.Button;

public class CreateOrJoinFragment extends Fragment {
    ConstraintLayout holder;
    Button create_btn, join_btn;
    EditText roomCode_et;
    FrameLayout loading_layout;

    String roomCode = "Unknown";

    public CreateOrJoinFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_create_or_join, container, false);

        holder = rootView.findViewById(R.id.selection_holder_layout);
        create_btn = rootView.findViewById(R.id.selection_create_btn);
        join_btn = rootView.findViewById(R.id.selection_join_btn);
        roomCode_et = rootView.findViewById(R.id.selection_roomCode_et);
        loading_layout = rootView.findViewById(R.id.selection_loading_framelayout);

        create_btn.setOnClickListener(view -> {
            MainActivity.playClickSound(getContext());
            loading_layout.setVisibility(View.VISIBLE);

            FireStoreClass.createPrivateRoom(new FireStoreClass.OnRoomCreatedListener() {
                @Override
                public void onSuccess(String roomCode) {
                    loading_layout.setVisibility(View.GONE); // Hide loading
                    PrivateRoomFragment PRF = new PrivateRoomFragment();
                    PRF.setArguments(createBundle(roomCode));
                    ((MainActivity) requireActivity()).clearOneNavigateFragment(PRF);
                }

                @Override
                public void onFailure(Exception e) {
                    showToast("Error: " + e);
                }
            });
        });
        join_btn.setOnClickListener(view -> {
            roomCode = roomCode_et.getText().toString();
            if(roomCode.isEmpty()){
                showToast("Enter a valid room");
                return;
            }
            FireStoreClass.joinRoom(roomCode, (MainActivity) requireActivity(), createBundle(roomCode));
        });
        holder.setOnClickListener(view -> {
            MainActivity.fragmentManager.popBackStack();
        });


        return rootView;
    }

    public static Bundle createBundle(String code) {
        Bundle bundle = new Bundle();
        bundle.putString("roomCode", code);

        return bundle;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}