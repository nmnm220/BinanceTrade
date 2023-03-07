package bin.trade.datahandler;

import bin.trade.tools.Strategy;

public interface TradeDataHandler {
    void openPosition(double openPrice, double tradeBalance, double targetPrice, double stopPrice);

    void closePosition(double closePrice, Strategy.SellType sellType);

}
