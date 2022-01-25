package client.view;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainFrame extends JFrame{
    private boolean isYourTurn;
    private ViewField p1F;
    private ViewField p2F;
    private JPanel fields;

    private Socket server;

    public MainFrame(Socket server, boolean isYourTurn){
        this.server = server;
        this.isYourTurn = isYourTurn;//возможно бесполезно
        setTitle("My game");
        setLayout(new BorderLayout());
        setBounds(150,50,600,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p1F = new ViewField(server, 10, 10, true);
        p2F = new ViewField(server, 10, 10, false);
        p1F.setBorder(BorderFactory.createTitledBorder("Own field"));
        p2F.setBorder(BorderFactory.createTitledBorder("Opponent field"));
        fields = new JPanel(new GridLayout(1, 2));
        fields.add(p1F);
        fields.add(p2F);
        add(fields, BorderLayout.CENTER);

        setVisible(true);
    }

//    public void sendOnServer(int x, int y){
//    }

    public void changeTurn(boolean turn){
        isYourTurn = turn;
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void setFields(byte[] fl, boolean first){
        if (first) {
            p1F.initField(fl, false);
        } else {
//            if (isYourTurn()) p2F.setMyEnabled(true);
//            else p2F.setMyEnabled(false);
            p2F.initField(fl, isYourTurn);
        }
    }

//    public static void main(String[] args){
//        MainFrame fl = new MainFrame();
//    }
}
