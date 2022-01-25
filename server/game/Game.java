package server.game;

import server.Lobbies;
import supp.InDebug;
import supp.Point;
import server.subsrc.ServerWriter;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class Game extends Thread{
    private InDebug inDebug = new InDebug();
    private boolean started = false;
    private boolean ready = false;
    private boolean gameWithAI;
    private String lobbyName;
    private ServerWriter serverWriter;
    private String firstPlayerName;
    private String secondPlayerName;
    private Model model;
    private Lobbies lobbies;

    public Game(String lobeName, Lobbies lobbies){
        model = new Model();
        model.randSpawnShips();
        gameWithAI = false;
        this.lobbies = lobbies;
        this.lobbyName = lobeName;
    }

    public void setAI(boolean AI){
        gameWithAI = AI;
        secondPlayerName = "RandAI";
    }

    public void setSockets(Socket first, Socket second) throws IOException {
        serverWriter = new ServerWriter(first, second);
    }

    public void setSecondPlayerName(String secondPlayerName) {
        this.secondPlayerName = secondPlayerName;
        ready = true;
    }

    public void setFirstPlayerName(String firstPlayerName) {
        this.firstPlayerName = firstPlayerName;
        //ready = true;
    }

    public String getFirstPlayerName() {
        return firstPlayerName;
    }

    public void setNames(String firstName, String secondName){
        firstPlayerName = firstName;
        secondPlayerName = secondName;
        ready = true;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void run(){
        started = true;
        try {
            serverWriter.sendToFirstClient(model.getFirstField(false), true, true);
//            if (inDebug.inDebug){
//                System.out.println("Field is sent " + model.getFirstField(false));
//            }
            if (!gameWithAI)
                serverWriter.sendToSecondClient(model.getSecondField(false), true, false);
            while (model.gameState() == 0){
                Point turnData;
                if (model.whoTurn()) {
                    turnData = serverWriter.receiveFromFirstClient();
                    model.turn(turnData.getX(), turnData.getY());
                    if (!gameWithAI)
                        serverWriter.sendToSecondClient(model.getSecondField(false), true, !model.whoTurn());
                    serverWriter.sendToFirstClient(model.getSecondField(true), false, model.whoTurn());
                }else {
                    if (!gameWithAI)
                        turnData = serverWriter.receiveFromSecondClient();
                    else
                        turnData = getTurnDataToAI();
                    model.turn(turnData.getX(), turnData.getY());
                    serverWriter.sendToFirstClient(model.getFirstField(false), true, model.whoTurn());
                    if (!gameWithAI)
                        serverWriter.sendToSecondClient(model.getFirstField(true), false, !model.whoTurn());
                }
            }
            if(model.gameState() == 1){
                lobbies.changeWinRate(firstPlayerName, true);
                lobbies.changeWinRate(secondPlayerName, false);
            }
            else {
                lobbies.changeWinRate(firstPlayerName, false);
                lobbies.changeWinRate(secondPlayerName, true);
            }
            serverWriter.sendWinInfo(model.gameState());
            if (inDebug.inDebug){
                System.out.println("Win Player " + model.gameState());
            }
        }catch (SocketException e){
            if (inDebug.inDebug){
                System.out.println("Win Player " + !model.whoTurn());
            }
            lobbies.changeWinRate(firstPlayerName, !model.whoTurn());
            lobbies.changeWinRate(secondPlayerName, model.whoTurn());
            serverWriter.sendWinInfo(!model.whoTurn());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        finally {
            lobbies.playerDisconnect(firstPlayerName);
            if (!gameWithAI)
                lobbies.playerDisconnect(secondPlayerName);
            lobbies.exitGame(lobbyName);
        }
    }

    public Point getTurnDataToAI(){
        Random random = new Random();
        Point res = new Point(random.nextInt(10), random.nextInt(10));
        return res;
    }
}
