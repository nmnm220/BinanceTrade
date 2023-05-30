package bin.trade.database.hibernate;

import bin.trade.database.DataBaseConnector;
import bin.trade.database.hibernate.model.Asset;
import bin.trade.database.hibernate.model.Trade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Date;
import java.util.List;

public class HibernateConnector implements DataBaseConnector {
    private Date date;
    Configuration configuration = new Configuration().addAnnotatedClass(Trade.class)
            .addAnnotatedClass(Asset.class);

    SessionFactory sessionFactory = configuration.buildSessionFactory();

    @Override
    public void addNewTrade(Trade trade) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(trade);
            session.getTransaction().commit();
        }
    }

    @Override
    public String getAllTrades() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Trade> trades = session.createQuery("FROM Trade").getResultList();
            StringBuilder stringTrades = new StringBuilder();
            for (Trade trade : trades) {
                stringTrades.append("\nId: ");
                stringTrades.append(trade.getId());
                stringTrades.append("\nDate: ");
                date = new Date(trade.getDate() * 1000L);
                stringTrades.append(date);
                stringTrades.append("\nAsset: ");
                stringTrades.append(trade.getOwner().getName());
                stringTrades.append("\nBuy price: ");
                stringTrades.append(trade.getBuyPrice());
                stringTrades.append("\nSell price: ");
                stringTrades.append(trade.getSellPrice());
                stringTrades.append("\nSell type: ");
                stringTrades.append(trade.getSellType());
                stringTrades.append("\n");
            }
            return stringTrades.toString();
        }
    }
}
