package bin.trade.gui;

import bin.trade.binanceapi.BinanceConnector;
import bin.trade.binanceapi.Kline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    BinanceConnector binanceConnector = new BinanceConnector();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Show klines");
        Pane root = new Pane();
        ArrayList<Kline> klines = binanceConnector.getKlines("BNBUSDT", "1m");
        for (int i = 0; i < 10; i++) {
            double openPrice = klines.get(i).openPrice();
            double closePrice = klines.get(i).closePrice();
            double delta;

            if (openPrice > closePrice) {
                delta = openPrice - closePrice;
            } else delta = closePrice - openPrice;

            Rectangle rectangle = new Rectangle();
            rectangle.setX(20 + i * 10);
            rectangle.setY(50);
            rectangle.setWidth(5);
            rectangle.setHeight(100 * delta);
            root.getChildren().add(rectangle);
            System.out.println(rectangle);
        }
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }
}
