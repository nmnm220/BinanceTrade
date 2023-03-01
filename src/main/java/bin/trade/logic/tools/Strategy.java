package bin.trade.logic.tools;

import bin.trade.logic.market.MarketConnector;
import bin.trade.logic.records.Candle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Strategy {
    private final MarketConnector marketConnector;
    private enum sellType{SELL_BY_STOP, SELL_TAKE_PROFIT, NO_SELL}
    private double balance;
    private double targetPrice;
    private double stopPrice;
    private boolean isOpenPosition = false;
    private final PatternDetector patternDetector = new PatternDetector();
    private final List<Candle> candles = new LinkedList<>();
    private double quantity;
    private String asset;
    private double tradingBalance = 20;
    private final double initBalance;
    private double PROFIT_COEFF = 1.02;
    private double STOP_COEFF = 0.55;
    private String time = "1m";

    public Strategy(MarketConnector marketConnector, String asset) {
        this.marketConnector = marketConnector;
        this.asset = asset;
        initBalance = marketConnector.getBalance("USDT");
    }

    public void checkOut() {
        if (isOpenPosition) {
            if (checkSaleConditions().equals(sellType.SELL_TAKE_PROFIT) || checkSaleConditions().equals(sellType.SELL_BY_STOP)) {
                isOpenPosition = false;
                closePosition();
            }
        } else {
            if (checkBuyConditions()) {
                isOpenPosition = true;
                openPosition();
            }
        }
    }

    public void setCoefficients(double PROFIT_COEFF, double STOP_COEFF) {
        this.PROFIT_COEFF = PROFIT_COEFF;
        this.STOP_COEFF = STOP_COEFF;
    }

    private double getCurrentPrice() {
        return marketConnector.getCurrentPrice(asset);
    }

    private String getQuantity(double balance) {
        return String.valueOf(Math.ceil(tradingBalance / getCurrentPrice()));
    }

    private void closePosition() {
        marketConnector.closePosition(asset, getQuantity(tradingBalance));
        balance = marketConnector.getBalance(asset);
        tradingBalance = balance - initBalance;
    }

    private void openPosition() {
        marketConnector.openPosition(asset, getQuantity(tradingBalance));
        targetPrice = getCurrentPrice() * PROFIT_COEFF;
        stopPrice = getCurrentPrice() * STOP_COEFF;
    }

    private sellType checkSaleConditions() {
        double currentPrice = getCurrentPrice();
        if (currentPrice >= targetPrice) {
            return sellType.SELL_TAKE_PROFIT;
        }
        else if (currentPrice <= stopPrice) {
            return sellType.SELL_BY_STOP;
        }
        else return sellType.NO_SELL;
    }

    private ArrayList<Candle> getCandles(String candlesQuantity) {
        return marketConnector.getLastCandles(asset, time, candlesQuantity);
    }

    private boolean checkBuyConditions() {
        ArrayList<Candle> candles = getCandles("6");
        ArrayList<Boolean> conditions = new ArrayList<>();
        Boolean resultCondition = null;
        conditions.add(patternDetector.isGraveStoneDoji(candles.get(2)));
        conditions.add(patternDetector.isLong(candles.get(1)));
        conditions.add(patternDetector.isLong(candles.get(0)));
        for (Boolean condition : conditions) {
            if (resultCondition == null)
                resultCondition = condition;
            else resultCondition = resultCondition && condition;
        }
        return resultCondition;
    }
}
