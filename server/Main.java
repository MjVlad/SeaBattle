package server;

import server.subsrc.ServerListener;
import supp.InDebug;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static InDebug inDebug = new InDebug();
    public static void main(String[] args){
        //if (args.length != 1) throw new RuntimeException("Wrong arguments");
        try {
            //ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            ServerSocket serverSocket = new ServerSocket(7777);
            //TODO java server/Main
            //java.net.BindException: Address already in use: bind
            //        at server.Main.main(Main.java:16)
            Lobbies lobbies = new Lobbies();
            if (inDebug.inDebug){
                System.out.println("Server started");
            }
            while(true){
                Socket socket = serverSocket.accept();
                if (inDebug.inDebug){
                    System.out.println("Client was connected");
                }
                ServerListener serverListener = new ServerListener(socket, lobbies);
                serverListener.start();
            }
        }catch (BindException e){
            System.out.println("this port already used");
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
