package client.view;

import client.WinInfo;
import supp.InDebug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ViewField extends JPanel{
    private InDebug inDebug = new InDebug();
    private boolean own;
    private boolean isTurn;
    private int sizeX;
    private int sizeY;
    private final ArrayList<Cell> cells;

    private Socket server;

    private PrintWriter serverWriter;

    public ViewField(Socket server, int x, int y, boolean isOwn){
        this.server = server;
        try {
            serverWriter = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException exception) {
            WinInfo dialog = new WinInfo();
            dialog.setText("Unexpected error");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
        own = isOwn;
        sizeX = x;
        sizeY = y;
        setLayout(new GridLayout(x, y));
        cells = new ArrayList<>();
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                cells.add(new Cell(j, i, Cell.CellState.NULL));
                //cells.get(getIndex(i, j)).setEnabled(false);
                if (!own) {
                    cells.get(getIndex(i, j)).addActionListener(this::buttonListener);
                }
                add(cells.get(getIndex(i, j)));
            }
        }
    }

    public void setMyEnabled(boolean enabled){
        for (Cell c : cells){
            c.setEnabled(enabled);
        }
    }

    public boolean isMyTurn(){
        return isTurn;
    }

    private void buttonListener(ActionEvent e){
        if (inDebug.inDebug){
            //System.out.println("is 1 My turn: " + isMyTurn());
        }
        if (!((MainFrame)((Cell)e.getSource()).getParent().getParent().getParent().getParent().getParent().getParent()).isYourTurn()){
            //((Cell)e.getSource()).setBackground(new Color(255,0,0));
            return;
        }
        //((Cell)e.getSource()).setBackground(new Color(0,0,0));
        int x = ((Cell)e.getSource()).getXCoordinate();
        int y = ((Cell)e.getSource()).getYCoordinate();
        StringBuilder stb = new StringBuilder();
        stb.append("GT ");
        stb.append(x);
        stb.append(" ");
        stb.append(y);
        if (inDebug.inDebug){
            System.out.println("Sending turn data: " + stb.toString());
        }
        serverWriter.println(stb.toString());
    }

    public void initField(byte[] input, boolean isYourTurn){
        for (int i = 0; i < cells.size(); i++) {
            cells.get(i).setState(switch (input[i]){
                case (byte)0 -> Cell.CellState.NULL;
                case (byte)1 -> Cell.CellState.MISS;
                case (byte)2 -> Cell.CellState.ALIVE;
                case (byte)3 -> Cell.CellState.WRECKED;
                case (byte)4 -> Cell.CellState.DEATH;
                default -> throw new RuntimeException("Wrong cell state in " + this.getClass() + ".initField");
            });
        }
        this.revalidate();
        this.repaint();
    }

    private int getIndex(int x, int y){
        return x * sizeX + y;
    }

    public Cell getCells(int x, int y) {
        if (x >= sizeX || y >= sizeY)
            throw new RuntimeException("Wrong size in " + this.getClass() + ".getCells");
        return cells.get(getIndex(x, y));
    }
}
