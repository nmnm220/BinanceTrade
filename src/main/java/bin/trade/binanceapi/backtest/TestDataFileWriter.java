package bin.trade.binanceapi.backtest;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.Source;
import tech.tablesaw.io.json.JsonReader;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;

public class TestDataFileWriter {
    private static long START_TIME = 1667250000;
    private static long CURRENT_TIME = System.currentTimeMillis() / 1000;
    private static long END_TIME = 1671787489;
    private static final int LIMIT = 1000;

    public static void writeRecentDataToFile(String symbol) {
        String startTime = START_TIME + "000";
        String endTime = END_TIME + "000";
        SpotClient sc = new SpotClientImpl();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", symbol);
        parameters.put("interval", "1m");
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);

        String rawData = sc.createMarket().klines(parameters);
        JsonReader jsonReader = new JsonReader();

        Table candlesData = jsonReader.read(Source.fromString(rawData));

        try (FileWriter fw = new FileWriter(symbol + ".txt", true)) {
            writeColumnsToFile(candlesData, fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeColumnsToFile(Table candlesData, FileWriter fw) throws IOException {
        for (int j = 0; j < candlesData.column(0).size(); j++) {
            fw.write(candlesData.column(1).get(j) + "\n"); //Open price
            fw.write(candlesData.column(2).get(j) + "\n"); // High price
            fw.write(candlesData.column(3).get(j) + "\n"); // Low price
            fw.write(candlesData.column(4).get(j) + "\n"); // Close price
        }
        fw.flush();
    }

    public static void writeRecentData(String symbol, int days, String interval) {
        long daysInSeconds = Duration.ofDays(days).getSeconds();
        START_TIME = CURRENT_TIME - daysInSeconds;
        String startTime = START_TIME + "000";
        int intInterval = Integer.parseInt(interval.substring(0, 1));

        SpotClient sc = new SpotClientImpl();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", symbol);
        parameters.put("interval", interval);
        parameters.put("limit", LIMIT);
        JsonReader jsonReader = new JsonReader();

        long limitInSeconds = intInterval * Duration.ofMinutes(LIMIT).getSeconds();
        int duration = (int) (daysInSeconds / limitInSeconds);

        for (int i = 0; i < duration; i++) {
            parameters.put("startTime", startTime);
            String rawData = sc.createMarket().klines(parameters);
            Table candlesData = jsonReader.read(Source.fromString(rawData));

            try (FileWriter fw = new FileWriter(symbol + ".txt", true)) {
                writeColumnsToFile(candlesData, fw);
                START_TIME += limitInSeconds;
                startTime = START_TIME + "000";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void writeThreeMonthsDataToFileFiveMinutes(String symbol) {
        SpotClient sc = new SpotClientImpl();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        String startTime = START_TIME + "000";

        parameters.put("symbol", symbol);
        parameters.put("interval", "5m");
        parameters.put("limit", "1000");

        for (int i = 0; i <= 130; i++) {
            parameters.put("startTime", startTime);
            String rawData = sc.createMarket().klines(parameters);
            JsonReader jsonReader = new JsonReader();

            Table candlesData = jsonReader.read(Source.fromString(rawData));

            try (FileWriter fw = new FileWriter(symbol + ".txt", true)) {
                writeColumnsToFile(candlesData, fw);
                START_TIME += 300000;
                startTime = START_TIME + "000";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void writeHoursData(String symbol) {
        SpotClient sc = new SpotClientImpl();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        String startTime = START_TIME + "000";

        parameters.put("symbol", symbol);
        parameters.put("interval", "1h");
        parameters.put("limit", "1000");

        for (int i = 0; i <= 130; i++) {
            parameters.put("startTime", startTime);
            String rawData = sc.createMarket().klines(parameters);
            JsonReader jsonReader = new JsonReader();

            Table candlesData = jsonReader.read(Source.fromString(rawData));

            try (FileWriter fw = new FileWriter(symbol + ".txt", true)) {
                writeColumnsToFile(candlesData, fw);
                START_TIME += 3600000;
                startTime = START_TIME + "000";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
