package processing.image;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;

import processing.CommonMethods;


public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image processing");

        Provider imageProvider = new Provider();
        Edition edition = new Edition();
        GridPane grid = new GridPane();

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Button fileButton = CommonMethods.setButton("New file", 0, 1);
        fileButton.setOnAction(e -> {
            Image imageFile = imageProvider.getImage();
            ImageView imageView = imageProvider.imageView(imageFile);
            GridPane.setConstraints(imageView, 0, 0);
            grid.getChildren().add(imageView);
            edition.setImage(imageFile);
        });

        TextField brighter = CommonMethods.setTextField("0", 0, 3);
        Button brighterButton = CommonMethods.setButton("Brighter/Darker", 0, 2);
        brighterButton.setOnAction(e -> {
            int brightness = Integer.parseInt(brighter.getText());
            edition.brightness(brightness);
            imageProvider.showChangedImg(grid, edition.getImage());
        });

        TextField contrastTxtFld = CommonMethods.setTextField("10", 1, 3);
        Button contrastButton = CommonMethods.setButton("Contrast", 1, 2);
        contrastButton.setOnAction(e -> {
            int contrast = Integer.parseInt(contrastTxtFld.getText());
            edition.contrast(contrast);
            imageProvider.showChangedImg(grid, edition.getImage());
        });

        Button negativeButton = CommonMethods.setButton("Negative", 0, 4);
        negativeButton.setOnAction(e -> {
            int negative = 255;
            int negativeCoefficient = 1;
            edition.negativePositive(negative, negativeCoefficient);
            imageProvider.showChangedImg(grid, edition.getImage());
        });

        Button filterAvg3x3Button = CommonMethods.setButton("Filter average 3x3", 1, 4);
        filterAvg3x3Button.setOnAction(e -> {
            edition.average(1, 3, 3);
            imageProvider.showChangedImg(grid, edition.getImage());
        });

        Button filterAvg5x5 = CommonMethods.setButton("Filter average 9x9", 2, 4);
        filterAvg5x5.setOnAction(e -> {
            edition.average(2, 5, 5);
            imageProvider.showChangedImg(grid, edition.getImage());
        });

        ArrayList<TextField> input = new ArrayList<>();
        int rowIndex = 6;
        for (int i = 0; i < 9; i++) {
            input.add(CommonMethods.setTextField("2", i % 3, rowIndex));
            if (i % 3 == 2) {
                rowIndex += 1;
            }
        }

        Button filterButton = CommonMethods.setButton("Filter", 0, 5);
        filterButton.setOnAction(e -> {
            int[][] mask = new int[3][3];
            int rowIdx = 0;
            for (int i = 0; i < input.size(); i++) {
                mask[rowIdx][i % 3] = (Integer.parseInt(input.get(i).getText()));
                if (i % 3 == 0) {
                    rowIdx += 1;
                }
            }
            edition.maskFiltration(1, mask);
            imageProvider.showChangedImg(grid, edition.getImage());
        });

        grid.getChildren().addAll(input);
        grid.getChildren().addAll(fileButton, brighterButton, brighter, contrastButton, contrastTxtFld, negativeButton, filterAvg3x3Button,
                filterAvg5x5, filterButton);
        Scene scene = new Scene(grid, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
