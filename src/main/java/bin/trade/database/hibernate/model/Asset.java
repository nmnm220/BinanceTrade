package bin.trade.database.hibernate.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "asset")
    private String name;

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    @OneToMany(mappedBy = "owner")
    private List<Trade> trades;
    public Asset(){

    }

    public Asset(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
