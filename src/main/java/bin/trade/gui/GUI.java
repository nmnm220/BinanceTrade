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
    public void start(Stage stage) throws Exception {

    }
}
