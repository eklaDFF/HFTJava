package DemaAccount;

public class TestAccount {
    private double balance = 1000.0;
    private double buyPrice;

    public boolean isLongPositionOpen = false;
    public boolean isShortPositionOpen = false;

    public String buyLong(double price){
        isLongPositionOpen = true;
        buyPrice = price;
        return ("TEST_ORDER(BuyLong) Filled ADA[@Price: " + price + "] Total Fees Collected Till Now : ");

    }
    public String sellLong(double price){
        isLongPositionOpen = false;
        return  ("TEST_ORDER(SellLong) Filled ADA[@Price: " + price + ", Loss/Profit: " + ((price-buyPrice)/buyPrice)*100);

    }
    public String buyShort(double price){
        isShortPositionOpen = true;
        buyPrice = price;
        return ("TEST_ORDER(BuyShort) Filled ADA[@Price: " + price + "] Total Fees Collected Till Now : ");
    }
    public String sellShort(double price){
        isShortPositionOpen = false;
        return  ("TEST_ORDER(SellShort) Filled ADA[@Price: " + price + ", Loss/Profit: " + ((buyPrice-price)/buyPrice)*100);
    }
}
