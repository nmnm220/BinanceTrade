package bin.trade.binanceapi;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;

import java.util.LinkedHashMap;

public class BinanceConnector {

    public static void main(String[] args) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", "BNBUSDT");
        parameters.put("interval", "1m");

        SpotClient spotClient = new SpotClientImpl();
        System.out.println(spotClient.createMarket().klines(parameters));
    }
}

