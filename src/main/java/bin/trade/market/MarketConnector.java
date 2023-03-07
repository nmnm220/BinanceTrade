package bin.trade.market;

import bin.trade.records.Candle;

import java.util.ArrayList;

public interface MarketConnector {
    double openPosition(String asset, String qtyString);
    double closePosition(String asset, String qtyString);
    ArrayList<Candle> getLastCandles(String coinsPair, String time, String lookback);
    double getCurrentPrice(String asset);
    double getBalance(String coin);
    void getOpenOrders();
    String getMostActiveToken();

}
