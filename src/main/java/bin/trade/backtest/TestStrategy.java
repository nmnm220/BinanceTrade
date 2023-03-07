package bin.trade.backtest;

import bin.trade.tools.PatternDetector;
import bin.trade.records.Candle;
import bin.trade.records.Parameters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TestStrategy {
    Random random = new Random();
    private int ordersCount = 0;
    private final double PROFIT_COEFF = random.nextDouble(0.5) + 1.02;
    private final double STOP_COEFF = 1 - random.nextDouble(0.7);
    //private final double STOP_COEFF = 0.5;
    private final double FEE = 0.001;
    private final double START_BALANCE = 20;
    /*    private final double PROFIT_COEFF = 1.02;
        private final double STOP_COEFF = 0.975;*/
    private double totalFee;
    private double fee;
    private double buyPrice;
    private double targetPrice;
    private double stopPrice;
    private double currentPrice;
    private double totalProfit;
    private boolean isOpenPosition = false;
    private double balance = START_BALANCE;
    private double quantity;
    private final PatternDetector patternDetector = new PatternDetector();
    private final List<Candle> candles = new LinkedList<>();
    private final List<Double> balanceChange = new ArrayList<>();

    private void newOrder(double buyPrice) {
        this.buyPrice = buyPrice;
        targetPrice = buyPrice * PROFIT_COEFF;
        stopPrice = buyPrice * STOP_COEFF;
        ordersCount++;
        quantity = balance / buyPrice;
        openPosition(buyPrice, quantity);
    }

    private void checkSaleConditions(double currentPrice) {
        if ((currentPrice >= targetPrice) || (currentPrice <= stopPrice)) {
            closePosition(currentPrice);
        }
    }
    private boolean checkBuyConditions() {
        ArrayList<Boolean> conditions = new ArrayList<>();
        Boolean resultCondition = null;
        conditions.add(patternDetector.isGraveStoneDoji(getCandle(2)));
        conditions.add(patternDetector.isLong(getCandle(1)));
        conditions.add(patternDetector.isLong(getCandle(0)));
        for (Boolean condition: conditions) {
            if (resultCondition == null)
                resultCondition = condition;
            else resultCondition = resultCondition && condition;
        }
        return resultCondition;
    }
    public void updateCurrentPrice(double currentPrice, Candle candle) {
        candles.add(candle);
        if ((!isOpenPosition) && (candles.size() > 6)) {
            if (checkBuyConditions())
                newOrder(currentPrice);
        }
        else if (isOpenPosition)
            checkSaleConditions(currentPrice);
    }

    private Candle getCandle(int candleOffset) {
        return candles.get(candles.size() - candleOffset - 1);
    }

    private double calculateProfit() {
        return quantity * (currentPrice - buyPrice);
    }

    private void openPosition(double buyPrice, double quantity) {
        fee = (buyPrice * quantity) * FEE;
        totalFee += (buyPrice * quantity) * FEE;
        isOpenPosition = true;
    }

    private void closePosition(double currentPrice) {
        this.currentPrice = currentPrice;
        isOpenPosition = false;
        fee += (currentPrice * quantity) * FEE;
        totalFee += (currentPrice * quantity) * FEE;
        totalProfit += calculateProfit();
        balance = quantity * currentPrice - fee;
        balanceChange.add(balance);
/*        System.out.println("Balance:" + balance);
        System.out.println("Profit, current order:" + calculateProfit());
        System.out.println("Total profit:" + totalProfit);*/
    }

    public Parameters getParameters() {
        double percentProfit = ((balance / START_BALANCE) * 100 - 100);
        if (balance < START_BALANCE)
            percentProfit = -percentProfit;
        return new Parameters(STOP_COEFF, PROFIT_COEFF, totalProfit, balance,
                totalFee, ordersCount, percentProfit, balanceChange);
    }
}
