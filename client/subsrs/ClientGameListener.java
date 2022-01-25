package client.subsrs;

import client.view.MainFrame;
import supp.InDebug;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientGameListener extends Thread {
    private InDebug inDebug = new InDebug();
    private MainFrame mainFrame;
    private Socket server;

    public ClientGameListener(MainFrame mainFrame, Socket server){
        this.server = server;
        this.mainFrame = mainFrame;
    }

    public void run(){
        try {//поле не приходит //если приходит, то не на своё место
            DataInputStream in = new DataInputStream(server.getInputStream());
            byte[] out = new byte[102];
            in.available();
            while (true) {
                if (in.readInt() == 102){
                    if (inDebug.inDebug){
                        System.out.println("Field ready to receive " + in.available() + " bytes");
                    }
                    in.readFully(out, 0, 102);
                } else continue;
                //in.read(out);
                if (inDebug.inDebug){
                    System.out.println("Field is receive");
                }
                //if (out[out.length - 1] == (byte)1) mainFrame.changeTurn();
                boolean flag = out[out.length - 2] == (byte) 1;
                mainFrame.setFields(out, flag);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
