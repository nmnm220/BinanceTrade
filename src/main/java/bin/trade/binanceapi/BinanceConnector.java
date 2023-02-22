package bin.trade.binanceapi;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.json.JsonReader;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class BinanceConnector {

    private final List<String> COLUMN_NAMES_KLINES = Arrays.asList("Kline open time", "Open price", "High price", "Low price", "Close price", "Volume", "Kline Close time", "Quote asset volume",
            "Number of trades", "Taker buy base asset volume", "Taker buy quote asset volume");
    private final String API_KEY = "0fE7TrXY6NFrOdaeGiHQLkfC33hcAKXUFpaWPagLxMQ7MbQr57ko5R0kG7hjGV5s";
    private final String SECRET_KEY = "8NOKEJhswNFHNtNZb4nkyZ0158DI1So8Mml1ufEK626o8o8SSP1CLXQPRUpdyVyv";
    private final String URL = "https://testnet.binance.vision";
    private final SpotClient spotClient = new SpotClientImpl(API_KEY, SECRET_KEY, URL);

    public void exchangeInfo() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        System.out.println(spotClient.createMarket().exchangeInfo(parameters));
    }

    public String getMostActiveToken() {

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        //parameters.put("symbol", "BNBUSDT");
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

    public DoubleColumn getLastData(String coinsPair, String time, String lookback) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", coinsPair);
        parameters.put("interval", time);
        parameters.put("limit", lookback);
        String klinesData = spotClient.createMarket().klines(parameters);

        JsonReader jsonReader = new JsonReader();
        Table table = jsonReader.read(Source.fromString(klinesData));
        for (int i = 0; i < COLUMN_NAMES_KLINES.size(); i++) {
            table.column(i).setName(COLUMN_NAMES_KLINES.get(i));
        }
        return table.doubleColumn("Close price");

    }

    public void start() {
        boolean openPostion = false;
        double buyAmt = 20;
        String asset = getMostActiveToken();
        DoubleColumn lastPriceData = getLastData(asset, "1m", "2");
        double lastPrice = lastPriceData.get(0);
        double preLastPrice = lastPriceData.get(1);
        double target = 1.02 * lastPrice;
        double stopLoss = 0.975 * lastPrice;
        double qty = buyAmt / lastPrice;

        String qtyString = String.valueOf(qty).substring(0, 5);

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
        parameters.put("quantity", qtyString);
        spotClient.createTrade().newOrder(parameters);
        openPostion = true;
        System.out.println("Bought: " + asset + " Price: " + lastPrice + " Qty: " + qtyString +
                " Target: " + target + " SL: " + stopLoss);
        while (openPostion) {
            try {
                double currPrice = getLastData(asset, "1m", "2").get(0);
                if ((currPrice >= target) || (currPrice <= stopLoss)) {
                    parameters = new LinkedHashMap<>();
                    parameters.put("symbol", asset);
                    parameters.put("side", "SELL");
                    parameters.put("type", "MARKET");
                    parameters.put("quantity", qtyString);
                    spotClient.createTrade().newOrder(parameters);
                    openPostion = false;
                    double profit = currPrice - lastPrice;
                    System.out.println("Sold Price " + currPrice + " Profit: " + profit);
                } else {
                    Thread.sleep(5000);
                    System.out.println("Current price: " + currPrice);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

