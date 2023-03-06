package test.strategy;

import bin.trade.logic.market.BinanceConnector;
import bin.trade.logic.market.MarketConnector;
import bin.trade.logic.tools.Strategy;

public class StrategyTest {
    public static void main(String[] args) {
        MarketConnector marketConnector = new BinanceConnector();
        Strategy strategy = new Strategy(marketConnector, "XRPUSDT", "USDT");
/*        String qty = strategy.getQuantity("BTCUSDT");
        strategy.openPosition("BTCUSDT");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        strategy.closePosition("XRPUSDT");
    }
}
