package DataStructures;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TwoEmaTimeCapsule {
    private EmaTimeCapsule prev;
    private EmaTimeCapsule curr;

    public TwoEmaTimeCapsule(EmaTimeCapsule prev, EmaTimeCapsule curr){
        this.prev = prev;
        this.curr = curr;
    }

    public int swap(){
        EmaTimeCapsule temp = curr;
        curr = prev;
        prev = temp;

        return 0; // Successful
    }

    public EmaTimeCapsule getPrev(){
        return prev;
    }

    public EmaTimeCapsule getCurr(){
        return curr;
    }

    public void setPrev(EmaTimeCapsule prev) {
        this.prev = prev;
    }

    public void setCurr(EmaTimeCapsule curr) {
        this.curr = curr;
    }

    @Override
    public String toString(){
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");

        return "TWO_EMA_CAPSULE{" + obj.format(new Date(prev.getTime())) + "[" + prev.getEma() + "] || " + obj.format(new Date(curr.getTime())) + "[" + curr.getEma() + "]}";

    }

    public void storeEmaTime(long time, double ema, double price){
        if (time <= getCurr().getTime()){
            getCurr().setEma(ema);
        } else {
            getPrev().setTime((getCurr().getTime() + 300000));
            swap();
            ema = liveEMA(prev.getEma(),price); // This will correct the ema
            storeEmaTime(time,ema,price);
        }
    }

    private double liveEMA(double previousEma, double price){
        double smoother = 0.001998001998001998; // value for (2/(1+N)), N = 1000
        double currentEma = price - previousEma;
        currentEma *= smoother;
        currentEma += previousEma;
        return currentEma;
    }

}
