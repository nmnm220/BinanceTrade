package bin.trade;

import bin.trade.datahandler.SocketDataHandler;
import bin.trade.datahandler.TradeDataHandler;
import bin.trade.market.BinanceConnector;
import bin.trade.market.MarketConnector;
import bin.trade.tools.Strategy;
import socket.client.SocketClient;

public class TradingBot {
    private static final String serverName = "127.0.0.1";
    private static final MarketConnector marketConnector = new BinanceConnector();
    private static final String mostActive = marketConnector.getMostActiveToken();
    private static final TradeDataHandler dataHandler = new SocketDataHandler(serverName, 6666);
    private static final Strategy strategy = new Strategy(marketConnector, dataHandler, mostActive, "USDT");
    private static Thread tradeBotThread;

    public static void main(String[] args) {
        tradeBotThread = new Thread(() -> {
            while (true) {
                strategy.checkOut();
                try {
                    Thread.sleep(35000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "TradeBot Thread");
        Thread socketClientThread = new Thread(() -> {
            dataHandler.init();
            dataHandler.getMostActiveAsset(mostActive);
            String text = "";
            while (true) {
                try {
                    if ((text = dataHandler.getData()) != null)
                        System.out.println(text);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        tradeBotThread.start();
        socketClientThread.start();
    }
}
