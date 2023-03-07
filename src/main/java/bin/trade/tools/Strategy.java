package bin.trade.tools;

import bin.trade.datahandler.TradeDataHandler;
import bin.trade.market.MarketConnector;
import bin.trade.records.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Locale;

public class Strategy {
    private final MarketConnector marketConnector;
    public enum SellType {SELL_BY_STOP, SELL_TAKE_PROFIT, NO_SELL}
    private double balance;
    private double targetPrice;
    private double stopPrice;
    private boolean isOpenPosition = false;
    private final PatternDetector patternDetector = new PatternDetector();
    //private final List<Candle> candles = new LinkedList<>();
    //private double quantity;
    private String asset;
    private double tradeBalance = 20;
    private final double initBalance;
    private double PROFIT_COEFF = 1.02;
    private double STOP_COEFF = 0.55;
    private String coin = "USDT";
    private String time = "1m";
    private final Logger logger = LoggerFactory.getLogger(Strategy.class);
    private final TradeDataHandler dataHandler;

    public Strategy(MarketConnector marketConnector, TradeDataHandler dataHandler, String asset, String coin) {
        this.dataHandler = dataHandler;
        this.marketConnector = marketConnector;
        this.asset = asset;
        initBalance = marketConnector.getBalance(coin);
    }

    public void checkOut() {
        if (isOpenPosition) {
            SellType sellType = checkSaleConditions();
            if (sellType.equals(SellType.SELL_TAKE_PROFIT) || sellType.equals(SellType.SELL_BY_STOP)) {
                isOpenPosition = false;
                closePosition(asset, sellType);
            }
        } else {
            if (checkBuyConditions()) {
                isOpenPosition = true;
                openPosition(asset);
            }
        }
    }

    public void setCoefficients(double PROFIT_COEFF, double STOP_COEFF) {
        this.PROFIT_COEFF = PROFIT_COEFF;
        this.STOP_COEFF = STOP_COEFF;
    }

    private double getCurrentPrice(String asset) {
        return marketConnector.getCurrentPrice(asset);
    }

    private String getQuantity(String asset) {
        return String.format(Locale.US,"%.3f", (tradeBalance / getCurrentPrice(asset)));
    }

    private void closePosition(String asset, SellType sellType) {
        double closePrice = marketConnector.closePosition(asset, getQuantity(asset));
        balance = marketConnector.getBalance(coin);
        tradeBalance += balance - initBalance;
        dataHandler.receiveClosePositionPrice(closePrice, sellType);
        logger.info("New trade balance: " + tradeBalance);
    }

    private void openPosition(String asset) {
        double openPosition = marketConnector.openPosition(asset, getQuantity(asset));
        targetPrice = getCurrentPrice(asset) * PROFIT_COEFF;
        stopPrice = getCurrentPrice(asset) * STOP_COEFF;
        dataHandler.openPosition(openPosition, tradeBalance, targetPrice, stopPrice);
        logger.info("Target: " + targetPrice);
        logger.info("Stop: " + stopPrice);
    }

    private SellType checkSaleConditions() {
        double currentPrice = getCurrentPrice(asset);
        if (currentPrice >= targetPrice) {
            return SellType.SELL_TAKE_PROFIT;
        }
        else if (currentPrice <= stopPrice) {
            return SellType.SELL_BY_STOP;
        }
        else return SellType.NO_SELL;
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
