package server.game.field;

import supp.InDebug;

public class Cell  {
    private InDebug inDebug = new InDebug();
    private int x;
    private int y;
    private CellState state;

    public Cell(int xCoordinate, int yCoordinate, CellState cellState){
        state = cellState;
        x = xCoordinate;
        y = yCoordinate;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public CellState getState() {
        return state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAlive(){
        return state == CellState.ALIVE;
    }

    public boolean isWrecked(){
        return state == CellState.WRECKED;
    }

    public boolean isDeath(){
        return state == CellState.DEATH;
    }

    public enum CellState{
        NULL,
        MISS,
        ALIVE,
        WRECKED,
        DEATH
    }
}
