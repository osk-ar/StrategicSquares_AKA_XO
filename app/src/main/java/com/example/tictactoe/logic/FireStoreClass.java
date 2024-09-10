package com.example.tictactoe.logic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.ui.HomeFragment;
import com.example.tictactoe.ui.OnlineGameFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FireStoreClass {
    // Collections Keys
    public static String USERS_COLLECTION_KEY = "Users";
    public static String ROOMS_COLLECTION_KEY = "Rooms";
    public static String COMP_COLLECTION_KEY = "Comp";
    // User Keys
    public static String USERNAME_KEY = "Username";
    public static String POINTS_KEY = "Points";
    // Rooms Keys
    public static String PLAYER1_KEY = "Player1";
    public static String PLAYER2_KEY = "Player2";
    public static String STATUS_KEY = "Status";
    public static String TURN_KEY = "Turn";
    public static String GAMEMODE_KEY = "GameMode";
    public static String GAMESTATE_KEY = "GameState";
    public static String ROOM_ID_KEY = "RoomId";


    public interface UserDataListener {
        void onSuccess(Map<String, String> userData);

        void onFailure(Exception e);
    }

    public interface OnRoomCreatedListener {
        void onSuccess(String roomCode);

        void onFailure(Exception e);
    }

    public interface OnIdUniqueCheckListener {
        void onCheckComplete(boolean isUnique);

        void onCheckError(Exception e);
    }

    public interface OnStatusChangedListener {
        void onChanged();
    }

    public interface OnStatusChangedCompListener {
        void onChanged(String roomId);
    }

    public interface OnFieldValueListener {
        void onFieldValueReceived(Object fieldValue);
    }


    // Get a Firestore instance
    @SuppressLint("StaticFieldLeak")
    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addUser(MainActivity activity, String name, String userID) {

        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        user.put(USERNAME_KEY, name);
        user.put(POINTS_KEY, 200);

        // Add a new document with a generated ID
        db.collection(USERS_COLLECTION_KEY)
                .document(userID)
                .set(user)
                .addOnSuccessListener(documentReference -> Toast.makeText(activity.getBaseContext(), "User Added Successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity.getBaseContext(), "Error adding User: " + e, Toast.LENGTH_SHORT).show());

    }

    public static void getUserData(String userID, UserDataListener listener) {
        db.collection(USERS_COLLECTION_KEY).document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Map<String, String> userData = new HashMap<>();

                        userData.put(USERNAME_KEY, documentSnapshot.getString(USERNAME_KEY));

                        // Assuming POINTS_KEY is stored as a Long in Firestore
                        Long points = documentSnapshot.getLong(POINTS_KEY);
                        if (points != null) {
                            userData.put(POINTS_KEY, String.valueOf(points));
                        }
                        listener.onSuccess(userData);
                    } else {
                        listener.onFailure(new Exception("No such document"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }


    // create private room
    public static void createPrivateRoom(OnRoomCreatedListener listener) {
        //generate unique id
        String roomId = generateRoomId();
        isIdUnique(roomId, db.collection(ROOMS_COLLECTION_KEY), new OnIdUniqueCheckListener() {
            @Override
            public void onCheckComplete(boolean isUnique) {
                if (isUnique) {
                    // initial gameState
                    Map<String, Object> roomData = new HashMap<>();
                    List<String> gameState = new ArrayList<>(Collections.nCopies(9, " "));

                    // initial data
                    String Uid = AuthClass.getUser().getUid();
                    roomData.put(PLAYER1_KEY, Uid);
                    roomData.put(STATUS_KEY, "waiting");
                    roomData.put(GAMEMODE_KEY, "private");
                    roomData.put(TURN_KEY, Uid);
                    roomData.put(GAMESTATE_KEY, gameState);

                    // add to fireStore
                    db.collection(ROOMS_COLLECTION_KEY).document(roomId).set(roomData)
                            .addOnSuccessListener(aVoid -> {
                                listener.onSuccess(roomId);
                            })
                            .addOnFailureListener(listener::onFailure);
                } else {
                    createPrivateRoom(listener);
                }
            }

            @Override
            public void onCheckError(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public static String generateRoomId() {

        SecureRandom random = new SecureRandom();
        int idLength = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(idLength);
        for (int i = 0; i < idLength; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();

    }

    private static void isIdUnique(final String id, final CollectionReference collection, final OnIdUniqueCheckListener listener) {
        db.runTransaction(transaction -> {
            DocumentSnapshot doc = transaction.get(collection.document(id));
            return !doc.exists(); // Return true if document doesn't exist
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onCheckComplete(task.getResult());
            } else {
                // Handle error
                listener.onCheckError(task.getException());
            }
        });
    }


    // join private room
    public static void joinRoom(String roomId, MainActivity activity, Bundle bundle) {

        FireStoreClass.getFieldValue(activity, FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.STATUS_KEY, fieldValue -> {
            if (fieldValue.toString().equals("waiting")) {
                DocumentReference roomRef = db.collection(ROOMS_COLLECTION_KEY).document(roomId);
                roomRef.update(PLAYER2_KEY, AuthClass.getUser().getUid(),
                                STATUS_KEY, "inProgress")
                        .addOnSuccessListener(aVoid -> {

                            OnlineGameFragment OGF = new OnlineGameFragment();
                            OGF.setArguments(bundle);
                            activity.clearOneNavigateFragment(OGF);
                        })
                        .addOnFailureListener(e -> {
                            MainActivity.showToast(activity, "Error: No such room");
                        });
            } else {
                Toast.makeText(activity, "Room Unavailable :(", Toast.LENGTH_SHORT).show();
            }


        });


    }

    public static void isPlayer2Joined(Context context, String roomId, OnStatusChangedListener listener) {
        DocumentReference roomRef = db.collection(ROOMS_COLLECTION_KEY).document(roomId);
        roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    MainActivity.showToast(context, "Error: " + error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    String status = snapshot.getString(STATUS_KEY);

                    if ("inProgress".equals(status)) {

                        // Remove the listener to avoid repeated triggers
                        ListenerRegistration registration = roomRef.addSnapshotListener(this);
                        registration.remove();

                        // Field has been created
                        listener.onChanged();

                    }
                }
            }
        });
    }


    // get value
    public static void getFieldValue(Context context, String colId, String docId, String fieldName, OnFieldValueListener listener) {
        db.collection(colId).document(docId).
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Object fieldValue = document.get(fieldName);
                            listener.onFieldValueReceived(fieldValue);
                        } else {
                            // Handle case where document doesn't exist
                            listener.onFieldValueReceived(null);
                        }
                    } else {
                        // Handle errors
                        MainActivity.showToast(context, "Error Happened! :(");
                        listener.onFieldValueReceived(null);
                    }
                });
    }

    // set value
    public static void updateFieldValue(Context context, String colId, String docId, String fieldName, Object fieldValue) {
        db.collection(colId).document(docId).update(fieldName, fieldValue)
                .addOnFailureListener(e ->
                        MainActivity.showToast(context, "Error Happened! :("));
    }

    public static void updateFieldValue(Context context, String colId, String docId, String field1Name, Object field1Value, String field2Name, String field2Value) {
        List<String> value = Arrays.asList((String[]) field1Value);
        db.collection(colId).document(docId).update(field1Name, value, field2Name, field2Value)
                .addOnFailureListener(e ->
                        MainActivity.showToast(context, "Error Happened! :("));
    }

    // delete value
    public static void deleteDocument(String colId, String docId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection(colId)
                .document(docId)
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    // check gameEnd
    public static void playerMoved(MainActivity activity, String roomId, OnStatusChangedListener listener) {

        DocumentReference roomRef = db.collection(ROOMS_COLLECTION_KEY).document(roomId);

        roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {

                // handle error
                if (error != null) {
                    MainActivity.showToast(activity, "Error: " + error);
                    return;
                }
                if (snapshot == null || !snapshot.exists()) {
                    return;
                }

                // check if game ended
                String status = snapshot.getString(STATUS_KEY);
                if ("finished".equals(status)) {
                    MainActivity.isGameStarted = false;

                    FireStoreClass.getFieldValue(activity, FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.PLAYER1_KEY, fieldValue -> {
                        if (fieldValue == null) {
                            Toast.makeText(activity, "Error getting p1Uid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String p1Uid = fieldValue.toString();
                        FireStoreClass.getFieldValue(activity, FireStoreClass.USERS_COLLECTION_KEY, p1Uid, FireStoreClass.USERNAME_KEY, innerFieldValue -> {
                            if (innerFieldValue == null) {
                                Toast.makeText(activity, "Error getting p1_name", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String player1_name = innerFieldValue.toString();
                            FireStoreClass.getFieldValue(activity, FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.PLAYER2_KEY, secondFieldValue -> {
                                if (secondFieldValue == null) {
                                    Toast.makeText(activity, "Error getting p2Uid", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String p2Uid = secondFieldValue.toString();
                                FireStoreClass.getFieldValue(activity, FireStoreClass.USERS_COLLECTION_KEY, p2Uid, FireStoreClass.USERNAME_KEY, secondInnerFieldValue -> {
                                    if (secondInnerFieldValue == null) {
                                        Toast.makeText(activity, "Error getting p2_name", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    String player2_name = secondInnerFieldValue.toString();
                                    FireStoreClass.getFieldValue(activity, FireStoreClass.ROOMS_COLLECTION_KEY, roomId, FireStoreClass.GAMESTATE_KEY, thirdFieldValue -> {
                                        ArrayList<String> board = (ArrayList<String>) thirdFieldValue;

                                        String gameMode = snapshot.getString(GAMEMODE_KEY);
                                        String winner = endGameMessage(activity, player1_name, player2_name, board.toArray(new String[0]));
                                        if(Objects.equals(gameMode, "comp")){
                                            if(Objects.equals(winner, player1_name)){
                                                getFieldValue(activity, USERS_COLLECTION_KEY, p1Uid, POINTS_KEY, fieldValue1 -> {
                                                    long points = 10 + (long) fieldValue1;
                                                    updateFieldValue(activity,USERS_COLLECTION_KEY,p1Uid,POINTS_KEY,points);
                                                });
                                            }
                                            if (Objects.equals(winner, player2_name)) {
                                                getFieldValue(activity, USERS_COLLECTION_KEY, p2Uid, POINTS_KEY, fieldValue1 -> {
                                                    long points = 10 + (long) fieldValue1;
                                                    updateFieldValue(activity,USERS_COLLECTION_KEY,p2Uid,POINTS_KEY,points);
                                                });
                                            }
                                        }

                                    });
                                });
                            });
                        });
                    });

                    activity.clearAllNavigateFragment(new HomeFragment());
                    // Remove the listener to avoid repeated triggers
                    ListenerRegistration registration = roomRef.addSnapshotListener(this);
                    registration.remove();
                    return;
                }

                // Check if the GameState field exists and is an array
                if (snapshot.contains(GAMESTATE_KEY) && (snapshot.get(GAMESTATE_KEY) instanceof List)) {
                    listener.onChanged();
                }

            }
        });
    }

    private static String endGameMessage(Context context, String p1_name, String p2_name, String[] board) {

        // check if board is full
        boolean isFull = true;
        for (String str : board) {
            if (str == null || str.trim().isEmpty()) {
                isFull = false;
            }
        }


        if (Objects.equals(checkWinner(board), " ") && isFull) {
            Toast.makeText(context, winner(p1_name, p2_name, " "), Toast.LENGTH_LONG).show();
            return " ";
        }

        // check who won
        String winner_char = checkWinner(board);
        String winner = winner(p1_name, p2_name, winner_char);
        Toast.makeText(context, winner + " wins!", Toast.LENGTH_LONG).show();
        return winner;


    }

    private static String checkWinner(String[] board) {
        int[][] winConditions = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
                {0, 4, 8}, {2, 4, 6} // Diagonals
        };

        for (int[] condition : winConditions) {
            if (
                    board[condition[0]].equals(board[condition[1]]) &&
                            board[condition[1]].equals(board[condition[2]]) &&
                            !board[condition[0]].equals(" ")) {
                return board[condition[0]];
            }
        }
        return " ";
    }

    private static String winner(String p1, String p2, String winner) {
        if (winner.equals("X")) {
            return p2;
        } else if (winner.equals("O")) {
            return p1;
        } else {
            return "Draw";
        }
    }


    // -------------------------------------------------------- COMP FUNCTIONS ------------------------------------------------------------//
    // checks if there's people in the queue
    public static void isCollectionEmpty(OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(COMP_COLLECTION_KEY).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    listener.onComplete(Tasks.forResult(document));
                } else {
                    listener.onComplete(Tasks.forResult(null)); // Or handle empty case differently
                }
            } else {
                Exception e = task.getException();
                listener.onComplete(Tasks.forException(e)); // Pass the exception to the listener
            }
        });
    }
    // create gameRoom like privateRoom then create comp queue document with status and roomId
    public static void createCompRoom(MainActivity activity, OnStatusChangedCompListener listener, OnStatusChangedCompListener listener2) {
        //generate unique id
        String roomId = generateRoomId();
        isIdUnique(roomId, db.collection(ROOMS_COLLECTION_KEY), new OnIdUniqueCheckListener() {
            @Override
            public void onCheckComplete(boolean isUnique) {
                if (isUnique) {
                    // initial gameState
                    Map<String, Object> roomData = new HashMap<>();
                    List<String> gameState = new ArrayList<>(Collections.nCopies(9, " "));
                    // initial data
                    String Uid = AuthClass.getUser().getUid();
                    roomData.put(PLAYER1_KEY, Uid);
                    roomData.put(STATUS_KEY, "waiting");
                    roomData.put(GAMEMODE_KEY, "comp");
                    roomData.put(TURN_KEY, Uid);
                    roomData.put(GAMESTATE_KEY, gameState);
                    // add to fireStore
                    db.collection(ROOMS_COLLECTION_KEY).document(roomId).set(roomData);
                    listener2.onChanged(roomId);

                    Map<String, Object> queueData = new HashMap<>();
                    queueData.put(STATUS_KEY,"waiting");
                    queueData.put(ROOM_ID_KEY,roomId);

                    db.collection(COMP_COLLECTION_KEY).document(Uid).set(queueData).addOnSuccessListener(aVoid -> isGameStarted(activity, Uid, listener, roomId));
                }
                else {
                    createCompRoom(activity, listener, listener2);
                }
            }

            @Override
            public void onCheckError(Exception e) {
                Toast.makeText(activity, "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // wait for other player to join comp
    public static void isGameStarted(Context context, String Uid, OnStatusChangedCompListener listener, String roomId) {
        DocumentReference compRef = db.collection(COMP_COLLECTION_KEY).document(Uid);
        compRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    MainActivity.showToast(context, "Error: " + error);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    String status = snapshot.getString(STATUS_KEY);
                    if ("inProgress".equals(status)) {

                        // Remove the listener to avoid repeated triggers
                        ListenerRegistration registration = compRef.addSnapshotListener(this);
                        registration.remove();

                        // Field has been changed
                        listener.onChanged(roomId);

                    }
                }
            }
        });
    }





















}














