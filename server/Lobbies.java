package server;

import client.subsrs.ClientListener;
import server.game.Game;
import server.subsrc.PlayersAndWinRate;
import server.subsrc.ServerListener;
import supp.InDebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Lobbies{
    private InDebug inDebug = new InDebug();
    private HashMap<String, ServerListener> listeners;
    private PlayersAndWinRate playersAndWinRate;
    private HashMap<String, Game> games;
    private HashMap<String, MyPair> players;

    public Lobbies(){
        playersAndWinRate = new PlayersAndWinRate();
        games = new HashMap<>();
        players = new HashMap<>();
        listeners = new HashMap<>();
        MyPair mP = new MyPair(null);
        mP.setAI(true);
        players.put("RandAI", mP);
    }

    public double getWinRate(String name){
        return playersAndWinRate.getWinRate(name);
    }

    public void exitGame(String name){
        synchronized (games){
            games.remove(name);
        }
    }

    public void playerDisconnect(String name){
        synchronized (players){
            players.remove(name);
        }
        System.out.println("Player " + name + " disconnect");
    }

    private void sendToPlayer(String text, Socket client){
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println(text);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void sendToPlayer(String text, String name){
        if (inDebug.inDebug){
            System.out.println("Server sent to " + name + " " + text);
        }
        try {
            if (players.get(name).isAI()) return;
            PrintWriter out = new PrintWriter(players.get(name).getClient().getOutputStream(), true);
            out.println(text);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void changeWinRate(String name, boolean win){
        playersAndWinRate.changeWinRate(name, win);
    }

    public void addPlayer(String name, Socket client, ServerListener serverListener){
        synchronized (players) {
            if (players.containsKey(name)){
                sendToPlayer("IF", client);//IF - Info False
                if (inDebug.inDebug){
                    System.out.println("Name: " + name + " is busy");
                }
                serverListener.interrupt();
                return;
            }
            players.put(name, new MyPair(client));
            sendToPlayer("IT", name);
        }
        sendLobbiesList(name);
        if (!playersAndWinRate.isRegistered(name)) {
            playersAndWinRate.add(name);
        }
        if (inDebug.inDebug){
            System.out.println("Players and win rate: " + playersAndWinRate);
        }
        try {
            if (inDebug.inDebug){
                System.out.println("New player connected with name: " + name);
            }
            Socket socket = players.get(name).getClient();
            if (!client.equals(socket)){
                synchronized (players) {
                    players.remove(name);
                    players.put(name, new MyPair(client));
                }
                listeners.remove(name);
                listeners.put(name, serverListener);
            }else listeners.put(name, serverListener);
        }
        catch (NullPointerException e){
            synchronized (players) {
                players.put(name, new MyPair(client));
            }
        }
    }

    public void createNewLobby(String name, String playerName, boolean gameWithAI){
        if (!gameWithAI) {
            if (games.containsKey(name)) {
                sendToPlayer("IF", playerName);
                return;
            } else
                sendToPlayer("IT", playerName);
            synchronized (players) {
                players.get(playerName).setInGame(true);
            }
        }
        else {
            sendToPlayer("IT", playerName);
        }
        Game lobby = new Game(name, this);
        if (gameWithAI) {
            if (inDebug.inDebug){
                System.out.println("game started");
            }
            lobby.setAI(true);
            synchronized (players) {
                try {
                    lobby.setSockets(players.get(playerName).getClient(), null);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            lobby.start();
        }
        else {
            synchronized (games) {
                games.put(name, lobby);
            }
            sendLobbiesList();
        }
        lobby.setFirstPlayerName(playerName);
        if (inDebug.inDebug){
            System.out.println("New Lobby started with name: " + name + "by" + playerName);
            System.out.println("Start sending new Lobbies list...");
        }
//        if (!gameWithAI)
//            sendLobbiesList();
        if (inDebug.inDebug){
            System.out.println("End send");
        }
//        if (gameWithAI) {
//            lobby.start();
//        }
        listeners.get(playerName).interrupt();
        listeners.remove(playerName);

    }

    private void sendLobbiesList(String name) {
        String lobbiesList = getActiveLobbies();
        MyPair myPair;
        synchronized (players){
        myPair = players.get(name);
        if (myPair.isAI()) return;
        }
        try {
            if (inDebug.inDebug){
                System.out.println("Sending to " + name);
            }
            if (lobbiesList.isEmpty()) lobbiesList = "empty";
            PrintWriter out = new PrintWriter(myPair.getClient().getOutputStream(), true);
            out.println("LL;" + lobbiesList); //LL - LobbyList
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void sendLobbiesList(){
        String lobbiesList = getActiveLobbies();
        if (inDebug.inDebug) {
            System.out.println("Current Lobby list: |" + lobbiesList + "|");
        }
        synchronized (players) {
            for (String name : players.keySet()) {
                MyPair myPair = players.get(name);
                if (myPair.isAI()) continue;
                if (!myPair.isInGame()) {
                    try {
                        if (inDebug.inDebug) {
                            System.out.println("Sending to " + name);
                        }
                        if (lobbiesList.isEmpty()) {
                            if (inDebug.inDebug) {
                                System.out.println("Cur lobby list is empty");
                            }
                            lobbiesList = "empty";
                        }
                        PrintWriter out = new PrintWriter(myPair.getClient().getOutputStream(), true);
                        out.println("LL;" + lobbiesList); //LL - LobbyList
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    private String getActiveLobbies(){
        StringBuilder list = new StringBuilder();
        synchronized (games) {
            for (String name : games.keySet()) {
                if (!games.get(name).isReady())
                    list.append(name + " ");
            }
        }
        if (!list.isEmpty())
            list.deleteCharAt(list.length() - 1);
        return list.toString();
    }

    public void connectedToLobby(String name, String playerName){
        synchronized (games){
            if (!(games.containsKey(name) && !games.get(name).isStarted())) return;
        }
        players.get(playerName).setInGame(true);
        Game game;
        synchronized (games) {
            game = games.get(name);
        }
        try {
            game.setSockets(players.get(game.getFirstPlayerName()).getClient(), players.get(playerName).getClient());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        game.setSecondPlayerName(playerName);
        game.start();
        //listeners.remove(game.getFirstPlayerName());
        listeners.get(playerName).interrupt();
        listeners.remove(playerName);
        sendLobbiesList();
    }
}

class MyPair{
    private Socket client;
    private boolean inGame = false;
    private boolean AI;

    public MyPair(Socket client) {
        this.client = client;
        AI = false;
    }

    public boolean isAI() {
        return AI;
    }

    public void setAI(boolean AI) {
        this.AI = AI;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }
}