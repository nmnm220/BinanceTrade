package bin.trade;

import bin.trade.logic.market.BinanceConnector;
import bin.trade.logic.market.MarketConnector;
import bin.trade.logic.tools.Strategy;

public class TradingBot {
    private static MarketConnector marketConnector = new BinanceConnector();
    private static String mostActive = marketConnector.getMostActiveToken();
    private static final Strategy strategy = new Strategy(marketConnector, mostActive);

    public static void main(String[] args) {
        while (true) {
            strategy.checkOut();
            try {
                Thread.sleep(35000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
