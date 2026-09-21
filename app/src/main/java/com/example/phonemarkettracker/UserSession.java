package com.example.phonemarkettracker;

//current user for this app process.
public class UserSession {
    private static int userId = -1;

    private UserSession() { }

    public static void signIn(int id) { userId = id; }

    public static int getUserId() { return userId; }

    public static void signOut() { userId = -1; }
}
