package bin.trade.binanceapi.backtest;

import bin.trade.binanceapi.BinanceConnector;
import bin.trade.binanceapi.records.Parameters;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;

import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        BinanceConnector binanceConnector = new BinanceConnector();

        /*BackTest backTest = new BackTest();
        String mostActive = binanceConnector.getMostActiveToken();
        BackTest.setSymbol(mostActive);
        TestDataFileWriter.writeRecentData(BackTest.getSymbol(), 90, "1m");
        test(backTest);*/

        binanceConnector.getOpenOrders();
    }
    private static void test(BackTest backTest) {
        for (int i = 0; i < 1000; i++) {
            backTest.runTest();
        }
        createPlotCoeffToBalance(backTest);
        createPlotBalanceChange(backTest);
    }
    private static void createPlotCoeffToBalance(BackTest backTest) {
        Table profitToCoeff = Table.create("plot data").addColumns(
                DoubleColumn.create("coeff"),
                DoubleColumn.create("balance"));
        List<Parameters> plotData = backTest.getParameters();
        for (Parameters parameters: plotData) {
            profitToCoeff.column("coeff").appendObj(parameters.PROFIT_COEFF());
            profitToCoeff.column("balance").appendObj(parameters.balance());
        }
        //profitToCoeff = profitToCoeff.dropWhere(profitToCoeff.numberColumn(1).isLessThan(0.01));
        Plot.show(ScatterPlot.create("Coeff from profit", profitToCoeff,
                "balance", "coeff"));
    }
    private static void createPlotBalanceChange(BackTest backTest) {
        Table profitToCoeff = Table.create("Balance change").addColumns(
                DoubleColumn.create("balance"),
                IntColumn.create("time"));
        List<Double> plotData = backTest.getBalanceChange();
        int time = 0;
        for (Double balance: plotData) {
            profitToCoeff.column("balance").appendObj(balance);
            profitToCoeff.column("time").appendObj(time++);
        }
        //profitToCoeff = profitToCoeff.dropWhere(profitToCoeff.numberColumn(1).isLessThan(0.01));
        //System.out.println(profitToCoeff);
        Plot.show(ScatterPlot.create("Balance change", profitToCoeff,
                "time", "balance"));
    }
}
