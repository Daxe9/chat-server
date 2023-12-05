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
            System.out.println("Server listening at 42069...");
            // chiave nickname valore connessione
            HashMap<String, Socket> list = new HashMap<>();
            do{
                Socket s = server.accept(); 
                System.out.println("New connection happened");
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
