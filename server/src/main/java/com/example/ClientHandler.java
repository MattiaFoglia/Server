package com.example;

import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    private Socket socket;
    private UserDatabase userDatabase;
    private ArrayList<ClientHandler> clients;

    BufferedReader in;
    DataOutputStream out;

    public ClientHandler(Socket socket, UserDatabase userDatabase, ArrayList<ClientHandler> clients)
            throws IOException {
        this.socket = socket;
        this.userDatabase = userDatabase;
        this.clients = clients;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String msg) throws IOException{
        out.writeBytes(msg + "\n");
    }

    @Override
    public void run() {

        try {
            signIn();
            handleChat();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void signIn() throws IOException {

        boolean continua = true;
        String username = "";

        while (continua) {
            username = in.readLine();

            if (!userDatabase.authenticateUser(username)) {
                out.writeBytes("siS" + "\n");
                userDatabase.addUser(username);
                int indexUserSender = userDatabase.findIndexUser(username);
                for(int i = 0; i < clients.size(); i++){
                    if (i != indexUserSender) {
                        clients.get(i).sendMessage("JL");
                        clients.get(i).sendMessage(username);
                        clients.get(i).sendMessage(" e' entrato nella chat");
                    }
                }
                continua = false;
            } else {
                out.writeBytes("si!" + "\n");

            }



            
        }

    }

    synchronized private void handleChat() {
        try {
            String userSender = "";
            String privateMessage ="";
            String globalMessage = "";
            String[] parts;
            boolean continua = true;
            while (continua) {
                String message = in.readLine();
                switch (message) {
                    case "UserList":
                        sendUserList();
                        break;

                    case"@":
                        userSender = in.readLine();
                        privateMessage = in.readLine();
                        parts = privateMessage.split("-");
                        String user = parts[0];
                        String text = parts[1];
                        
                        int indexUserReceiver = userDatabase.findIndexUser(user);
                        if (indexUserReceiver == -1) {
                            out.writeBytes("!" + "\n");
                            break;
                        }
                        if(userSender.equals(user)){
                            out.writeBytes("!" + "\n");
                        } else{
                            clients.get(indexUserReceiver).sendMessage("PRIVATE");
                            clients.get(indexUserReceiver).sendMessage(userSender);
                            clients.get(indexUserReceiver).sendMessage(text);
                        }
                        break;

                    case "GLOBAL":
                        userSender = in.readLine();
                        globalMessage = in.readLine();
                        int indexUserSender = userDatabase.findIndexUser(userSender);
                        for(int i = 0; i < clients.size(); i++){
                            if (i != indexUserSender) {
                                clients.get(i).sendMessage("GB");
                                clients.get(i).sendMessage(userSender);
                                clients.get(i).sendMessage(globalMessage);
                            }
                        }
                        break;

                    case "exit":
                        userSender = in.readLine();
                        clients.remove(userDatabase.findIndexUserAndRemove(userSender));
                        for(int i = 0; i < clients.size(); i++){
                                clients.get(i).sendMessage("JL");
                                clients.get(i).sendMessage(userSender);
                                clients.get(i).sendMessage(" ha abbandonato la chat");
                        }
                        continua = false;
                        break;
                
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    synchronized private void sendUserList() {
        try {
            out.writeBytes("UL" + "\n");
            out.writeBytes(userDatabase.getUsernames() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
