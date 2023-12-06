package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerHandler extends Thread {

    Socket connection;
    String nickname;
    BufferedReader in;
    DataOutputStream out;
    ClientManager manager;
    HashMap<String, Socket> list;

    public ServerHandler(Socket connection, HashMap<String, Socket> list) throws IOException {
        this.connection = connection;
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new DataOutputStream(connection.getOutputStream());
        this.list = list;
    }

    public void run() {
        try {
            // get nickname
            getNickname(list);
            list.put(nickname, connection);
            manager = new ClientManager(list, nickname);
            manager.notifyAllClients();            

            while (true) {
                // posizione 0 commando, posizione 1 messaggio 
                ArrayList<String> cmd = processMessage();
                if (cmd.size() == 0) {
                    // il client ha chiuso la connessione
                    return;
                }

                // prender il messaggio effettivo
                String originalMessage = cmd.get(1);

                switch (cmd.get(0)) {
                        // inoltre il messaggio a tutti
                    case "all":
                        if (!manager.toAll(originalMessage)) {
                            out.writeBytes("n\n");
                        } else {
                            out.writeBytes("y\n");
                        }
                        break;
                    case "quit":
                        // chiude la connessione
                        manager.endConnection();
                        break;
                    default:
                        String nickname = cmd.get(0);
                        String message = originalMessage;

                        // manda il messaggio al destinatario specificato con il messaggio
                        if (!manager.to(nickname, message)) {
                            out.writeBytes("n\n");
                        } else {
                            out.writeBytes("y\n");
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void getNickname(HashMap<String, Socket> list) throws IOException {
        // chiede l'inserimento del nickname
        out.writeBytes("name\n");

        ArrayList<String> cmd = processMessage();
        
        // se la lista e' vuota, l'utente ha chiuso la connessione
        if (cmd.size() == 0) {
            return;
        }

        // controllo se il nickname e' uguale a 'all'
        if (cmd.get(1).equals("all")) {
            out.writeBytes("n\n");
            out.writeBytes("name\n");
            getNickname(list);
            return;
        }

        // controllo se esiste di gia' il nickname
        if (list.size() > 0) {
            for (String nn : list.keySet()) {
                if (nn.equals(cmd.get(1))) {
                    out.writeBytes("n\n");
                    out.writeBytes("name\n");
                    getNickname(list);
                    return;
                }
            }
        }

        // setta il nickname
        this.nickname = cmd.get(1);
        out.writeBytes("y\n");

    }

    // format messsage: all/<username>/nickname/quit,messaggio
    private ArrayList<String> processMessage() throws IOException {
        String rawMessage = in.readLine();

        // se e' null l'utente ha chiuso la connessione
        if (rawMessage == null) {
            return new ArrayList<>();
        }

        ArrayList<String> cmd = new ArrayList<>();

        // trova la prima virgola per separare la parte dei commandi e la parte del messaggio
        int firstCommaIndex = rawMessage.indexOf(",");
        // l'utente ha mandato quit
        if (firstCommaIndex == -1) {
            cmd.add("quit");
            // filler...
            cmd.add("filler");
            return cmd;
        }
        
        // aggiunge alla lista il commando
        cmd.add(rawMessage.substring(0, firstCommaIndex));
        // aggiunge alla lista il messaggio
        cmd.add(rawMessage.substring(firstCommaIndex + 1));
        System.out.println(cmd);

        return cmd;
    }
}
