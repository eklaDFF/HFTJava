package org.example;

import CoinPairs.ADAUSDT;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");

        ADAUSDT adausdt = new ADAUSDT();
        if (adausdt.fetchKLineData()==0){
            System.out.println("Fetched Successfully");
        }

        if (adausdt.calculateEMA()==0){
            System.out.println("Calculation of Ema success");
        }

        adausdt.calculateLiveEma();

        System.out.println("main method thread->" + Thread.currentThread().getName());
        do {
            Thread.sleep(1000);
        }while (true);
    }

//    public static void main (String[] args){
//        TestAccount testAccount = new TestAccount();
//        testAccount.buyLong(0.3340);
//        testAccount.sellLong(0.3424);
//
//        testAccount.buyShort(0.3340);
//        testAccount.sellShort(0.3424);
//    }
}