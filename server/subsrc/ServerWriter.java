package server.subsrc;

import supp.InDebug;
import supp.Point;

import java.io.*;
import java.net.Socket;

public class ServerWriter {
    private InDebug inDebug = new InDebug();
    private Socket firstClient;
    private Socket secondClient;

    private PrintWriter firstClientWriter;
    private PrintWriter secondClientWriter;

    private BufferedReader firstClientRec;
    private BufferedReader secondClientRec;

    public ServerWriter(Socket client1, Socket client2) throws IOException {
        firstClient = client1;
        secondClient = client2;

        firstClientRec = new BufferedReader(new InputStreamReader(firstClient.getInputStream()));
        if (client2 != null) {
            secondClientRec = new BufferedReader(new InputStreamReader(secondClient.getInputStream()));
            secondClientWriter = new PrintWriter(client2.getOutputStream(), true);
        }

        firstClientWriter = new PrintWriter(client1.getOutputStream(), true);
    }

    public Point receiveFromFirstClient() throws IOException {
        if (inDebug.inDebug){
            System.out.println("1 Turn ready to rec");
        }
        String[] st = firstClientRec.readLine().split(" ", 2);
        Point p = null;
        //if (st.length != 2) //TODO проброс исключения
        if (st.length == 2 && st[0].equals("GT")){
            String[] tmp = st[1].split(" ", 2);
            p = new Point(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
        }
        return p;
    }

    public Point receiveFromSecondClient() throws IOException {
        if (inDebug.inDebug){
            System.out.println("1 Turn ready to rec");
        }
        String[] st = secondClientRec.readLine().split(" ", 2);
        Point p = null;
        //if (st.length != 2) //TODO проброс исключения
        if (st.length == 2 && st[0].equals("GT")){
            String[] tmp = st[1].split(" ", 2);
            p = new Point(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
        }
        return p;
    }

    public void sendToFirstClient(String fl, boolean own, boolean turn) throws IOException {
//        if (inDebug.inDebug){
//            System.out.println("1 Field ready to sent");
//        }
        StringBuilder tmp = new StringBuilder();
        tmp.append(";");
        tmp.append(own? 1 : 2);
        tmp.append(";");
        tmp.append(turn? 1 : 2);
        String out = "FL;" + fl + tmp.toString();
        firstClientWriter.println(out);
//        if (inDebug.inDebug){
//            //System.out.println("1 Field is:\n" + out);
//            System.out.println("1 Field send");
//        }
    }

    public void sendToSecondClient(String fl, boolean own, boolean turn) throws IOException {
//        if (inDebug.inDebug){
//            System.out.println("2 Field ready to sent");
//        }
        StringBuilder tmp = new StringBuilder();
        tmp.append(";");
        tmp.append(own? 1 : 2);
        tmp.append(";");
        tmp.append(turn? 1 : 2);
        String out = "FL;" + fl + tmp.toString();
        secondClientWriter.println(out);
//        if (inDebug.inDebug){
//            System.out.println("2 Field send");
//        }
    }

    public void sendWinInfo(boolean whoWin){
        if (whoWin) {
            firstClientWriter.println("WI;1");
        }
        else {
            if (secondClient != null)
                secondClientWriter.println("WI;1");
        }
    }

    public void sendWinInfo(int state){
        if (state == 1){
            firstClientWriter.println("WI;1");
            if (secondClient != null)
                secondClientWriter.println("WI;0");
        }
        if (state == 2){
            firstClientWriter.println("WI;0");
            if (secondClient != null)
                secondClientWriter.println("WI;1");
        }
    }
}