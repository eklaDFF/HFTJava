package DataStructures;

public class Candle {
    private long time;
    private double price;
    public Candle (long time, double price){
        this.time = time;
        this.price = price;
    }

    public long getTime() {
        return time;
    }

    public double getPrice() {
        return price;
    }
}
