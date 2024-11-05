package com.example;

import java.util.ArrayList;

public class UserDatabase {
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> passwords = new ArrayList<>();

    public boolean userExists(String username){
        return usernames.contains(username);
    }

    public void addUser(String username, String password){
        usernames.add(username);
        passwords.add(password);
    }

    public boolean authenticateUser(String username, String password){
        int index = usernames.indexOf(username);
        return index != -1 && passwords.get(index).equals(password);
    }
}