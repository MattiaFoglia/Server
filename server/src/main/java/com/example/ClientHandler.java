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

            String loginChoice = in.readLine();

            if ("su?".equals(loginChoice)) {
                signUp();
                signIn();
            } else if ("si?".equals(loginChoice)) {
                directSignIn();
            }
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

    private void signUp() throws IOException {
        boolean continua = true;
        out.writeBytes("suC?" + "\n");
        while (continua) {
            String username = in.readLine();
            String password = in.readLine();
            if (userDatabase.authenticateUser(username)) {
                out.writeBytes("su!" + "\n");
            } else {
                userDatabase.addUser(username, password);
                out.writeBytes("suS" + "\n");
                continua = false;
            }
        }


    }

    private void signIn() throws IOException {
        String richiesta = in.readLine();
        if (richiesta.equals("si?")) {
            String username = "";
            String password = "";
            boolean continua = true;
            out.writeBytes("siC?" + "\n");

            while (continua) {
                username = in.readLine();
                password = in.readLine();

                if (userDatabase.authenticateUser(username, password)) {
                    out.writeBytes("siS" + "\n");
                    continua = false;
                } else {
                    out.writeBytes("si!" + "\n");

                }

            }
            int indexUserSender = userDatabase.findIndexUser(username);
            for(int i = 0; i < clients.size(); i++){
                if (i != indexUserSender) {
                    clients.get(i).sendMessage("GB");
                    clients.get(i).sendMessage(username + " e' entrato nella chat");
                }
            }
        }

    }

    //

    private void directSignIn() throws IOException {

        boolean continua = true;
        String username = "";
        String password = "";
        out.writeBytes("siC?" + "\n");

        while (continua) {
            username = in.readLine();
            password = in.readLine();

            if (userDatabase.authenticateUser(username, password)) {
                out.writeBytes("siS" + "\n");

                continua = false;
            } else {
                out.writeBytes("si!" + "\n");

            }

            int indexUserSender = userDatabase.findIndexUser(username);
            for(int i = 0; i < clients.size(); i++){
                if (i != indexUserSender) {
                    clients.get(i).sendMessage("GB");
                    clients.get(i).sendMessage(username + " e' entrato nella chat");
                }
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
                        if(userSender.equals(user)){
                            out.writeBytes("!" + "\n");
                        } else{
                            clients.get(indexUserReceiver).sendMessage("PRIV");
                            clients.get(indexUserReceiver).sendMessage("(Privato)"+ userSender + ": " + text);
                        }
                        break;

                    case "GLOBAL":
                        userSender = in.readLine();
                        globalMessage = in.readLine();
                        int indexUserSender = userDatabase.findIndexUser(userSender);
                        for(int i = 0; i < clients.size(); i++){
                            if (i != indexUserSender) {
                                clients.get(i).sendMessage("GB");
                                clients.get(i).sendMessage("(Globale)"+ userSender + ": " + globalMessage);
                            }
                        }
                        break;

                    case "exit":
                        userSender = in.readLine();
                        clients.remove(userDatabase.findIndexUserAndRemove(userSender));
                        for(int i = 0; i < clients.size(); i++){
                                clients.get(i).sendMessage("GB");
                                clients.get(i).sendMessage(userSender + " ha abbandonato la chat");
                            
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
