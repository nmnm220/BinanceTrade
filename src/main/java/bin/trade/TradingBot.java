package bin.trade;

import bin.trade.datahandler.TelegramDataHandler;
import bin.trade.datahandler.TradeDataHandler;
import bin.trade.market.BinanceConnector;
import bin.trade.market.MarketConnector;
import bin.trade.telegrambot.TradeDataTelegramBot;
import bin.trade.tools.Strategy;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TradingBot {
    private static MarketConnector marketConnector = new BinanceConnector();
    private static String mostActive = marketConnector.getMostActiveToken();
    //private static Boolean tradeBotIdle = false;
    private static final TradeDataTelegramBot telegramBot = new TradeDataTelegramBot();
    private static final TradeDataHandler dataHandler = new TelegramDataHandler(telegramBot);
    private static final Strategy strategy = new Strategy(marketConnector, dataHandler, mostActive, "USDT");
    private static Thread tradeBotThread;

    public static void main(String[] args) {
        Thread telegramBotThread = new Thread(() -> {
            TelegramBotsApi telegramBotsApi = null;
            try {
                telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(telegramBot);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        },"TelegramBot Thread");
        telegramBotThread.start();
    }

    public static void tradeBotStart() {
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
        tradeBotThread.start();
    }
    public static void tradeBotStop() {
        tradeBotThread.interrupt();
    }
}
