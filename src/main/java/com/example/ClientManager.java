package com.example;

import java.util.HashMap;
import java.util.Map;
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
            // itera tutti i client presenti e manda il messaggio 
            for (Map.Entry<String, Socket> entry : all.entrySet()) {
                DataOutputStream out = new DataOutputStream(entry.getValue().getOutputStream());
                // <username>,message
                out.writeBytes(this.nickname + "," + message + "\n");
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
            // se esiste prendere la connessione
            Socket user = all.get(nickname);

            DataOutputStream out = new DataOutputStream(user.getOutputStream());
            // manda il messaggio
            out.writeBytes(this.nickname + "," + message + "\n");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void endConnection() {
        // rimuove la coppia dal hashmap
        all.remove(this.nickname);
    }
}
