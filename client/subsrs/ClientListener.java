package client.subsrs;

import client.LobbiesList;
import client.WinInfo;
import client.view.MainFrame;
import supp.InDebug;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientListener extends Thread{
    private InDebug inDebug = new InDebug();
    private Socket server;
    private LobbiesList lobbiesList;
    private MainFrame mainFrame = null;

    public ClientListener(Socket server, LobbiesList lobbiesList){
        this.server = server;
        this.lobbiesList = lobbiesList;
    }

    public void setMainFrame(MainFrame mainFrame){
        this.mainFrame = mainFrame;
    }

    public void run(){
        try {
            if (inDebug.inDebug){
                System.out.println("Lobby listener is started");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                if (inDebug.inDebug){
                    System.out.println("Receive message");
                }
                if (inputLine.equals("IF")){
                    lobbiesList.permissionOnStartNewLobby(false);
                    continue;
                }
                if (inputLine.equals("IT")){
                    lobbiesList.permissionOnStartNewLobby(true);
                    continue;
                }
                if (inputLine.isEmpty()) continue;
//                if (inputLine.equals("LL;empty")) {
//                    inputLine = "";
//                    if (inDebug.inDebug) {
//                        System.out.println("cur lobby list is empty");
//                    }
//                }
                String[] inStrings = inputLine.split(";");
                if (inStrings.length < 2) continue;

                if (inStrings[0].equals("LL")){
                    if (inDebug.inDebug){
                        System.out.println(" it is lobby list");
                    }
                    if (inStrings[1].equals("empty"))
                        inStrings[1] = "";
                    //String[] tmp = inStrings[1].split(" ", 2);
                    ArrayList<String> out = new ArrayList<>(Arrays.asList(inStrings[1].split(" ", 2)));
                    lobbiesList.initLobbyList(out);
                    if (inDebug.inDebug){
                        System.out.println("Ready print in GUI");
                    }
                }
                else if(inStrings[0].equals("FL")) {
                    if (inDebug.inDebug) {
                        System.out.println(" it is game field");
                    }
                    if (mainFrame == null)
                        throw new RuntimeException("Wrong init process in " + this.getClass() + ".run");
                    if (inStrings.length != 4) continue;
                    String[] tmp = inStrings[1].split(" ", 100);
                    byte[] out = new byte[100];
                    for (int i = 0; i < tmp.length; i++) {
                        out[i] = (byte) Integer.parseInt(tmp[i]);
                    }
                    mainFrame.changeTurn(Integer.parseInt(inStrings[3]) == 1);
                    boolean own = Integer.parseInt(inStrings[2]) == 1;
                    mainFrame.setFields(out, own);
                    if (inDebug.inDebug) {
                        System.out.println("Ready print in GUI");
                    }
                }else if (inStrings[0].equals("WI")) {
                        if (inDebug.inDebug) {
                            System.out.println("i'm here " + inStrings.length);
                            System.out.println(inputLine);
                        }
                        WinInfo dialog = new WinInfo();
                        dialog.setInfo(Integer.parseInt(inStrings[1]));
                        dialog.pack();
                        dialog.setVisible(true);
                        System.exit(0);
                    }else if(inStrings[0].equals("WR")){
                    WinInfo dialog = new WinInfo();
                    dialog.setText("Yours win rate = " + inStrings[1]);
                    dialog.pack();
                    dialog.setVisible(true);
                }
            }
        }catch (SocketException e){
            WinInfo dialog = new WinInfo();
            dialog.setText("Server shutdown, please connect later.");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
