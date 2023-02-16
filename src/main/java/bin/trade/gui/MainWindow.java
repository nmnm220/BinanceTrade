package bin.trade.gui;
import bin.trade.binanceapi.BinanceConnector;
import bin.trade.binanceapi.Kline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainWindow extends Application {
    BinanceConnector binanceConnector = new BinanceConnector();

    public void start(Stage stage) throws Exception {
        stage.setTitle("Show klines");
        StackPane root = new StackPane();
        ArrayList<Kline> klines = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            double openPrice = klines.get(i).openPrice();
            double closePrice = klines.get(i).closePrice();
            double delta;
            if (openPrice > closePrice) {
                delta = openPrice - closePrice;
            } else delta = closePrice - openPrice;
            Rectangle rectangle = new Rectangle(20 , 100 + delta);
            root.getChildren().add(rectangle);
        }
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }
}
