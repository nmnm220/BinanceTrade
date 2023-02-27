package bin.trade.logic.backtest;

import bin.trade.logic.records.Candle;
import bin.trade.logic.records.Parameters;

import java.io.*;
import java.util.*;

public class BackTest {
    private final List<Parameters> bestParameters = new ArrayList<>();

    public static String getSymbol() {
        return symbol;
    }

    public static void setSymbol(String symbol) {
        BackTest.symbol = symbol;
    }

    private static String symbol = null;
    private ArrayList<Candle> marketData = null;

    private ArrayList<Candle> getDataFromFile() {
        ArrayList<String> marketData = new ArrayList<>(530000);
        try (FileReader fr = new FileReader(symbol + ".txt")) {
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            marketData.add(line);
            while (line != null) {
                line = br.readLine();
                if (line != null)
                    marketData.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Candle> candlesData = new ArrayList<>(130000);
        for (int i = 0; i < marketData.size(); i += 4) {
            double openPrice = Double.parseDouble(marketData.get(i));
            double highPrice = Double.parseDouble(marketData.get(i + 1));
            double lowPrice = Double.parseDouble(marketData.get(i + 2));
            double closePrice = Double.parseDouble(marketData.get(i + 3));
            candlesData.add(new Candle(openPrice, highPrice, lowPrice, closePrice));
        }
        return candlesData;
    }

    public void runTest() {
        if (marketData == null) {
            marketData = getDataFromFile();
        }
        TestStrategy strategy = new TestStrategy();
        for (int i = 1; i < marketData.size(); i++) {
            strategy.updateCurrentPrice(marketData.get(i).openPrice(), marketData.get(i - 1));
            strategy.updateCurrentPrice(marketData.get(i).lowPrice(), marketData.get(i - 1));
            strategy.updateCurrentPrice(marketData.get(i).highPrice(), marketData.get(i - 1));
            strategy.updateCurrentPrice(marketData.get(i).closePrice(), marketData.get(i - 1));
        }
        bestParameters.add(strategy.getParameters());
    }

    public List<Parameters> getParameters() {
        return bestParameters;
    }
    public List<Double> getBalanceChange() {
        Parameters bestPar = bestParameters.get(0);
        for (Parameters parameters : bestParameters) {
            if (parameters.balance() > bestPar.balance())
                bestPar = parameters;
        }
        System.out.println("Profit coeff:" + bestPar.PROFIT_COEFF() + " Stop coeff:" +
                bestPar.STOP_COEFF() + " Balance:" + bestPar.balance() +
                " Fee:" + bestPar.fee() + " Orders:" + bestPar.ordersCount());
        return bestPar.balanceChange();
    }
}
