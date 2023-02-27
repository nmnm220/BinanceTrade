package bin.trade.logic.tools;

import bin.trade.logic.market.MarketConnector;
import bin.trade.logic.records.Candle;

import java.util.LinkedList;
import java.util.List;

public class Strategy {
    private final MarketConnector marketConnector;
    private double balance;
    private double targetPrice;
    private double stopPrice;
    private boolean isOpenPosition = false;
    private final PatternDetector patternDetector = new PatternDetector();
    private final List<Candle> candles = new LinkedList<>();
    private double quantity;
    private String asset;

    Strategy(MarketConnector marketConnector, String asset) {
        this.marketConnector = marketConnector;
        this.asset = asset;
        balance = marketConnector.getBalance("USDT");
    }
    private double getCurrentPrice() {
        return marketConnector.getCurrentPrice(asset);
    }
    private void closePosition(double currentPrice) {
        //marketConnector.closePosition(asset, quantity);
    }
    private void checkSaleConditions(double currentPrice) {
        if ((currentPrice >= targetPrice) || (currentPrice <= stopPrice)) {
            closePosition(currentPrice);
        }
    }
}
