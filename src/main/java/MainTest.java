import bin.trade.binanceapi.BinanceConnector;

public class MainTest {
    public static void main(String[] args) {
        BinanceConnector binanceConnector = new BinanceConnector();
        binanceConnector.getKlines("BNBUSDT", "1m");
    }
}
