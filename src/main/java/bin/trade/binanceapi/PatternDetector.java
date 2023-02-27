package bin.trade.binanceapi;

import bin.trade.binanceapi.records.Candle;

public class PatternDetector {
    double high;
    double low;
    double open;
    double close;
    double prevHigh;
    double prevLow;
    double prevOpen;
    double prevClose;
    public boolean isLong(Candle candle) {
        open = candle.openPrice();
        close = candle.closePrice();
        return close > open;
    }
    public boolean isShort(Candle candle) {
        open = candle.openPrice();
        close = candle.closePrice();
        return open > close;
    }

    public boolean isHammer(Candle candle) {
        high = candle.highPrice();
        low = candle.lowPrice();
        open = candle.openPrice();
        close = candle.closePrice();
        return (((high - low) > 3 * (open - close)) &
                ((close - low) / (0.001 + high - low) > 0.6) &
                ((open - low) / (0.001 + high - low) > 0.6));
    }

    public boolean isInvertedHammer(Candle candle) {
        high = candle.highPrice();
        low = candle.lowPrice();
        open = candle.openPrice();
        close = candle.closePrice();
        return (((high - low) > 3 * (open - close)) &
                ((high - close) / (.001 + high - low) > 0.6)
                & ((high - open) / (.001 + high - low) > 0.6));
    }
    public boolean isBullishHarami(Candle candle, Candle previousCandle) {
        high = candle.highPrice();
        low = candle.lowPrice();
        open = candle.openPrice();
        close = candle.closePrice();
        prevHigh = previousCandle.highPrice();
        prevLow = previousCandle.lowPrice();
        prevOpen = previousCandle.openPrice();
        prevClose = previousCandle.closePrice();
        return ((open > prevOpen) &&
                (close < prevOpen) &&
                (close < prevClose) &&
                (open > prevLow) &&
                (high > prevHigh));
    }
    public boolean isGraveStoneDoji (Candle candle) {
        high = candle.highPrice();
        low = candle.lowPrice();
        open = candle.openPrice();
        close = candle.closePrice();
        return (Math.abs(close - open) / (high - low) < 0.1 &&
                (high - Math.max(close, open)) > (3 * Math.abs(close - open)) &&
                (Math.min(close, open) - low) <= Math.abs(close - open));
    }
}
