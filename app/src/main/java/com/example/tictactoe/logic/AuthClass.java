package com.example.tictactoe.logic;

import android.widget.Toast;

import com.example.tictactoe.MainActivity;
import com.example.tictactoe.ui.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthClass {

    public static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public static boolean isSignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    public static void logOut() {
        mAuth.signOut();
    }

    public static void signUp(MainActivity activity, String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                System.out.println("createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                activity.clearAllNavigateFragment(new HomeFragment());
                FireStoreClass.addUser(activity, name, AuthClass.getUser().getUid());
            } else {
                System.out.println("createUserWithEmail:failure");
                Toast.makeText(activity.getBaseContext(), "Failed to create account.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void logIn(MainActivity activity, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {

            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                System.out.println("createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                activity.clearAllNavigateFragment(new HomeFragment());
            } else {
                // If sign in fails, display a message to the user.
                System.out.println("createUserWithEmail:failure");
                Toast.makeText(activity.getBaseContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
