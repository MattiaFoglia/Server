package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class UserDatabase {
    private ArrayList<String> usernames = new ArrayList<>();
    private ArrayList<String> passwords = new ArrayList<>();

    

    public UserDatabase() {
        this.usernames = new ArrayList();
        this.passwords = new ArrayList();
    }

    public boolean userExists(String username){
        return usernames.contains(username);
    }

    synchronized public void addUser(String username, String password){
        usernames.add(username);
        passwords.add(password);
    }

    public boolean authenticateUser(String username){
        for(int i = 0; i < usernames.size(); i++){
            if(username.equals(usernames.get(i))){
                return true;
            }
        }
        return false;
    }

    public boolean authenticateUser(String username, String password){
        for(int i = 0; i < usernames.size(); i++){
            if(username.equals(usernames.get(i)) && password.equals(passwords.get(i))){
                return true;
            }
        }
        return false;
    }

    public String getUsernames(){
        String lista = "";
        for(int i = 0; i < usernames.size(); i++){
            lista += i+1 + ") " + usernames.get(i) + "\n";
        }
        return lista;
    }
}