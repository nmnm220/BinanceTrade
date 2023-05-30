package bin.trade.controller;

import bin.trade.database.DataBaseConnector;
import bin.trade.tools.Strategy;

public class TelegramBotCommandHandler {
    private final DataBaseConnector dataBaseConnector;
    private final Strategy strategy;
    public TelegramBotCommandHandler(DataBaseConnector dataBaseConnector, Strategy strategy) {
        this.strategy = strategy;
        this.dataBaseConnector = dataBaseConnector;

    }

    public String command(String command) {
        switch (command) {
            case ("/orders") -> {
                return getOrders();
            }
            case("/sell") -> {
                strategy.manualSell();
                return "sold";
            }
            case("/buy") -> {
                strategy.manualBuy();
                return "bought";
            }
        }
        return "Unknown command";
    }
    private String getOrders() {
        return dataBaseConnector.getAllTrades();
    }
}
