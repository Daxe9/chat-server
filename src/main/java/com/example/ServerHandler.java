package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerHandler {

    Socket connection;
    String nickname; 
    BufferedReader in;
    DataOutputStream out;
    ClientManager manager;
    

    public ServerHandler(Socket connection, HashMap<String, Socket> list) throws IOException{
        this.connection = connection;
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new DataOutputStream(connection.getOutputStream());
        // get nickname
        getNickname();        
        list.put(nickname, connection);
        manager = new ClientManager(list, nickname);
    }

    public void run() {

        try {
            while(true) {
                ArrayList<String> cmd = processMessage();
                switch (cmd.get(0)) {
                    case "all":
                        manager.toAll(cmd.get(1));
                        break;
                    case "nickname":
                            
                        break;
                    case "quit":
                        
                        break;
                    default:
                        // control nickname
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void getNickname() throws IOException {
        ArrayList<String> cmd = processMessage();
        if(cmd.get(0).equals("nickname")){
            nickname = cmd.get(1);
        } else {
            out.writeBytes("Invalid Nickname. Try again\n");
            getNickname();
        }
    }

    // format messsage: all/<username>/nickname/quit,messaggio
    private ArrayList<String> processMessage() throws IOException {
        String rawMessage = in.readLine();
        String[] rawList = rawMessage.split(",");
        ArrayList<String> cmd = new ArrayList<>();

        for (int i = 0; i < rawList.length; ++i) {
            String temp = rawList[i].trim();
            cmd.add(temp);
        }

        return cmd;
    }
    



}


