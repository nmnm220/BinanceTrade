package bin.trade;

import bin.trade.controller.TelegramBotCommandHandler;
import bin.trade.database.DataBaseConnector;
import bin.trade.database.hibernate.HibernateConnector;
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
    private static int checkOutDelay = 35000; //delay for market check
    private static final MarketConnector marketConnector = new BinanceConnector();
    private static final String[] mostActiveToken = marketConnector.getMostActiveToken(); //[0] is name of token [1] is price change
    private static final Logger logger = LoggerFactory.getLogger(TradingBot.class);
    private static TradeDataHandler dataHandler;
    private static Strategy strategy;
    private static DataBaseConnector dataBaseConnector;
    private static TelegramBotCommandHandler telegramBotCommandHandler;

    public static void main(String[] args) {
        try {
            dataHandler = new SocketDataHandler(serverName, port); //set server address and port
            dataHandler.init(); //only then we can initialize everything else
            strategy = new Strategy(marketConnector, dataHandler, mostActiveToken[0], "USDT");
            dataHandler.getMostActiveAsset(mostActiveToken[0], mostActiveToken[1]);
            dataBaseConnector = new HibernateConnector();
            telegramBotCommandHandler = new TelegramBotCommandHandler(dataBaseConnector, strategy); //
        } catch (Exception e) {
            logger.error("Error " + e.getMessage());
            throw new RuntimeException();
        }
        //Thread to check for sale condition with set up timeout to not get a ban
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
        //Listening socket
        Thread socketClientThread = new Thread(() -> {
            String text = "";
            while (true) {
                try {
                    if ((text = dataHandler.getData()) != null) {
                        logger.info("Got command from bot: " + text);
                        dataHandler.sendData(telegramBotCommandHandler.command(text)); //answer from commandHandler
                    }
                } catch (Exception e) {
                    logger.error("Error " + e.getMessage());
                    throw new RuntimeException();
                }
            }
        }, "Socket Thread");

        //starting all threads
        tradeBotThread.start();
        socketClientThread.start();
    }
}
