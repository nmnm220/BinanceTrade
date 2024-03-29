package bin.trade.market;

import bin.trade.records.Candle;
import bin.trade.util.JsonSimpleParser;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.exceptions.BinanceClientException;
import com.binance.connector.client.impl.SpotClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.json.JsonReader;

import java.util.*;

public class BinanceConnector implements MarketConnector {

    private final List<String> COLUMN_NAMES_KLINES = Arrays.asList("Kline open time", "Open price", "High price", "Low price", "Close price", "Volume", "Kline Close time", "Quote asset volume",
            "Number of trades", "Taker buy base asset volume", "Taker buy quote asset volume");
    private final String API_KEY = "0fE7TrXY6NFrOdaeGiHQLkfC33hcAKXUFpaWPagLxMQ7MbQr57ko5R0kG7hjGV5s";
    private final String SECRET_KEY = "8NOKEJhswNFHNtNZb4nkyZ0158DI1So8Mml1ufEK626o8o8SSP1CLXQPRUpdyVyv";
    private final String URL = "https://testnet.binance.vision";
    private final SpotClient spotClient = new SpotClientImpl(API_KEY, SECRET_KEY, URL);
    private final Logger logger = LoggerFactory.getLogger(BinanceConnector.class);

    public String[] getMostActiveToken() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("type", "FULL");
        try {
            String allTokens = spotClient.createMarket().ticker24H(parameters);
            JsonReader jsonReader = new JsonReader();
            Table allTickers = jsonReader.read(Source.fromString(allTokens));
            Table workTickers = allTickers.where(t -> t.stringColumn("symbol").containsString("USDT"));

            workTickers = workTickers.sortDescendingOn("priceChangePercent");
            double maxChange = workTickers.doubleColumn("priceChangePercent").get(0);
            String maxTickerName = workTickers.stringColumn("symbol").get(0);
            logger.info("Got most active token: " + maxTickerName + ", Percent price change: " + maxChange);
            return new String[]{maxTickerName, String.valueOf(maxChange)};
        } catch (BinanceClientException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public ArrayList<Candle> getLastCandles(String coinsPair, String time, String lookBack) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", coinsPair);
        parameters.put("interval", time);
        parameters.put("limit", lookBack);
        try {
            String klinesData = spotClient.createMarket().klines(parameters);
            JsonReader jsonReader = new JsonReader();
            Table candlesData = jsonReader.read(Source.fromString(klinesData));
            for (int i = 0; i < COLUMN_NAMES_KLINES.size(); i++) {
                candlesData.column(i).setName(COLUMN_NAMES_KLINES.get(i));
            }
            ArrayList<Candle> candles = new ArrayList<>(Integer.parseInt(lookBack));
            for (Row row : candlesData) {
                double openPrice = row.getNumber("Open price");
                double highPrice = row.getNumber("High price");
                double lowPrice = row.getNumber("Low price");
                double closePrice = row.getNumber("Close price");
                candles.add(new Candle(openPrice, highPrice, lowPrice, closePrice));
            }
            logger.debug("Got last " + lookBack + " candles");
            return candles;
        } catch (BinanceClientException e) {
            logger.error("Error: " + e.getMessage());
            return null;
        }
    }

    public double getCurrentPrice(String asset) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        try {
            String priceData = spotClient.createMarket().tickerSymbol(parameters);
            JsonSimpleParser parser = new JsonSimpleParser();
            double currentPrice = parser.getValue(priceData, "price");
            logger.info("Got current price: " + currentPrice);
            return currentPrice;
        } catch (BinanceClientException e) {
            logger.error("Error: " + e.getMessage());
            return 0;
        }
    }

    public double getBalance(String coin) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("recvWindow", 60000);
        parameters.put("timestamp", System.currentTimeMillis() / 1000);
        try {
            String balance = spotClient.createTrade().account(parameters);
            JsonSimpleParser parser = new JsonSimpleParser();
            String balances = parser.getObj(balance, "balances").toString();

            JsonReader jsonReader = new JsonReader();
            Table allBalances = jsonReader.read(Source.fromString(balances));
            Table reqBalance = allBalances.where(c -> c.stringColumn("asset").containsString(coin));
            double balanceOut = Double.parseDouble(reqBalance.column("free").get(0).toString());
            logger.info("Got balance: " + balanceOut);
            return balanceOut;
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return 0;
        }
    }

    public void getOpenOrders() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("recvWindow", 60000);
        parameters.put("timestamp", System.currentTimeMillis() / 1000);
        String openOrders = spotClient.createTrade().getOpenOrders(parameters);
        JsonReader jsonReader = new JsonReader();
        Table allOrders = jsonReader.read(Source.fromString(openOrders));
    }

    public double closePosition(String asset, String qtyString) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        parameters.put("side", "SELL");
        parameters.put("type", "MARKET");
        parameters.put("newOrderRespType", "RESULT");
        parameters.put("quantity", qtyString);
        try {
            String closedPosition = spotClient.createTrade().newOrder(parameters);
            JsonSimpleParser jsonSimpleParser = new JsonSimpleParser();
            String price = String.valueOf(getCurrentPrice(asset));
            String execQty = (String) jsonSimpleParser.getObj(closedPosition, "executedQty");
            logger.info("Closed position: price " + price + " qty: " + execQty);
            return Double.parseDouble(price);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return 0;
        }
    }

    public double openPosition(String asset, String qtyString) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", asset);
        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
        parameters.put("newOrderRespType", "RESULT");
        parameters.put("quantity", qtyString);
        try {
            String openPosition = spotClient.createTrade().newOrder(parameters);
            JsonSimpleParser jsonSimpleParser = new JsonSimpleParser();
            String price = String.valueOf(getCurrentPrice(asset));
            String execQty = (String) jsonSimpleParser.getObj(openPosition, "executedQty");
            logger.info("Opened position: price " + price + " qty: " + execQty);
            return Double.parseDouble(price);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return 0;
        }
    }
}

