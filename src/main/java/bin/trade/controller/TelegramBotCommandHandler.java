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
                return "";
            }
            case("/buy") -> {
                strategy.manualBuy();
                return "";
            }
            case ("/price") -> {
                return "Current price: " + strategy.printCurPrice();
            }
            case ("/balance") -> {
                return "Balance: " + strategy.printBalance() + " " + strategy.getCoin();
            }
        }
        return "Unknown command";
    }
    private String getOrders() {
        return dataBaseConnector.getAllTrades();
    }
}
