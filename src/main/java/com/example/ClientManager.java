package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientManager {
    HashMap<String, Socket> all;
    String nickname;

    public ClientManager(HashMap<String, Socket> all, String nickname) {
        this.all = all;
        this.nickname = nickname;
    }

    public void toAll(String message) throws IOException {
        for (Socket socket : all.values()) {

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // <username>,message
            out.writeBytes(nickname + "," + message + "\n");

            out.close();
        }
    }

    public void to(String nickname, String message) throws IOException {
        Socket user = all.get(nickname);
        
        DataOutputStream out = new DataOutputStream(user.getOutputStream());
        out.writeBytes(this.nickname + "," + message + "\n");
    }

}
