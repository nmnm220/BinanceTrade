package bin.trade.database;

import bin.trade.database.hibernate.model.Trade;

import java.util.List;

public interface DataBaseConnector {
    void addNewTrade(Trade trade);
    String getAllTrades();
}
