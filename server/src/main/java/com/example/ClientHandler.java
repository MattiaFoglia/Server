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
        /*
         * if(username != null){
         * clients.remove(this);
         * }
         */
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

            boolean continua = true;
            out.writeBytes("siC?" + "\n");

            while (continua) {
                String username = in.readLine();
                String password = in.readLine();

                if (userDatabase.authenticateUser(username, password)) {
                    out.writeBytes("siS" + "\n");
                    continua = false;
                } else {
                    out.writeBytes("si!" + "\n");

                }

            }
        }

    }

    //

    private void directSignIn() throws IOException {

        boolean continua = true;
        out.writeBytes("siC?" + "\n");

        while (continua) {
            String username = in.readLine();
            String password = in.readLine();

            if (userDatabase.authenticateUser(username, password)) {
                out.writeBytes("siS" + "\n");

                continua = false;
            } else {
                out.writeBytes("si!" + "\n");

            }

        }

    }

    synchronized private void handleChat() {
        try {
            boolean continua = true;
            while (continua) {
                String message = in.readLine();
                switch (message) {
                    case "UserList":
                        sendUserList();
                        break;

                    case"@":
                        String privateMessage = in.readLine();
                        String[] parts = privateMessage.split(" ");
                        String user = parts[0];
                        String text = parts[1];
                        clients.get(userDatabase.findIndexUser(user)).sendMessage("PRIV");
                        clients.get(userDatabase.findIndexUser(user)).sendMessage(text);



                        break;

                    case "exit":
                        String username = in.readLine();
                        clients.remove(userDatabase.findIndexUserAndRemove(username));
                        continua = false;
                        break;
                
                    default:
                        break;
                }
                /*
                if (message.equalsIgnoreCase("UserList")) {
                    sendUserList();
                } else if (message.equals("@")) {
                    // handlePrivateMessage(message);
                } else if (message.startsWith("GLOBAL")) {
                    handleGlobalMessage(message);
                } else if ()
                */
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

    /*
     * private void handlePrivateMessage(String message) throws IOException {
     * String[] parts = message.split("", 2);
     * String targetUser = parts[0].substring(1);
     * String privateMessage = parts[1];
     * 
     * ClientHandler targetClient = null;
     * for (ClientHandler client : clients) {
     * if (client.username.equals(targetUser)) {
     * targetClient = client;
     * break;
     * }
     * }
     * 
     * if (targetClient != null) {
     * targetClient.out.writeBytes("@" + username + ": " + privateMessage + "\n");
     * 
     * } else {
     * out.writeBytes("pc!" + "\n");
     * }
     * }
     */
    private void handleGlobalMessage(String message) throws IOException {
        String globalMessage = message.substring(9);
        for (ClientHandler client : clients) {
            if (client != this) {
                client.out.writeBytes("--GLOBAL" + ": " + globalMessage + "\n");
            }
        }
    }

}
