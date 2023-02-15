package bin.trade.binanceapi;

import java.util.ArrayList;

public interface KlinesArray {
    public ArrayList<Double> getKlines(String coinsPair, String time);
}
