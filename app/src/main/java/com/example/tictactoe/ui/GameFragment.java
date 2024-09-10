package com.example.tictactoe.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.logic.AiClass;

import java.util.Arrays;

public class GameFragment extends Fragment {
    android.widget.ProgressBar timerProgressBar;
    TextView player1, player2, turn_tv;
    String player1_name, player2_name;
    ImageView enemy;

    TextView spot11, spot12, spot13, spot21, spot22, spot23, spot31, spot32, spot33;
    String[] boardStatus;
    String player1_char = "O";
    String player2_char = "X";
    String currentPlayer = player1_char;
    CountDownTimer timer;
    int ai;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            player1_name = getArguments().getString("player1");
            player2_name = getArguments().getString("player2");
            ai = getArguments().getInt("aiLevel");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

        enemy = rootView.findViewById(R.id.enemy_img_game);
        player1 = rootView.findViewById(R.id.game_player_name);
        player2 = rootView.findViewById(R.id.game_enemy_name);
        turn_tv = rootView.findViewById(R.id.game_player_turn);
        timerProgressBar = rootView.findViewById(R.id.game_timer_progressbar);

        spot11 = rootView.findViewById(R.id.textView11);
        spot12 = rootView.findViewById(R.id.textView12);
        spot13 = rootView.findViewById(R.id.textView13);
        spot21 = rootView.findViewById(R.id.textView21);
        spot22 = rootView.findViewById(R.id.textView22);
        spot23 = rootView.findViewById(R.id.textView23);
        spot31 = rootView.findViewById(R.id.textView31);
        spot32 = rootView.findViewById(R.id.textView32);
        spot33 = rootView.findViewById(R.id.textView33);

        spot11.setOnClickListener(v -> handlePlayerMove(spot11, (short) 0));
        spot12.setOnClickListener(v -> handlePlayerMove(spot12, (short) 1));
        spot13.setOnClickListener(v -> handlePlayerMove(spot13, (short) 2));
        spot21.setOnClickListener(v -> handlePlayerMove(spot21, (short) 3));
        spot22.setOnClickListener(v -> handlePlayerMove(spot22, (short) 4));
        spot23.setOnClickListener(v -> handlePlayerMove(spot23, (short) 5));
        spot31.setOnClickListener(v -> handlePlayerMove(spot31, (short) 6));
        spot32.setOnClickListener(v -> handlePlayerMove(spot32, (short) 7));
        spot33.setOnClickListener(v -> handlePlayerMove(spot33, (short) 8));

        // Initial State
        if (ai > 0) {
            enemy.setImageResource(R.drawable.robot);
        }
        player1.setText(player1_name);
        player2.setText(player2_name);
        turn_tv.setText(player1.getText().toString() + "\'s Turn");
        boardStatus = new String[9];
        Arrays.fill(boardStatus, " ");
        startTimer(timerProgressBar);

        return rootView;
    }


    private void handlePlayerMove(TextView textView, short index) {
        if (boardStatus[index].equals(" ")) {
            game(textView, index);
            if (ai > 0 && !isGameFinished(player1_char)) {
                int aiMoveIndex = aiLevel(boardStatus, ai);
                boardStatus[aiMoveIndex] = currentPlayer;
                aiGame(aiMove(aiMoveIndex));
                isGameFinished(player2_char);
            }
            if (ai == 0 && !isGameFinished(player1_char)) {
                isGameFinished(player2_char);
            }
        }
    }

    private void game(TextView textView, short index) {
        MainActivity.playClickSound(getContext());
        textView.setText(currentPlayer);
        boardStatus[index] = currentPlayer;
        switchPlayer();
        startTimer(timerProgressBar);
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(player1_char) ? player2_char : player1_char;
        turn_tv.setText(winner(currentPlayer) + "\'s Turn");
    }

    private void startTimer(ProgressBar timerProgressBar) {
        // Animation Part
        int timerDelay = 5000; // 15 sec
        Interpolator timerInterpolator = new LinearInterpolator();
        ObjectAnimator timerAnimator = ObjectAnimator.ofInt(timerProgressBar, "Progress", 0, 20000);
        timerAnimator.setDuration(timerDelay);
        timerAnimator.setInterpolator(timerInterpolator);
        timerAnimator.start();

        // Code Part
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(timerDelay, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Toast.makeText(getContext(), winner(currentPlayer) + " ran out of time!", Toast.LENGTH_LONG).show();
                switchPlayer();
                if (ai > 0 && !isGameFinished(player1_char)) {
                    int aiMoveIndex = aiLevel(boardStatus, ai);
                    boardStatus[aiMoveIndex] = currentPlayer;
                    aiGame(aiMove(aiMoveIndex));
                    isGameFinished(player2_char);
                }

                startTimer(timerProgressBar);
            }
        }.start();
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

    private boolean isDraw() {
        for (String status : boardStatus) {
            if (status.equals(" ")) {
                return false;
            }
        }
        return true;
    }


    private void aiGame(TextView textView) {
        textView.setText(currentPlayer);
        switchPlayer();
        startTimer(timerProgressBar);
    }

    private TextView aiMove(int move) {
        switch (move) {
            case 0:
                return spot11;
            case 1:
                return spot12;
            case 2:
                return spot13;
            case 3:
                return spot21;
            case 4:
                return spot22;
            case 5:
                return spot23;
            case 6:
                return spot31;
            case 7:
                return spot32;
            default:
                return spot33;
        }
    }

    private int aiLevel(String[] board, int level) {
        switch (level) {
            case 1:
                return AiClass.getEasyMove(board);
            case 2:
                return AiClass.getMediumMove(board, player2_char, player1_char);
            case 3:
                return AiClass.getHardMove(board, player2_char, player1_char);
            default:
                return 0;
        }
    }

    private boolean isGameFinished(String player) {
        if (checkWinner().equals(player)) {
            String winner = winner(player);
            Toast.makeText(requireContext(), winner + " wins!", Toast.LENGTH_LONG).show();
            if (timer != null) {
                timer.cancel();
            }
            ((MainActivity) requireActivity()).onBackPressed();
            return true;
        }
        if (isDraw()) {
            Toast.makeText(requireContext(), "Draw!", Toast.LENGTH_LONG).show();
            if (timer != null) {
                timer.cancel();
            }
            ((MainActivity) requireActivity()).onBackPressed();
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
