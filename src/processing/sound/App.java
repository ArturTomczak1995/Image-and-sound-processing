package processing.sound;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import static javafx.scene.layout.GridPane.*;
import processing.CommonMethods;

public class App extends Application {


    public void start(Stage primaryStage) {
        primaryStage.setTitle("Wav files processing");

        GridPane grid = new GridPane();
        Edition edition = new Edition();

        grid.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
        grid.setVgap(8.0D);
        grid.setHgap(10.0D);


        Button fileBtn = CommonMethods.setButton("New file", 0,1);
        fileBtn.setOnAction((e) -> edition.setFile());

        TextField periodInput = CommonMethods.setTextField("3",2,2);


        Button graphButton = CommonMethods.setButton("Signal in the time domain", 0,2);
        graphButton.setOnAction((e) -> {
            double period = Double.parseDouble(periodInput.getText());
            BarChart<String, Number> signalChart = edition.getChart(period);
            Provider.showStage(signalChart);
        });

        Button harmoniousBan = CommonMethods.setButton("Harmonious", 0,3);
        harmoniousBan.setOnAction((e) -> {
            BarChart<String, Number> fourierChart = edition.getFourierChart();
            Provider.showStage(fourierChart);
        });

        Label shiftLabel = CommonMethods.setLabel("Shift: ",3,3);
        TextField widowShiftHopSizeInput = CommonMethods.setTextField("4",3,4);

        Label windowLenLabel = CommonMethods.setLabel("Window length: ",3,1);
        TextField windowLenTF = CommonMethods.setTextField("33",3,2);

        Button rectangleFuncBan = CommonMethods.setButton("Rectangle window function", 3,5);
        rectangleFuncBan.setOnAction((e) -> {
            int widowLength = Integer.parseInt(windowLenTF.getText());
            int hopSizeShift = Integer.parseInt(widowShiftHopSizeInput.getText());
            edition.doSTFT("rectangular", widowLength, hopSizeShift);
        });

        Button vonHannBtn = CommonMethods.setButton("Von Hann window function", 3,6);
        vonHannBtn.setOnAction((e) -> {
            int widowLength = Integer.parseInt(windowLenTF.getText());
            int hopSizeShift = Integer.parseInt(widowShiftHopSizeInput.getText());
            edition.doSTFT("vonHann", widowLength, hopSizeShift);
        });

        Button hammingBtn = CommonMethods.setButton("Hamming window function", 3,7);
        hammingBtn.setOnAction((e) -> {
            int widowLength = Integer.parseInt(windowLenTF.getText());
            int hopSizeShift = Integer.parseInt(widowShiftHopSizeInput.getText());
            edition.doSTFT("hamming", widowLength, hopSizeShift);
        });

        Label cutOffFrequencyLbl = CommonMethods.setLabel("Cut off frequency: ",4,1);
        TextField cutOffTF = CommonMethods.setTextField("800",4,2);

        Button lowPassFilterBtn = CommonMethods.setButton("Low pass filter", 4,3);
        lowPassFilterBtn.setOnAction((e) -> {
            int widowLength = Integer.parseInt(cutOffTF.getText());
            int hopSizeShift = Integer.parseInt(widowShiftHopSizeInput.getText());
            edition.doSTFT("lowPassFilter", widowLength, hopSizeShift);
        });


        Button inverseFourierBtn = CommonMethods.setButton("Inverse Fourier", 4,4);
        inverseFourierBtn.setOnAction((e) -> edition.doInverseFourier());

        Button inverseDFTSoundButton = CommonMethods.setButton("Play processing.sound", 4,5);
        inverseDFTSoundButton.setOnAction((e) -> edition.playActualTrack());

        Button removeNoiseBtn = CommonMethods.setButton("Remove noise", 2,1);
        removeNoiseBtn.setOnAction((e) -> edition.RemoveNoise());

        Button wholePathBtn = CommonMethods.setButton("Play processing.sound", 0,5);
        wholePathBtn.setOnAction((e) -> edition.playInputSound());

        Button wholeFrequenciesBtn = CommonMethods.setButton("Display frequencies", 0,6);
        wholeFrequenciesBtn.setOnAction((e) -> {
            edition.wholeFrequencies();
            edition.groupFrequencies();
        });

        Button Play = new Button("Play main frequency");
        setConstraints(Play, 0, 4);
        Play.setOnAction((e) -> edition.playMainFrequency());

        grid.getChildren().addAll(inverseDFTSoundButton, inverseFourierBtn, cutOffFrequencyLbl, cutOffTF, lowPassFilterBtn, windowLenLabel, shiftLabel,
                widowShiftHopSizeInput, vonHannBtn, hammingBtn, rectangleFuncBan, windowLenTF, graphButton, periodInput, fileBtn, harmoniousBan, removeNoiseBtn,
                wholePathBtn, Play, wholeFrequenciesBtn);
        Scene scene1 = new Scene(grid, 850.0D, 350.0D);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
