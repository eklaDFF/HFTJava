package org.example;

import CoinPairs.ADAUSDT;

import java.io.IOException;

public class Main {
    static ADAUSDT adausdt;
    static boolean flagForAllowingSystemToRun = false;
    public static void main(String[] args) throws InterruptedException, IOException {
        flagForAllowingSystemToRun  = true;
        do {
            startTheADAUSDT();
            Thread.sleep(86400000);
            adausdt.closeTheADAUSDTSystem();
        }while (flagForAllowingSystemToRun);
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