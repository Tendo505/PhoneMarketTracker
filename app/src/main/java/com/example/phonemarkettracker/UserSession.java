package com.example.phonemarkettracker;

/** Keeps the signed-in user's ID for sales created during this app session. */
public class UserSession {

    private static int userId = -1;

    private UserSession() {
    }

    // create
    public static void signIn(int signedInUserId) {
        userId = signedInUserId;
    }

    // read
    public static int getUserId() {
        return userId;
    }

    // delete
    public static void signOut() {
        userId = -1;
    }
}
