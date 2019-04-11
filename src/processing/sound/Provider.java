package processing.sound;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.stage.Stage;

class Provider {

    static void showStage(BarChart<String, Number> data) {
        Stage stage = new Stage();
        Scene scene = new Scene(data, 800.0D, 600.0D);
        stage.setScene(scene);
        stage.show();
    }

}
