package bin.trade.database.hibernate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trades")
public class Trade {
    public Trade(){}

    @Override
    public String toString() {
        return "Trade{" +
                "id=" + id +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", sellType='" + sellType + '\'' +
                '}';
    }


    public Trade(int id, int assetId, int buyPrice, int sellPrice, int date, String sellType) {
        this.id = id;
        //this.assetId = assetId;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.date = date;
        this.sellType = sellType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    public int getDate() {
        return date;
    }
    public void setDate(int date) {
        this.date = date;
    }

    public Asset getOwner() {
        return owner;
    }

    public void setOwner(Asset owner) {
        this.owner = owner;
    }

    @ManyToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    private Asset owner;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    /*@Column(name = "asset_id")
    private int assetId;*/
    @Column(name = "buy_price")
    private int buyPrice;
    @Column(name = "sell_price")
    private int sellPrice;
    @Column(name = "date")
    private int date;
    @Column(name = "sell_type")
    private String sellType;
}
