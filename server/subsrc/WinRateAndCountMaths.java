package server.subsrc;

import supp.InDebug;

public class WinRateAndCountMaths {
    private InDebug inDebug = new InDebug();
    private double winRate;
    private int countMaths;

    public WinRateAndCountMaths(){
        winRate = 0;
        countMaths = 0;
    }

    public WinRateAndCountMaths(double winRate, int countMaths) {
        this.winRate = winRate;
        this.countMaths = countMaths;
    }

    public void changeRate(boolean win){
        countMaths++;
        winRate = win? (winRate * (countMaths - 1) + 1) / countMaths : (winRate * (countMaths - 1)) / countMaths;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getCountMaths() {
        return countMaths;
    }

    public void setCountMaths(int countMaths) {
        this.countMaths = countMaths;
    }

    public String toString(){
        return winRate + " " + countMaths;
    }
}
