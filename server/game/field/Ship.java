package server.game.field;

import supp.InDebug;

import java.util.ArrayList;

public class Ship {
    private InDebug inDebug = new InDebug();
    private boolean dead;
    private Field fl;
    private ArrayList<Cell> cells;

    public Ship(Field field){
        dead = false;
        fl = field;
        cells = new ArrayList<>();
    }

    public boolean isAlive(){
        int countAlive = cells.size();
        for (Cell c : cells){
            if(c.isDeath()) return false;
            if(c.isAlive()) return true;
            if(c.isWrecked())
                countAlive--;
        }
        if (countAlive == 0){
            //dead = true;
            return false;
        }
        return true;
    }

    public boolean isDead(){ return dead;}

    public void killThis(){
        if (dead) return;
        dead = true;
        for (Cell c : cells){
            c.setState(Cell.CellState.DEATH);
            ArrayList<Cell> neighbors = fl.getNeighbors(c);
            for (Cell cn : neighbors) {
                if (cn.isDeath() || cn.isWrecked()) continue;
                cn.setState(Cell.CellState.MISS);
            }
        }
    }

    public boolean spawnShip(int x, int y, int size, boolean direction){//true direction = right; false direction = down
        if (direction && fl.cellIsAvailable(x, y) && fl.cellIsAvailable(x + size - 1, y)){
            for (int i = 0; i < size; i++) {
                Cell cell = fl.get(x + i, y);
                if (cell.isAlive()) return false;
                for (Cell c : fl.getNeighbors(cell)){
                    if (c.isAlive()) return false;
                }
            }
            for (int i = 0; i < size; i++) {
                cells.add(fl.get(x + i, y));
                cells.get(cells.size() - 1).setState(Cell.CellState.ALIVE);
            }
            return true;
        }
        if(!direction && fl.cellIsAvailable(x, y) && fl.cellIsAvailable(x, y + size - 1)){
            for (int i = 0; i < size; i++) {
                Cell cell = fl.get(x, y + i);
                if (cell.isAlive()) return false;
                for (Cell c : fl.getNeighbors(cell)){
                    if (c.isAlive()) return false;
                }
            }
            for (int i = 0; i < size; i++) {
                cells.add(fl.get(x , y + i));
                cells.get(cells.size() - 1).setState(Cell.CellState.ALIVE);
            }
            return true;
        }
        return false;
    }


}
