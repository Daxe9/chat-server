package com.example;

import java.util.HashMap;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientManager {
    HashMap<String, Socket> all;
    String nickname;

    public ClientManager(HashMap<String, Socket> all, String nickname) {
        this.all = all;
        this.nickname = nickname;
    }

    public boolean toAll(String message) {
        try {
            for (Socket socket : all.values()) {

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                // <username>,message
                out.writeBytes(nickname + "," + message + "\n");

                out.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean to(String nickname, String message) {
        try {
            // controlla se il nickname esiste
            if (!all.containsKey(nickname)) {
                return false;
            }
            Socket user = all.get(nickname);

            DataOutputStream out = new DataOutputStream(user.getOutputStream());
            out.writeBytes(this.nickname + "," + message + "\n");

            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void endConnection() {
        all.remove(this.nickname);
    }
}
