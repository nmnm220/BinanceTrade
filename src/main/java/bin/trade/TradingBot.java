package bin.trade;

import bin.trade.datahandler.SocketDataHandler;
import bin.trade.datahandler.TradeDataHandler;
import bin.trade.market.BinanceConnector;
import bin.trade.market.MarketConnector;
import bin.trade.tools.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingBot {
    private static final String serverName = "127.0.0.1";
    private static final int port = 6666;
    private static int checkOutDelay = 35000;
    private static final MarketConnector marketConnector = new BinanceConnector();
    private static final String[] mostActiveToken = marketConnector.getMostActiveToken();
    private static final Logger logger = LoggerFactory.getLogger(TradingBot.class);
    private static TradeDataHandler dataHandler;
    private static Strategy strategy;

    public static void main(String[] args) {
        try {
            dataHandler = new SocketDataHandler(serverName, port);
            dataHandler.init();
            strategy = new Strategy(marketConnector, dataHandler, mostActiveToken[0], "USDT");
            dataHandler.getMostActiveAsset(mostActiveToken[0], mostActiveToken[1]);
        } catch (Exception e) {
            logger.error("Error " + e.getMessage());
            throw new RuntimeException();
        }
        Thread tradeBotThread = new Thread(() -> {
            while (true) {
                strategy.checkOut();
                try {
                    Thread.sleep(checkOutDelay);
                } catch (InterruptedException e) {
                    logger.error("Error " + e.getMessage());
                    throw new RuntimeException();
                }
            }
        }, "TradeBot Thread");
        Thread socketClientThread = new Thread(() -> {
            String text = "";
            while (true) {
                try {
                    if ((text = dataHandler.getData()) != null)
                        logger.info("Got command from server" + text);
                } catch (Exception e) {
                    logger.error("Error " + e.getMessage());
                    throw new RuntimeException();
                }
            }
        }, "Socket Thread");
        tradeBotThread.start();
        socketClientThread.start();
    }
}
