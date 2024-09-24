package DemaAccount;

public class TestAccount {
    private double balance = 1000.0;
    private double ADAQuantity;
    private double feesCollected = 0.0;

    public boolean isLongPositionOpen = false;
    public boolean isShortPositionOpen = false;

    public String buyLong(double price){
        isLongPositionOpen = true;
        balance -= 500;
        double buyingFee = 0.2;
        feesCollected += buyingFee;
        ADAQuantity = ((500-0.2)/price);
        return ("TEST_ORDER(BuyLong) Filled ADA[Qt: " + ADAQuantity + ", Pc: " + price + "] Total Fees Collected Till Now : " + feesCollected);
    }
    public String sellLong(double price){
        isLongPositionOpen = false;
        double cash = (ADAQuantity)*price;
        double sellingFee = (cash*0.0004);
        feesCollected += sellingFee;
        cash -= sellingFee;
        balance += cash;
        ADAQuantity = 0;
        return  ("TEST_ORDER(SellLong) Filled ADA[Qt: " + ADAQuantity + ", Pc: " + price + "] Total Fees Collected Till Now : " + feesCollected + "     Earn=" + cash + ", NewBalance=" + balance);

    }
    public String buyShort(double price){
        isShortPositionOpen = true;
        balance -= 500;
        double buyingFee = 0.2;
        feesCollected += buyingFee;
        ADAQuantity = ((500-0.2)/price);
        return ("TEST_ORDER(BuyShort) Filled ADA[Qt: " + ADAQuantity + ", Pc: " + price + "] Total Fees Collected Till Now : " + feesCollected);
    }
    public String sellShort(double price){
        isShortPositionOpen = false;
        double cash = (ADAQuantity)*price;
        double sellingFee = (cash*0.0004);
        feesCollected += sellingFee;
        double diff = 500-cash;
        balance += 500;
        balance += diff;
        balance -= sellingFee;
        ADAQuantity = 0;
        return ("TEST_ORDER(SellShort) Filled ADA[Qt: " + ADAQuantity + ", Pc: " + price + "] Total Fees Collected Till Now : " + feesCollected + "     Earn=" + cash + ", NewBalance=" + balance);

    }
}
