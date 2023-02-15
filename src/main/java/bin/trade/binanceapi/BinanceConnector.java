package bin.trade.binanceapi;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BinanceConnector implements KlinesArray {
    SpotClient spotClient = new SpotClientImpl();

    @Override
    public ArrayList<Double> getKlines(String coinsPair, String time) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", coinsPair);
        parameters.put("interval", time);
        String klinesData = spotClient.createMarket().klines(parameters);
        parseJSON(klinesData);
        ArrayList<Double> klinesArray = new ArrayList<>();
        return klinesArray;
    }
    private void parseJSON(String klines) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Double> openPrice = new ArrayList<>();
        try {

            JSONObject klinesData = (JSONObject) jsonParser.parse(klines);
            System.out.println(klinesData);
            //var stringData = klinesData.getJSONArray("");
            //openPrice.add(stringData.getDouble(1));
            //System.out.println(openPrice);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

