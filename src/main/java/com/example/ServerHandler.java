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
            while (true) {
                ArrayList<String> cmd = processMessage();
                if (cmd.size() == 0) {
                    // il client ha chiuso la connessione
                    return;
                }

                String originalMessage = cmd.get(1);

                switch (cmd.get(0)) {
                    case "all":
                        if (!manager.toAll(originalMessage)) {
                            out.writeBytes("n\n");
                        } else {
                            out.writeBytes("y\n");
                        }
                        break;
                    case "quit":
                        manager.endConnection();
                        break;
                    default:
                        // controllo del username
                        String nickname = cmd.get(0);
                        String message = originalMessage;

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
        out.writeBytes("name\n");
        ArrayList<String> cmd = processMessage();
        if (cmd.size() == 0) {
            return;
        }

        // controllo se il nickname e' uguale a 'all'
        if (cmd.get(1).equals("all")) {
            out.writeBytes("n\n");
            getNickname(list);
            return;
        }

        // controllo se esiste di gia' il nickname
        if (list.size() > 0) {

            for (String nn : list.keySet()) {
                if (nn.equals(cmd.get(1))) {
                    out.writeBytes("n\n");
                    getNickname(list);
                    return;
                }
            }
        }

        this.nickname = cmd.get(1);
        out.writeBytes("y\n");

    }

    // format messsage: all/<username>/nickname/quit,messaggio
    private ArrayList<String> processMessage() throws IOException {
        String rawMessage = in.readLine();

        if (rawMessage == null) {
            return new ArrayList<>();
        }

        String[] rawList = rawMessage.split(",");
        ArrayList<String> cmd = new ArrayList<>();

        int firstCommaIndex = rawMessage.indexOf(",");
        cmd.add(rawMessage.substring(0, firstCommaIndex));
        cmd.add(rawMessage.substring(firstCommaIndex + 1));
        System.out.println(cmd);

        return cmd;
    }
}
