package bin.trade.binanceapi;

public record Kline(long openTime, long closeTime, double openPrice, double closePrice) {
}
