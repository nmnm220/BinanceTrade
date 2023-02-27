package bin.trade.binanceapi;

import bin.trade.binanceapi.records.Candle;
import tech.tablesaw.api.DoubleColumn;

import java.util.ArrayList;
import java.util.List;

public interface MarketConnector {
    void openPosition(String asset, double lastPrice, double target, double stopLoss, String qtyString);
    void closePosition(String asset, String qtyString);
    ArrayList<Candle> getLastCandles(String coinsPair, String time, String lookback);
    double getCurrentPrice(String asset);
    double getBalance(String coin);
    void getOpenOrders();

}
