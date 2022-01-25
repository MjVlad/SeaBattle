package client.view;

import javax.swing.*;
import java.awt.*;

public class Cell extends JButton {
    private int xCoordinate;
    private int yCoordinate;
    private CellState state;

    public Cell(int xPos, int yPos, CellState cellState){
        super();
        setState(cellState);
        xCoordinate = xPos;
        yCoordinate = yPos;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public enum CellState{
        NULL,
        MISS,
        ALIVE,
        WRECKED,
        DEATH
    }

    public boolean isAlive(){
        return state == CellState.ALIVE;
    }
    public boolean isNull(){
        return state == CellState.NULL;
    }
    public boolean isNotAvailable(){
        return state == CellState.MISS || state == CellState.WRECKED || state == CellState.DEATH;
    }

    public void setState(CellState state) {
        this.state = state;
        switch (state){
            case NULL -> setBackground(new Color(0, 0, 180));
            case MISS -> setBackground(new Color(100, 100, 100));
            case ALIVE -> setBackground(new Color(0, 130, 0));
            case WRECKED -> setBackground(new Color(170, 100, 0));
            case DEATH -> setBackground(new Color(90, 0, 0));
        }
    }
}
