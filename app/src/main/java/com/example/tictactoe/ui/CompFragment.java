package com.example.tictactoe.ui;

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
import com.example.tictactoe.logic.AuthClass;
import com.example.tictactoe.logic.FireStoreClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;


public class CompFragment extends Fragment {
    private int searchIndex;
    private Handler searchHandler;
    private Runnable searchRunnable;

    private android.widget.TextView search_tv, player_name;
    private android.widget.ImageButton backButton;

    String rId;
    boolean gotrId = false;

    public CompFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnlineRoom = true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comp, container, false);

        player_name = rootView.findViewById(R.id.P_name_comp);
        player_name.setText(MainActivity.Userdata.get(FireStoreClass.USERNAME_KEY));


        // searching pulse animation
        AnimationsClass.searchAnimation(requireContext(), rootView, R.id.animation_container);

        // text animation function
        {
            String s1 = getString(R.string.searching_for_opponent);
            String[] textArray = {s1 + ".", s1 + "..", s1 + "..."};
            search_tv = rootView.findViewById(R.id.search_tv_comp);
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

        // Declare buttons
        backButton = rootView.findViewById(R.id.comp_back_btn);
        backButton.setOnClickListener(view -> {
                    MainActivity.playClickSound(getContext());
                    ((MainActivity) requireActivity()).onBackPressed();
                });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            FireStoreClass.isCollectionEmpty(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    // Collection is empty, do something
                    if (document == null && isAdded()) {
                        Context context = requireContext();
                        FireStoreClass.createCompRoom(((MainActivity) requireActivity()), (String roomId) -> {
                            Toast.makeText(context, "No Document, should get rId", Toast.LENGTH_SHORT).show();
                            MainActivity.isGameStarted = true;
                            if (isAdded()) {
                                OnlineGameFragment OGF = new OnlineGameFragment();
                                OGF.setArguments(CreateOrJoinFragment.createBundle(roomId));
                                ((MainActivity) requireActivity()).clearOneNavigateFragment(OGF);
                            }

                            FireStoreClass.deleteDocument(FireStoreClass.COMP_COLLECTION_KEY, AuthClass.getUser().getUid(), unused -> {
                            }, e -> {
                            });

                        }, roomId -> {
                            rId = roomId;
                            gotrId = true;
                        });
                    }
                    else {
                        // Collection is not empty, do something else
                        MainActivity.isGameStarted = true;
                        String docId = document.getId();
                        Map<String, Object> docData = document.getData();
                        assert docData != null;
                        String roomId = (String) docData.get(FireStoreClass.ROOM_ID_KEY);
                        if(isAdded()){
                            Context context = requireContext();
                            FireStoreClass.updateFieldValue(context,FireStoreClass.COMP_COLLECTION_KEY,docId,FireStoreClass.STATUS_KEY,"inProgress");
                            FireStoreClass.joinRoom(roomId, (MainActivity) requireActivity(), CreateOrJoinFragment.createBundle(roomId));
                        }

                    }
                }
                else {
                    // Handle the error
                    Exception e = task.getException();
                    Toast.makeText(requireContext(), "Error checking collection", Toast.LENGTH_SHORT).show();
                }
            });




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacks(searchRunnable);
        if(!MainActivity.isGameStarted && MainActivity.isOnlineRoom){
            while (!gotrId){
                System.out.println("Waiting for RoomId");
            }
                FireStoreClass.deleteDocument(FireStoreClass.ROOMS_COLLECTION_KEY, rId,
                        aVoid -> {
                            MainActivity.isOnlineRoom = false;
                        },
                        e -> {
                            Toast.makeText(getContext(), "Error deleting room", Toast.LENGTH_SHORT).show();
                        });


            FireStoreClass.deleteDocument(FireStoreClass.COMP_COLLECTION_KEY, AuthClass.getUser().getUid(),
                    aVoid -> {
                        MainActivity.isOnlineRoom = false;
                    },
                    e -> {
                        Toast.makeText(getContext(), "Error deleting room", Toast.LENGTH_SHORT).show();
                    });

        }
    }
}