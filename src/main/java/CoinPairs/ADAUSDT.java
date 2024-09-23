package CoinPairs;

import DataStructures.Candle;
import DataStructures.EmaTimeCapsule;
import DataStructures.TwoEmaTimeCapsule;
import DemaAccount.TestAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.Market;
import interfaces.TradingMaths;
import jakarta.websocket.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ADAUSDT implements Market, TradingMaths {
    Session webSocketSession;

    TwoEmaTimeCapsule twoEMACapsules;
    Candle[] candles = new Candle[1500];
    boolean crossOver = false;


    private final File file = new File("ADAUSDT.txt");
    RandomAccessFile randomAccessFileReaderWriter;

    private final File tradeLogFile = new File("ADAUSDTtradesSignals.txt");
    BufferedWriter bufferedWriter;

    // Below both should be removed later
    boolean isLongTradeActive = false;
    boolean isShortTradeActive = false;
    TestAccount testAccount = new TestAccount();

    /*
    * locallySavedTime,locallySavedEma stores the ema saved in local file, and it helps to calculate ema if there is saved ema any
    * */
    long locallySavedTime;
    double locallySavedEma;

    public ADAUSDT() throws IOException {
        if (!file.exists()){
            file.createNewFile();
        }
        randomAccessFileReaderWriter = new RandomAccessFile(file,"rw");

        if (!tradeLogFile.exists()){
            tradeLogFile.createNewFile();
        }
        bufferedWriter = new BufferedWriter(new FileWriter(tradeLogFile));
    }

    @Override
    public int fetchKLineData(){
        System.out.println("Fetching K-Line Data for ADAUSDT");

        String fetchKLineDataRESTAPIURL = "https://fapi.binance.com/fapi/v1/klines?symbol=ADAUSDT&interval=5m&limit=1500";

        Request request = new Request.Builder().url(fetchKLineDataRESTAPIURL).build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(response.body().string());



            // Printing the Fetched EMA to console
            DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");

            int i=0;
            for (JsonNode node : jsonNode){
                candles[i++] = new Candle(Long.parseLong(node.get(6).toString()),node.get(4).asDouble());
                System.out.println(obj.format(Long.parseLong(node.get(6).toString())) + "    PRICE : " + node.get(4).toString());
            }

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return 0; // 0 == SUCCESS SIGNAL
    }

    public void calculateEmaUsingFile(){}
    private int saveEmaInFile(){
        return 0;// Success
    }

    @Override
    public int calculateEMA() throws IOException {
        System.out.println("calculateEMA() called.....");
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        double previousEma;
        double smoother = 0.001998001998001998; // value for (2/(1+N)), N = 1000

        // If the last time Ema was stored locally, we will use here
        int indexOfLastMatchedCandle = findAccurateIndexOfTimestampStoredLocally();
        if (indexOfLastMatchedCandle == -1){
            indexOfLastMatchedCandle = 999;
            previousEma = calculateInitialSma();
        }else {
            System.out.println("EMA Calculation from locally stored");
            previousEma = locallySavedEma;
        }


        System.out.println("INITIAL_EMA(SMA)[" + previousEma + "] SMOOTHER_EMA[" + smoother + "] TIMESTAMP : " + obj.format(new Date(candles[999].getTime())));

        twoEMACapsules = new TwoEmaTimeCapsule(new EmaTimeCapsule(candles[indexOfLastMatchedCandle].getTime(),previousEma),new EmaTimeCapsule(candles[(indexOfLastMatchedCandle+1)].getTime(),0.0));

        double currentEma = 0.0;
        for (int i=(indexOfLastMatchedCandle+1); i<1500; i++){
            currentEma = (candles[i].getPrice()) - previousEma;
            currentEma *= smoother;
            currentEma += previousEma;
            System.out.println("NEXT_EMA[" + currentEma + "]" + "PRICE: " + candles[i].getPrice() + " TIMESTAMP : " + obj.format(new Date(candles[i].getTime())));

            twoEMACapsules.storeEmaTime(candles[i].getTime(),currentEma,(candles[i].getPrice()));
            System.out.println(twoEMACapsules);

            // Store the new Ema locally
            saveEmaLocally(candles[i-1].getTime(),previousEma);

            previousEma = currentEma;
        }

        // Preparing CrossOver variable to track the crossing point of ema and price
        crossOver = (candles[1499].getPrice()) >= currentEma;
        System.out.println("Initially Condition Is CROSSOVER(PRICE>=EMA) : " + crossOver);
        storeTradeSignalHistory(obj.format(new Date(candles[1499].getTime())) + "Initially Condition Is CROSSOVER(PRICE>=EMA) : " + crossOver);

        return 0;
    }
    private double calculateInitialSma(){
        double sum = 0.00;
        for (int i=0; i<1000; i++){
            sum = sum + candles[i].getPrice();
        }

        sum = sum/1000;

        return sum;
    }

    public void calculateLiveEma(){
        System.out.println("Calculate LIVE Ema called...");
        DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");

        String liveKLineDataWebSocketURL = "wss://fstream.binance.com/stream?streams=adausdt@kline_5m";

        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            webSocketSession = container.connectToServer(new Endpoint(){
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    System.out.println("WebSocket connection opened : " + Thread.currentThread().getName());
                    session.addMessageHandler(String.class,this::onMessage);
                }


                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode;
                @OnMessage
                public void onMessage(String message) {
                    // Handle incoming messages here
//                    System.out.println("Received message: " + message);

                    try {
                        jsonNode = objectMapper.readTree(message);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    long time = jsonNode.get("data").get("k").get("T").asLong();
                    double price = jsonNode.get("data").get("k").get("c").asDouble();

                    System.out.println(obj.format(new Date(time)) + " PRICE : " + price);
                    twoEMACapsules.storeEmaTime(time,liveEMA(twoEMACapsules.getPrev().getEma(),price),price);
                    System.out.println(twoEMACapsules);


                    /* We are Testing our demo account for trading below  */
                    if (crossOver){
                        if (price < twoEMACapsules.getCurr().getEma()){
                            System.out.println("TRADE ALERT : ShortBuy Condition : CrossUnder begins");
                            storeTradeSignalHistory(obj.format(new Date(time)) + "TRADE ALERT : ShortBuy Condition : CrossUnder begins");
                            if (isLongTradeActive){
                                testAccount.sellLong(price);
                                isLongTradeActive = false;
                            }
                            testAccount.buyShort(price);
                            isShortTradeActive = true;
                            crossOver = false;
                        }
                    }else {
                        if (price >= twoEMACapsules.getCurr().getEma()){
                            System.out.println("TRADE ALERT : LongBuy Condition : CrossOver begins");
                            storeTradeSignalHistory(obj.format(new Date(time)) + "TRADE ALERT : LongBuy Condition : CrossOver begins");
                            if (isShortTradeActive){
                                testAccount.sellShort(price);
                                isShortTradeActive = false;
                            }
                            testAccount.buyLong(price);
                            isLongTradeActive = true;
                            crossOver = true;
                        }
                    }
                }

                @OnClose
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println("WebSocket connection closed: " + closeReason);
                }

                @OnError
                public void onError(Session session, Throwable thr) {
                    System.out.println("WebSocket error: " + thr.getMessage());
                }
            }, URI.create(liveKLineDataWebSocketURL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private double liveEMA(double previousEma, double price){
        double smoother = 0.001998001998001998; // value for (2/(1+N)), N = 1000
        double currentEma = price - previousEma;
        currentEma *= smoother;
        currentEma += previousEma;
        return currentEma;
    }

    public boolean saveEmaLocally(long time, double ema) throws IOException {
        randomAccessFileReaderWriter.setLength(0); // delete the previous content, because we want to overwrite
        randomAccessFileReaderWriter.seek(0);
        String s = time + ":" + ema;
        randomAccessFileReaderWriter.writeBytes(s);
        randomAccessFileReaderWriter.seek(0);
        return true;
    }

    public int findAccurateIndexOfTimestampStoredLocally() throws IOException {
        String s = randomAccessFileReaderWriter.readLine();
        randomAccessFileReaderWriter.seek(0);

        if ( s==null){return -1;}
        String[] ss = s.split(":");
        long time = Long.parseLong(ss[0]);
        double ema = Double.parseDouble(ss[1]);

        for (int i=0; i<candles.length; i++){
            if (candles[i].getTime() == time){
                locallySavedTime = time;
                locallySavedEma = ema;
                return i;
            }
        }
        return -1;
    }

    public void closeTheADAUSDTSystem() throws IOException {
        if (webSocketSession.isOpen()){
            System.out.println("Websocket closing");
            webSocketSession.close();
        }
    }

    //This is temporary method must be removed
    public void storeTradeSignalHistory(String s) {
        try {
            bufferedWriter.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
