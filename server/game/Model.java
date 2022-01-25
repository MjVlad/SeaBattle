package server.game;

import server.game.field.Field;
import supp.InDebug;

import java.util.ArrayList;

public class Model {
    private InDebug inDebug = new InDebug();
    private boolean firstPlayerTurn = true;
    private Field fieldFirst;
    private Field fieldSecond;

    public Model(){
        fieldFirst = new Field(10, 10);
        fieldSecond = new Field(10, 10);
        //System.out.println(fieldFirst + "\n\n" + fieldSecond);
    }

    public void randSpawnShips(){
        fieldFirst.randSpawnShips();
        fieldSecond.randSpawnShips();
    }

    public boolean whoTurn(){
        return firstPlayerTurn;
        //return true;
    } //true - first player, false - second player

    public short gameState(){
        boolean first = fieldFirst.isAlive();
        boolean second = fieldSecond.isAlive();
        if (first && second) return 0;
        if (first) return 1;
        return 2;
    }

    public void turn(int x, int y){
        boolean out;
        try {
            if (!firstPlayerTurn) {
                out = fieldFirst.turn(x, y);
                fieldFirst.updateField();
                fieldSecond.updateField();
                firstPlayerTurn = !out;
            } else {
                out = fieldSecond.turn(x, y);
                fieldSecond.updateField();
                fieldFirst.updateField();
                firstPlayerTurn = out;
            }
        }
        catch (RuntimeException e){
            if (!e.getMessage().equals("Cell is used")){
                e.printStackTrace();
            }
            else {
                System.out.println("Player's turn is not valid. Do nothing...");
            }
        }
    }

    public String getFirstField(boolean hidden){
        return fieldFirst.toString(hidden);
    }

    public String getSecondField(boolean hidden){
        return fieldSecond.toString(hidden);
    }

//    public byte[] getFirstField(boolean hidden){
//        return fieldFirst.getField(hidden);
//    }
//
//    public byte[] getSecondField(boolean hidden){
//        return fieldSecond.getField(hidden);
//    }

//    public static void main(String[] args){
//        Model model = new Model();
//    }
}
