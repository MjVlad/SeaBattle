package server.game.field;

import supp.InDebug;

import java.util.ArrayList;
import java.util.Random;

public class Field {
    private InDebug inDebug = new InDebug();
    private ArrayList<Ship> ships; // 1+2+3+4
    private int xSize;
    private int ySize;
    private Cell[][] fl;

    public Field(int x, int y) {
        xSize = x;
        ySize = y;
        fl = new Cell[x][y];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                fl[i][j] = new Cell(i, j, Cell.CellState.NULL);
            }
        }
        ships = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ships.add(new Ship(this));
        }
    }

    public Cell get(int x, int y){
        if (!cellIsAvailable(x, y)) throw new RuntimeException("Wrong index in " + this.getClass() + ".get");
        return fl[x][y];
    }
    
    public boolean cellIsAvailable(int x, int y){
        return !(x < 0 || x >= xSize || y < 0 || y >= ySize);
    }

    public boolean turn(int x, int y){
        if (!cellIsAvailable(x, y)) throw new RuntimeException("Wrong index in " + this.getClass() + ".turn");
        Cell cell = get(x, y);
        switch (cell.getState()) {
            case NULL -> {
                cell.setState(Cell.CellState.MISS);
                return false;
            }
            case ALIVE -> {
                cell.setState(Cell.CellState.WRECKED);
                return true;
            }
            default -> throw new RuntimeException("Cell is used");
        }
    }

    public void updateField(){
        for (Ship s : ships){
            if (!s.isAlive()) {
                s.killThis();
            }
        }
    }

    public boolean isAlive(){
        for (Ship s : ships){
            if (s.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public void randSpawnShips(){
        int size = 4;
        int index = 0;
        for (int i = 1; i <= size; i++) {
            for (int j = 0; j < size - (i - 1); j++) {
                randSpawnShip(index, i);
                index++;
            }
        }
    }

    private void randSpawnShip(int index, int size){
        if (size < 1 || size > 4) throw new RuntimeException("Wrong size in " + this.getClass() + ".randSpawnShip");
        if (index < 0 || index >= 10) throw new RuntimeException("Wrong index in " + this.getClass() + ".randSpawnShip");
        Random random = new Random();
        while (!ships.get(index).spawnShip(random.nextInt(10), random.nextInt(10), size, random.nextBoolean()));
    }

    public ArrayList<Cell> getNeighbors(Cell cell){
        ArrayList<Cell> out = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++){
                if ((i ==0 && j == 0) || !cellIsAvailable(cell.getX() + i, cell.getY() + j)) continue;
//                if (inDebug.inDebug){
//                    System.out.println((cell.getX() + i) + " | " + (cell.getY() + j) + "  is Neighbor");
//                }
                out.add(get(cell.getX() + i, cell.getY() + j));
            }
        }
        return out;
    }

    public byte[] getField(boolean hidden){
        byte[] builder = new byte[ySize * xSize];
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                Cell c = get(j, i);
                builder[i * ySize + j] = switch (c.getState()){
                    case NULL -> (byte)0;
                    case MISS -> (byte)1;
                    case ALIVE -> hidden? (byte) 0 : (byte) 2;
                    case WRECKED -> (byte)3;
                    case DEATH -> (byte)4;
                };
            }
        }
        return builder;
    }

    public String toString(boolean hidden){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                Cell c = get(j, i);
                switch (c.getState()){
                    case NULL -> stringBuilder.append(0);
                    case MISS -> stringBuilder.append(1);
                    case ALIVE -> stringBuilder.append(hidden? 0 : 2);
                    case WRECKED -> stringBuilder.append(3);
                    case DEATH -> stringBuilder.append(4);
                }
                //if (i != ySize - 1 && j != xSize - 1)
                stringBuilder.append(" ");
            }
            //stringBuilder.append('\n');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
