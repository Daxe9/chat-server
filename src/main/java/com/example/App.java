package com.example;

import java.net.Socket;
import java.util.HashMap;
import java.net.ServerSocket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        try {
            ServerSocket server = new ServerSocket(42069);
            HashMap<String, Socket> list = new HashMap<>();
            do{
                Socket s = server.accept(); 
                ServerHandler sh = new ServerHandler(s, list);
                sh.start();
            }
            while(true);
   

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("errore durante l'istanza del server");
            System.exit(1);
        }
    }
}
