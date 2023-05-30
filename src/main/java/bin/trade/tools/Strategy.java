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
    public enum SellType {SELL_BY_STOP, SELL_TAKE_PROFIT, NO_SELL, MANUAL}
    private double balance; //current balance
    private double targetPrice; //calculated target price, depends on profit coeff
    private double stopPrice; //calculated stop price, depends on profit coeff
    private boolean isOpenPosition = false;
    private final PatternDetector patternDetector = new PatternDetector();
    //private final List<Candle> candles = new LinkedList<>();
    //private double quantity;
    private String asset; //trading asset

    public void setTradeBalance(double tradeBalance) {
        this.tradeBalance = tradeBalance;
    }

    private double tradeBalance = 20; //trade balance should be set by user
    private final double initBalance; //initial balance for calculations
    private double PROFIT_COEFF = 1.02; //coefficient for take profit sale
    private double STOP_COEFF = 0.55; //coefficient for take stop loss sale
    private String coin = "USDT"; //default "stable" currency
    private String time = "1m"; //candles interval
    private final Logger logger = LoggerFactory.getLogger(Strategy.class);
    private final TradeDataHandler dataHandler; //data output

    public Strategy(MarketConnector marketConnector, TradeDataHandler dataHandler, String asset, String coin) {
        this.dataHandler = dataHandler;
        this.marketConnector = marketConnector;
        this.asset = asset;
        initBalance = marketConnector.getBalance(coin);
    }
    //check out for sale conditions
    public void checkOut() {
        getCurrentPrice(asset);
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
    //returns asset quantity that can be bought for current trade balance
    private String getQuantity(String asset) {
        return String.format(Locale.US,"%.2f", (tradeBalance / getCurrentPrice(asset)));
    }

    private void closePosition(String asset, SellType sellType) {
        double closePrice = marketConnector.closePosition(asset, getQuantity(asset));
        balance = marketConnector.getBalance(coin);
        tradeBalance += balance - initBalance;
        dataHandler.closePosition(closePrice, tradeBalance, sellType);
        logger.info("New trade balance: " + tradeBalance);
    }

    private void openPosition(String asset) {
        double openPosition = marketConnector.openPosition(asset, getQuantity(asset));
        double currentPrice = getCurrentPrice(asset);
        targetPrice = currentPrice * PROFIT_COEFF;
        stopPrice = currentPrice * STOP_COEFF;
        dataHandler.openPosition(openPosition, targetPrice, stopPrice);
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
    public void manualSell() {
        closePosition(asset, SellType.MANUAL);
    }
    public void manualBuy() {
        openPosition(asset);
    }
    public void printCurPrice() {

    }
}
