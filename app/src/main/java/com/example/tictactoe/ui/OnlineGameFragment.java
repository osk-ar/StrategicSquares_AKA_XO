package com.example.tictactoe.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AuthClass;
import com.example.tictactoe.logic.FireStoreClass;

import java.util.ArrayList;
import java.util.Objects;

public class OnlineGameFragment extends Fragment {
    TextView player1, player2, turn_tv;
    TextView spot11, spot12, spot13, spot21, spot22, spot23, spot31, spot32, spot33;
    FrameLayout inputBlocker;

    String player1_name, player2_name, p1Uid, p2Uid, roomId, turnUID, turn;
    String[] boardStatus;
    String player1_char = "O";
    String player2_char = "X";
    String currentPlayer = player1_char;

    public OnlineGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomId = getArguments().getString("roomCode");
        }
        MainActivity.isOnlineRoom = true;
        MainActivity.isGameStarted = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_online_game, container, false);

        spot11 = rootView.findViewById(R.id.o_textView11);
        spot12 = rootView.findViewById(R.id.o_textView12);
        spot13 = rootView.findViewById(R.id.o_textView13);
        spot21 = rootView.findViewById(R.id.o_textView21);
        spot22 = rootView.findViewById(R.id.o_textView22);
        spot23 = rootView.findViewById(R.id.o_textView23);
        spot31 = rootView.findViewById(R.id.o_textView31);
        spot32 = rootView.findViewById(R.id.o_textView32);
        spot33 = rootView.findViewById(R.id.o_textView33);
        player1 = rootView.findViewById(R.id.ogame_player1_name);
        player2 = rootView.findViewById(R.id.ogame_player2_name);
        turn_tv = rootView.findViewById(R.id.ogame_player_turn);
        inputBlocker = rootView.findViewById(R.id.ogame_turn_layout);

        // Initial state
        FireStoreClass.getFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.PLAYER1_KEY, fieldValue -> {
            if (fieldValue != null) {
                p1Uid = fieldValue.toString();
                setCurrentPlayer();

                FireStoreClass.getFieldValue(requireContext(), FireStoreClass.USERS_COLLECTION_KEY, p1Uid, FireStoreClass.USERNAME_KEY, innerFieldValue -> {
                    if (innerFieldValue != null) {
                        player1_name = innerFieldValue.toString();
                        player1.setText(player1_name);
                    } else {
                        Toast.makeText(requireContext(), "Error getting p1_name", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Error getting p1Uid", Toast.LENGTH_SHORT);
            }
        });
        FireStoreClass.getFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.PLAYER2_KEY, fieldValue -> {
            if (fieldValue != null) {
                p2Uid = fieldValue.toString();

                FireStoreClass.getFieldValue(requireContext(), FireStoreClass.USERS_COLLECTION_KEY, p2Uid, FireStoreClass.USERNAME_KEY, innerFieldValue -> {
                    if (innerFieldValue != null) {
                        player2_name = innerFieldValue.toString();
                        player2.setText(player2_name);
                    } else {
                        Toast.makeText(requireContext(), "Error getting p2_name", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Error getting p2Uid", Toast.LENGTH_SHORT);
            }
        });
        FireStoreClass.getFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.TURN_KEY, fieldValue -> {
            if (fieldValue != null) {
                turnUID = fieldValue.toString();
                stopInput();

                FireStoreClass.getFieldValue(requireContext(), FireStoreClass.USERS_COLLECTION_KEY, turnUID, FireStoreClass.USERNAME_KEY, innerFieldValue -> {
                    if (innerFieldValue != null) {
                        turn = innerFieldValue.toString();
                        turn_tv.setText(turn + "'s Turn");
                    } else {
                        Toast.makeText(requireContext(), "Error getting turn", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Error getting turnUID", Toast.LENGTH_SHORT);
            }
        });
        FireStoreClass.getFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.GAMESTATE_KEY, fieldValue -> {
            ArrayList<String> arr = (ArrayList<String>) fieldValue;
            boardStatus = arr.toArray(new String[0]);

        });
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spot11.setOnClickListener(v -> handlePlayerMove(spot11, (short) 0));
        spot12.setOnClickListener(v -> handlePlayerMove(spot12, (short) 1));
        spot13.setOnClickListener(v -> handlePlayerMove(spot13, (short) 2));
        spot21.setOnClickListener(v -> handlePlayerMove(spot21, (short) 3));
        spot22.setOnClickListener(v -> handlePlayerMove(spot22, (short) 4));
        spot23.setOnClickListener(v -> handlePlayerMove(spot23, (short) 5));
        spot31.setOnClickListener(v -> handlePlayerMove(spot31, (short) 6));
        spot32.setOnClickListener(v -> handlePlayerMove(spot32, (short) 7));
        spot33.setOnClickListener(v -> handlePlayerMove(spot33, (short) 8));


        FireStoreClass.playerMoved((MainActivity) requireActivity(), roomId, () -> {
            FireStoreClass.getFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.TURN_KEY, fieldValue -> {
                if(isAdded()){
                if (fieldValue != null ) {
                    turnUID = fieldValue.toString();
                    stopInput();

                    FireStoreClass.getFieldValue(requireContext(), FireStoreClass.USERS_COLLECTION_KEY, turnUID, FireStoreClass.USERNAME_KEY, innerFieldValue -> {
                        if (innerFieldValue != null) {
                            turn = innerFieldValue.toString();
                            turn_tv.setText(turn + "'s Turn");
                        } else {
                            Toast.makeText(requireContext(), "Error getting turn", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Error getting turnUID", Toast.LENGTH_SHORT).show();
                }
                }
            });
            FireStoreClass.getFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.GAMESTATE_KEY, fieldValue -> {
                ArrayList<String> arr = (ArrayList<String>) fieldValue;
                boardStatus = arr.toArray(new String[0]);

                spot11.setText(boardStatus[0]);
                spot12.setText(boardStatus[1]);
                spot13.setText(boardStatus[2]);
                spot21.setText(boardStatus[3]);
                spot22.setText(boardStatus[4]);
                spot23.setText(boardStatus[5]);
                spot31.setText(boardStatus[6]);
                spot32.setText(boardStatus[7]);
                spot33.setText(boardStatus[8]);

                if (isGameFinished(currentPlayer, boardStatus) && isAdded()) {
                    FireStoreClass.updateFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.STATUS_KEY, "finished");
                }
            });

        });
    }

    private void handlePlayerMove(TextView textView, short index) {
        // check spot is empty
        if (!boardStatus[index].equals(" ")) {
            return;
        }

        // do move
        MainActivity.playClickSound(getContext());
        textView.setText(currentPlayer);
        boardStatus[index] = currentPlayer;

        // update database
        String nextUid;
        if (Objects.equals(currentPlayer, player1_char))
            nextUid = p2Uid;
        else
            nextUid = p1Uid;
        FireStoreClass.updateFieldValue(requireContext(), FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.GAMESTATE_KEY, boardStatus, FireStoreClass.TURN_KEY, nextUid);
    }

    private void stopInput() {
        if (Objects.equals(turnUID, AuthClass.getUser().getUid()))
            inputBlocker.setVisibility(View.INVISIBLE);
        else
            inputBlocker.setVisibility(View.VISIBLE);
    }

    private void setCurrentPlayer() {
        if (Objects.equals(p1Uid, AuthClass.getUser().getUid())) {
            currentPlayer = player1_char;
        } else
            currentPlayer = player2_char;
    }


    private boolean isGameFinished(String player_char, String[] board) {

        // check if board is full
        boolean isFull = true;
        for (String str : board) {
            if (str == null || str.trim().isEmpty()) {
                isFull = false;
            }
        }

        // check who won
        if (checkWinner().equals(player_char)) {
            return true;
        }
        return Objects.equals(checkWinner(), " ") && isFull;
    }

    private String checkWinner() {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
                {0, 4, 8}, {2, 4, 6} // Diagonals
        };

        for (int[] condition : winConditions) {
            if (
                    boardStatus[condition[0]].equals(boardStatus[condition[1]]) &&
                            boardStatus[condition[1]].equals(boardStatus[condition[2]]) &&
                            !boardStatus[condition[0]].equals(" ")) {
                return boardStatus[condition[0]];
            }
        }
        return " ";
    }

    private String winner(String winner) {
        if (winner.equals(player2_char)) {
            return player2_name;
        } else if (winner.equals(player1_char)) {
            return player1_name;
        } else {
            return "Draw";
        }
    }


}