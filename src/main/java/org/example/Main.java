package org.example;

import CoinPairs.ADAUSDT;

import java.io.IOException;

public class Main {
    static ADAUSDT adausdt;
    public static void main(String[] args) throws InterruptedException, IOException {
        do {
            startTheADAUSDT();
            Thread.sleep(86400000);
            adausdt.closeTheADAUSDTSystem();
        }while (true);
    }

    public static void startTheADAUSDT() throws IOException {
        adausdt = new ADAUSDT();
        if (adausdt.fetchKLineData()==0){
            System.out.println("Fetched Successfully");
        }

        if (adausdt.calculateEMA()==0){
            System.out.println("Calculation of Ema success");
        }

        adausdt.calculateLiveEma();

    }

//    public static void main (String[] args) throws IOException {
//        ADAUSDT adausdt = new ADAUSDT();
//        adausdt.storeTradeSignalHistory("hgghh");
//        adausdt.storeTradeSignalHistory("hgghh");
//    }
}