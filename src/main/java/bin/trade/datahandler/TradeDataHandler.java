package bin.trade.datahandler;

import bin.trade.tools.Strategy;

public interface TradeDataHandler {
    void openPosition(double openPrice, double targetPrice, double stopPrice);

    void closePosition(double closePrice, double tradeBalance, Strategy.SellType sellType);
    void receiveOpenOrders(String openOrders);
    String getData();
    void init();
    void getMostActiveAsset(String asset, String percentChange);
    void sendCurrentPrice(String price);
    void sendData(String text);
}
