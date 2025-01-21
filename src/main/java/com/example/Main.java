package com.example;

import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class Main {

    

    public static void main(String[] args) {
        ArrayList<ClientHandler> clients = new ArrayList();
        UserDatabase userDatabase = new UserDatabase();
        
        try {
            ServerSocket serverSocket = new ServerSocket(3000);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, userDatabase, clients);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}