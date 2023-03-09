package bin.trade.datahandler;

import bin.trade.telegrambot.TradeDataTelegramBot;
import bin.trade.tools.Strategy;

public class TelegramDataHandler implements TradeDataHandler {
    TradeDataTelegramBot telegramBot;

    public TelegramDataHandler(TradeDataTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void openPosition(double openPrice, double targetPrice, double stopPrice) {
        String text = ("Opened postition" +
                "\nOpen price: " + openPrice +
                "\nTarget price: " + targetPrice +
                "\nStop price:" + stopPrice);
        telegramBot.sendTradeInfo(text);
    }

    @Override
    public void closePosition(double closePrice, double tradeBalance, Strategy.SellType sellType) {
        String sellTypeText;
        if (sellType.equals(Strategy.SellType.SELL_TAKE_PROFIT))
            sellTypeText = "Sell by takeprofit";
        else
            sellTypeText = "Sell by stoploss";
        String text = ("Closed postition" +
                "\nClose price: " + closePrice +
                "\nNew trade balance: " + tradeBalance +
                "\n" + sellTypeText);
        telegramBot.sendTradeInfo(text);
    }
    public void receiveOpenOrders(String openOrders) {
        String text = openOrders;
        telegramBot.sendTradeInfo(text);
    }

    public void getMostActiveAsset(String asset) {
        String text = "Most active asset:" + asset;
        telegramBot.sendTradeInfo(text);
    }

    public void init() {
        String text = "Trade bot start...";
        telegramBot.sendTradeInfo(text);
    }

    public boolean startTradeBot() {
        return true;
    }
    public boolean stopTradeBot() {
        return false;
    }
}
