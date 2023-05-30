package bin.trade.database.hibernate;

import bin.trade.database.DataBaseConnector;

public class Test {
    public static void main(String[] args) {
        DataBaseConnector dataBaseConnector = new HibernateConnector();
        System.out.println(dataBaseConnector.getAllTrades());
    }
}
