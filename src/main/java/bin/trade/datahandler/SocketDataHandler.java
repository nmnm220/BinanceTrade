package bin.trade.datahandler;

import bin.trade.tools.Strategy;
import socket.SocketClient;
//Class to get and send data from telegram bot
public class SocketDataHandler implements TradeDataHandler {
    private final SocketClient socketClient;

    public SocketDataHandler(String serverName, int port) {
        socketClient = new SocketClient(serverName, port);
    }

    @Override
    public void openPosition(double openPrice, double targetPrice, double stopPrice) {
        String text = ("Opened postition" +
                "\nOpen price: " + openPrice +
                "\nTarget price: " + targetPrice +
                "\nStop price: " + stopPrice);
        socketClient.sendData(text);
    }

    @Override
    public void closePosition(double closePrice, double tradeBalance, Strategy.SellType sellType) {
        String sellTypeText;
        if (sellType.equals(Strategy.SellType.SELL_TAKE_PROFIT))
            sellTypeText = "Sell by takeprofit";
        else if (sellType.equals(Strategy.SellType.MANUAL))
            sellTypeText = "Sell by manual command";
        else
            sellTypeText = "Sell by stoploss";
        String text = ("Closed postition" +
                "\nClose price: " + closePrice +
                "\nNew trade balance: " + tradeBalance +
                "\n" + sellTypeText);
        socketClient.sendData(text);
    }
    public String getData() {
        return socketClient.getData();
    }
    public void receiveOpenOrders(String openOrders) {
        socketClient.sendData(openOrders);
    }

    public void getMostActiveAsset(String asset, String percentChange) {
        String text = "Most active asset: " + asset + ", 24H percent change: " + percentChange;
        socketClient.sendData(text);
    }
    @Override
    public void sendCurrentPrice(String price) {
        socketClient.sendData("Current asset price: " + price);
    }

    public void init() { //bot start signal
        String text = "Trade bot start...";
        socketClient.sendData(text);
    }
    @Override
    public void sendData(String text) {
        socketClient.sendData(text);
    } //utility method to send data
}
