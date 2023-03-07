package test.strategy;

import bin.trade.market.BinanceConnector;
import bin.trade.market.MarketConnector;
import bin.trade.tools.Strategy;

public class StrategyTest {
    public static void main(String[] args) {
        MarketConnector marketConnector = new BinanceConnector();
        //Strategy strategy = new Strategy(marketConnector, "XRPUSDT", "USDT");
/*        String qty = strategy.getQuantity("BTCUSDT");
        strategy.openPosition("BTCUSDT");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        //strategy.closePosition("XRPUSDT");
    }
}
