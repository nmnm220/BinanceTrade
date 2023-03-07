package bin.trade;

import bin.trade.datahandler.TelegramDataHandler;
import bin.trade.datahandler.TradeDataHandler;
import bin.trade.market.BinanceConnector;
import bin.trade.market.MarketConnector;
import bin.trade.tools.Strategy;

public class TradingBot {
    private static MarketConnector marketConnector = new BinanceConnector();
    private static String mostActive = marketConnector.getMostActiveToken();
    private static TradeDataHandler dataHandler = new TelegramDataHandler();
    private static final Strategy strategy = new Strategy(marketConnector, dataHandler, mostActive, "USDT");

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
