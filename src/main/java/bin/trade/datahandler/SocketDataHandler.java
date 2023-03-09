package bin.trade.datahandler;

import bin.trade.tools.Strategy;
import socket.client.SocketClient;

public class SocketDataHandler implements TradeDataHandler {
    private SocketClient socketClient;
    public SocketDataHandler(String serverName, int port) {
        socketClient = new SocketClient(serverName, port);
    }

    @Override
    public void openPosition(double openPrice, double targetPrice, double stopPrice) {
        String text = ("Opened postition" +
                "\nOpen price: " + openPrice +
                "\nTarget price: " + targetPrice +
                "\nStop price:" + stopPrice);
        socketClient.sendData(text);
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
        socketClient.sendData(text);
    }
    public void receiveOpenOrders(String openOrders) {
        String text = openOrders;
        socketClient.sendData(text);
    }

    public void getMostActiveAsset(String asset) {
        String text = "Most active asset:" + asset;
    }

    public void init() {
        String text = "Trade bot start...";
    }
}
