package bin.trade.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonSimpleParser {
    public double getValue(String input, String fieldName) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(input);
            String output = (String) jsonObject.get(fieldName);
            return Double.parseDouble(output);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public Object getObj(String input, String fieldName) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(input);
            return jsonObject.get(fieldName);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
