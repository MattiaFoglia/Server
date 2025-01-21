package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class UserDatabase {
    private ArrayList<String> usernames = new ArrayList<>();

    

    public UserDatabase() {
        this.usernames = new ArrayList();
    }

    synchronized public void addUser(String username){
        usernames.add(username);
    }

    public boolean authenticateUser(String username){
        for(int i = 0; i < usernames.size(); i++){
            if(username.equals(usernames.get(i))){
                return true;
            }
        }
        return false;
    }


    public String getUsernames(){
        String lista = "";
        for(int i = 0; i < usernames.size(); i++){
            lista += i+1 + ") " + usernames.get(i) + " ";
        }
        return lista;
    }

    public int findIndexUser(String username){
        for(int i = 0; i < usernames.size(); i++){
            if(username.equals(usernames.get(i))){
                return i;
            }
        }
        return -1;
    }

    public int findIndexUserAndRemove(String username){
        int index;
        index = findIndexUser(username);
        usernames.remove(index);
        return index;
    }
}