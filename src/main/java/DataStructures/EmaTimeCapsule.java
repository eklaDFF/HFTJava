package DataStructures;

public class EmaTimeCapsule {
    private long time;
    private double ema;
    public EmaTimeCapsule (long time, double ema){
        this.time = time;
        this.ema = ema;
    }

    public long getTime() {
        return time;
    }

    public double getEma() {
        return ema;
    }

    public void setEma(double ema) {
        this.ema = ema;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
