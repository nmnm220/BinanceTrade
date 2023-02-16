package bin.trade.binanceapi;

import java.util.ArrayList;

public interface KlinesArray {
    public ArrayList<Kline> getKlines(String coinsPair, String time);
}
