package bin.trade.binanceapi;

import java.util.List;

public record Parameters(double STOP_COEFF, double PROFIT_COEFF, double totalProfit, double balance, double fee, int ordersCount, double percentProfit, List<Double> balanceChange) {
}
