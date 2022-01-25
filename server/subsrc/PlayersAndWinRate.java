package server.subsrc;

import supp.InDebug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

public class PlayersAndWinRate {
    private InDebug inDebug = new InDebug();
    private Path path;
    private static HashMap<String, WinRateAndCountMaths> playersAndWinRate;

    public PlayersAndWinRate(){
        playersAndWinRate = new HashMap<>();
        path = Paths.get("D:\\Учёба\\kursach2\\src\\winrate.txt");
        try {
            String fl = new String(Files.readAllBytes(path));
            if(fl.isEmpty()) return;
            String[] lines = fl.split("\n");
            for (String line : lines){
                String[] tmp = line.split(" ");
                synchronized (playersAndWinRate){
                    playersAndWinRate.put(tmp[0], new WinRateAndCountMaths(Double.parseDouble(tmp[1]), Integer.parseInt(tmp[2])));
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public double getWinRate(String name){
        return playersAndWinRate.get(name).getWinRate();
    }

    public boolean isRegistered(String name){
        synchronized (playersAndWinRate){
            return playersAndWinRate.containsKey(name);
        }
    }

    public void add(String name){
        synchronized (playersAndWinRate){
            playersAndWinRate.put(name, new WinRateAndCountMaths(0, 0));
            String text = name + " " + String.valueOf((double)0) + " " + String.valueOf(0) + "\n";
            try {
                Files.writeString(path, text, StandardOpenOption.APPEND);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void changeWinRate(String name, boolean win){
        synchronized (playersAndWinRate){
            playersAndWinRate.get(name).changeRate(win);
            try {
                Files.writeString(path, this.toString());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        synchronized (playersAndWinRate) {
            for (String name : playersAndWinRate.keySet()) {
                stringBuilder.append(name + " " + playersAndWinRate.get(name) + "\n");
            }
        }
        return stringBuilder.toString();
    }
}
