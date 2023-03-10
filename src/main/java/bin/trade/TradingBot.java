package bin.trade;

import bin.trade.datahandler.SocketDataHandler;
import bin.trade.datahandler.TradeDataHandler;
import bin.trade.market.BinanceConnector;
import bin.trade.market.MarketConnector;
import bin.trade.tools.Strategy;
import socket.client.SocketClient;

public class TradingBot {
    private static final String serverName = "127.0.0.1";
    private static MarketConnector marketConnector = new BinanceConnector();
    private static String mostActive = marketConnector.getMostActiveToken();
    //private static Boolean tradeBotIdle = false;
    private static final TradeDataHandler dataHandler = new SocketDataHandler(serverName, 6666);
    private static final Strategy strategy = new Strategy(marketConnector, dataHandler, mostActive, "USDT");
    private static Thread tradeBotThread;

    public static void main(String[] args) {
        tradeBotThread = new Thread(() -> {
            while (true) {
                dataHandler.init();
                strategy.checkOut();
                try {
                    Thread.sleep(35000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "TradeBot Thread");
        Thread socketClientThread = new Thread(() -> {
            while (true) {
                String text = dataHandler.getData();
                if (text != null)
                    System.out.println(dataHandler.getData());
            }
        });
        tradeBotThread.start();
        socketClientThread.start();
    }
}
