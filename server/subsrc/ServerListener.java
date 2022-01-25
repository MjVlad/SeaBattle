package server.subsrc;

import server.Lobbies;
import supp.InDebug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ServerListener extends Thread{
    private InDebug inDebug = new InDebug();
    private Lobbies lobbies;
    private String name;
    private Socket client;

    public ServerListener(Socket client, Lobbies lobbies){
        this.client = client;
        this.lobbies = lobbies;
    }

    public void run(){
        if (inDebug.inDebug){
            System.out.println("New player listener is ALIVE");
        }
        String data;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            if (inDebug.inDebug){
                System.out.println("Player listener ready to receive new message");
            }
            while((data = in.readLine()) != null){
                if (inDebug.inDebug){
                    System.out.println("Player listener receive new message");
                }
                String[] tmp = data.split(" ", 2);
                switch (tmp[0]){
                    case "CPN" -> { //connected player name
                        if (inDebug.inDebug){
                            System.out.println("New player will connect with name: " + tmp[1]);
                        }
                        lobbies.addPlayer(tmp[1], client, this);
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            if (inDebug.inDebug){
                                System.out.println("While is dead");
                            }
                           return;
                        }
                        name = tmp[1];
                    }
                    case "NLN" -> { //new Lobby name
                        String[] temp = tmp[1].split(" ", 2);
                        boolean gameWithAI = temp[0].equals("RAI");
                        if (inDebug.inDebug){
                            System.out.println("New lobby will starting with name: " + tmp[1] + " by " + name);
                            System.out.println("this lobby with AI" + gameWithAI);
                        }
                        lobbies.createNewLobby(tmp[1], name, gameWithAI); //TODO приём от пользователя игры с ИИ или нет
                        return;
                    }
                    case "ALN" -> { //available lobby name
                        if (inDebug.inDebug){
                            System.out.println("player " + name + " will connected to lobby with name: " + tmp[1]);
                        }
                        lobbies.connectedToLobby(tmp[1], name);
                        return;
                    }
                    case "WR" -> {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        out.println("WR;" + lobbies.getWinRate(name));
                    }
                }
            }
            if (inDebug.inDebug){
                System.out.println("While is dead");
            }
        }catch (SocketException exception){
            lobbies.playerDisconnect(name);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
