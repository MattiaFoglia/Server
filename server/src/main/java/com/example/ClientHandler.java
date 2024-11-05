package com.example;

import java.util.ArrayList;
import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    
    private Socket socket;
    private String username;
    private UserDatabase userDatabase;
    private ArrayList<ClientHandler> clients;

    BufferedReader in;
    DataOutputStream out; 


    public ClientHandler(Socket socket, UserDatabase userDatabase, ArrayList<ClientHandler> clients, BufferedReader in,  DataOutputStream out)throws IOException {
        this.socket = socket;
        this.userDatabase = userDatabase;
        this.clients = clients;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new DataOutputStream(socket.getOutputStream()); 
    }

    @Override
    public void run(){

        try{
            String signal = in.readLine();
            if("s".equals(signal)){
                out.writeBytes("l?" +"\n");
                String loginChoice = in.readLine();

                if("si?".equals(loginChoice)){
                    signUp();
                }else if("si?".equals(loginChoice)){
                    signIn();
                }
                handleChat();
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        if(username != null){
            clients.remove(this);
        }
        try{
            socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }        
        


}


private void signUp()throws IOException {

    out.writeBytes("suC?"+"\n");
    String nickname = in.readLine();
    String password = in.readLine();

    synchronized(userDatabase){
    
        if(userDatabase.authenticateUser(nickname,password)){
            out.writeBytes("su!"+"\n");
            this.username = nickname;
        
        }else{
            out.writeBytes("suS"+"\n");
        }
    }
}

private void signIn()throws IOException {

    out.writeBytes("siC?"+"\n");
    String nickname = in.readLine();
    String password = in.readLine();

    synchronized(userDatabase){
    
        if(userDatabase.authenticateUser(nickname,password)){
            out.writeBytes("siS"+"\n");
            this.username = nickname;
        
        }else{
            out.writeBytes("si!"+"\n");
        }
    }
}

private void handleChat() throws IOException{
    while(true){
        String message = in.readLine();

        if(message.equalsIgnoreCase("UserList")){
            sendUserList();
        }else if(message.startsWith("@")){
            handlePrivateMessage(message);            
          }else if(message.startsWith("--GLOBAL")){
            handleGlobalMessage(message);            
        }
            
    }
}

private void sendUserList() throws IOException{
    out.writeBytes("UL"+"\n");
    for(ClientHandler client : clients){
        out.writeBytes(client.username);
    }
}

private void handlePrivateMessage(String message) throws IOException{
    String[] parts = message.split("", 2);
    String targetUser= parts[0].substring(1);
    String privateMessage = parts[1];

    ClientHandler targetClient = null;
    for(ClientHandler client : clients){
        if(client.username.equals(targetUser)){
            targetClient = client;
            break;
        }
    }

    if(targetClient != null){
        targetClient.out.writeBytes("@" + username + ": " + privateMessage+"\n");

    }else{
        out.writeBytes("pc!"+"\n");
    }
}


private void handleGlobalMessage(String message)throws IOException{
String globalMessage = message.substring(9);
for(ClientHandler client : clients){
        if(client!= this){
            client.out.writeBytes("GLobal -" + username +": " + globalMessage+"\n");
        }
    }
}     
        

}

