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
//        adausdt.saveEmaLocally(1032934880229009L,0.34230012735039717);
//        adausdt.saveEmaLocally(1032934880229009L,0.3422805079857333);
////        adausdt.findAccurateIndexOfTimestampStoredLocally();
////        adausdt.isEmaSavedLocally();
//    }
}