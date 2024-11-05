package com.example;

import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class Main {

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static UserDatabase userDatabase = new UserDatabase();
    

    public static void main(String[] args) {
        try (
                ServerSocket serverSocket = new ServerSocket(3000)) {
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream()); 
                ClientHandler clientHandler = new ClientHandler(clientSocket,userDatabase,clients,in,out);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}