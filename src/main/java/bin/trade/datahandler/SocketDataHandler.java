package bin.trade.datahandler;

import bin.trade.tools.Strategy;
import socket.client.SocketClient;

public class SocketDataHandler implements TradeDataHandler {
    private SocketClient socketClient;
    private String serverName;
    private int port;
    public SocketDataHandler(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        newConnection(this.serverName, this.port);
    }
    private void newConnection(String serverName, int port) {
        socketClient = new SocketClient(serverName, port);
    }

    @Override
    public void openPosition(double openPrice, double targetPrice, double stopPrice) {
        newConnection(serverName, port);
        String text = ("Opened postition" +
                "\nOpen price: " + openPrice +
                "\nTarget price: " + targetPrice +
                "\nStop price:" + stopPrice);
        socketClient.sendData(text);
    }

    @Override
    public void closePosition(double closePrice, double tradeBalance, Strategy.SellType sellType) {
        newConnection(serverName, port);
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
    public String getData() {
        return socketClient.getData();
    }
    public void receiveOpenOrders(String openOrders) {
        socketClient.sendData(openOrders);
    }

    public void getMostActiveAsset(String asset) {
        String text = "Most active asset:" + asset;
        socketClient.sendData(text);
    }

    @Override
    public void sendCurrentPrice(String price) {
        socketClient.sendData("Current price: " + price);
    }

    public void init() {
        String text = "Trade bot start...";
        socketClient.sendData(text);
    }
}
