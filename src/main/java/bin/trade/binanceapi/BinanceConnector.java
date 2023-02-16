package bin.trade.binanceapi;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BinanceConnector implements KlinesArray {
    SpotClient spotClient = new SpotClientImpl();

    @Override
    public ArrayList<Kline> getKlines(String coinsPair, String time) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", coinsPair);
        parameters.put("interval", time);
        String klinesData = spotClient.createMarket().klines(parameters);
        return parseJSON(klinesData);
    }
    private ArrayList<Kline> parseJSON(String klines) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray klinesData = (JSONArray) jsonParser.parse(klines);
            ArrayList<Kline> klineArrayList = new ArrayList<>();
            for (Object kline: klinesData)
            {
                JSONArray klineToParse = (JSONArray) kline;
                klineArrayList.add(parseSingleKline(klineToParse));
            }
            return klineArrayList;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private Kline parseSingleKline(JSONArray klineArray) {
        long openTime = Long.parseLong(klineArray.get(0).toString());
        double openPrice = Double.parseDouble(klineArray.get(1).toString());
        double closePrice = Double.parseDouble(klineArray.get(4).toString());
        long closeTime = Long.parseLong(klineArray.get(6).toString());
        return new Kline(openTime, closeTime, openPrice, closePrice);
    }
}

