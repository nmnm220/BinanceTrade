package bin.trade.logic.market;

import bin.trade.logic.records.Candle;
import bin.trade.logic.util.JsonSimpleParser;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.json.JsonReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class BinanceConnector implements MarketConnector {

    private final List<String> COLUMN_NAMES_KLINES = Arrays.asList("Kline open time", "Open price", "High price", "Low price", "Close price", "Volume", "Kline Close time", "Quote asset volume",
            "Number of trades", "Taker buy base asset volume", "Taker buy quote asset volume");
    private final String API_KEY = "0fE7TrXY6NFrOdaeGiHQLkfC33hcAKXUFpaWPagLxMQ7MbQr57ko5R0kG7hjGV5s";
    private final String SECRET_KEY = "8NOKEJhswNFHNtNZb4nkyZ0158DI1So8Mml1ufEK626o8o8SSP1CLXQPRUpdyVyv";
    private final String URL = "https://testnet.binance.vision";
    private final SpotClient spotClient = new SpotClientImpl(API_KEY, SECRET_KEY, URL);
    public String getMostActiveToken() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("type", "FULL");
        String allTokens = spotClient.createMarket().ticker24H(parameters);
        JsonReader jsonReader = new JsonReader();
        Table allTickers = jsonReader.read(Source.fromString(allTokens));
        Table workTickers = allTickers.where(t -> t.stringColumn("symbol").containsString("USDT"));

        workTickers = workTickers.sortDescendingOn("priceChangePercent");
        double maxChange = workTickers.doubleColumn("priceChangePercent").get(0);
        String maxTickerName = workTickers.stringColumn("symbol").get(0);
        System.out.println(maxChange);
        System.out.println(maxTickerName);
        return maxTickerName;
    }

    public ArrayList<Candle> getLastCandles(String coinsPair, String time, String lookback) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", coinsPair);
        parameters.put("interval", time);
        parameters.put("limit", lookback);
        String klinesData = spotClient.createMarket().klines(parameters);

        JsonReader jsonReader = new JsonReader();
        Table candlesData = jsonReader.read(Source.fromString(klinesData));
        for (int i = 0; i < COLUMN_NAMES_KLINES.size(); i++) {
            candlesData.column(i).setName(COLUMN_NAMES_KLINES.get(i));
        }
        ArrayList<Candle> candles = new ArrayList<>(Integer.parseInt(lookback));
        for (Row row: candlesData) {
            double openPrice = row.getDouble("Open price");
            double highPrice = row.getDouble("High price");
            double lowPrice = row.getDouble("Low price");
            double closePrice = row.getDouble("Close price");
            candles.add(new Candle(openPrice, highPrice, lowPrice, closePrice));
        }
        return candles;
    }
    public double getCurrentPrice(String asset) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        String priceData = spotClient.createMarket().tickerSymbol(parameters);
        JsonReader jsonReader = new JsonReader();
        //Table price = jsonReader.read(Source.fromString(priceData));
        JsonSimpleParser parser = new JsonSimpleParser();
        return parser.getValue(priceData, "price");
    }
    public double getBalance(String coin) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("recvWindow", 60000);
        parameters.put("timestamp", System.currentTimeMillis() / 1000);
        String balance = spotClient.createTrade().account(parameters);
        JsonSimpleParser parser = new JsonSimpleParser();
        String balances = parser.getObj(balance, "balances").toString();

        JsonReader jsonReader = new JsonReader();
        Table allBalances = jsonReader.read(Source.fromString(balances));
        Table reqBalance = allBalances.where(c -> c.stringColumn("asset").containsString(coin));
        return reqBalance.doubleColumn("free").get(0);
    }
    public void getOpenOrders() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("recvWindow", 60000);
        parameters.put("timestamp", System.currentTimeMillis() / 1000);
        String openOrders = spotClient.createTrade().getOpenOrders(parameters);
        JsonReader jsonReader = new JsonReader();
        Table allOrders = jsonReader.read(Source.fromString(openOrders));
        System.out.println(openOrders);
    }

    public void closePosition(String asset, String qtyString) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        parameters.put("side", "SELL");
        parameters.put("type", "MARKET");
        parameters.put("quantity", qtyString);
        spotClient.createTrade().newOrder(parameters);
        /*double profit = currPrice - sellPrice;
        System.out.println("Sold Price " + currPrice + " Profit: " + profit);*/
    }

    public void openPosition(String asset, double buyPrice, double target, double stopLoss, String qtyString) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
        parameters.put("quantity", qtyString);
        spotClient.createTrade().newOrder(parameters);
        System.out.println("Bought: " + asset + " Price: " + buyPrice + " Qty: " + qtyString +
                " Target: " + target + " SL: " + stopLoss);
    }
}

