import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.File;
import java.util.Arrays;

public class ImageProcessing extends Application {
    private static Image image;
    private static int[][] arrRGB;

    public static void main(String[] args) {
        launch(args);
    }

    private static Image image() {
        File file = new CommonMethods().file("All Images", "*.*");
        image = new Image(file.toURI().toString());
        System.out.println(file);
        return image;
    }

    private int[][] getFiltrationMask(int width, int height) {
        int[][] mask = new int[width][height];
        for (int i = 0; i < height; i++) {
            Arrays.fill(mask[i], 1);
        }
        return mask;
    }

    private int getImageHeight() {
        return (int) image.getHeight();
    }

    private int getImageWidth() {
        return (int) image.getWidth();
    }

    private int getImagePixelsAmount() { return getImageHeight() * getImageWidth(); }

    private void setRedGreenBlue(int pixelNumber, int coordinateX, int coordinateY) {
        int color = image.getPixelReader().getArgb(coordinateX, coordinateY);
        arrRGB = new int[getImagePixelsAmount()][3];
        arrRGB[pixelNumber][0] = (color >> 16) & 0xff;
        arrRGB[pixelNumber][1] = (color >> 8) & 0xff;
        arrRGB[pixelNumber][2] = color & 0xff;
    }

    private void pixelToColors() {

        int pixelNumber = 0;
        for (int coordinateY = 0; coordinateY < getImageHeight(); coordinateY++) {
            for (int coordinateX = 0; coordinateX < getImageWidth(); coordinateX++) {
                setRedGreenBlue(pixelNumber, coordinateX, coordinateY);
                pixelNumber++;
            }
        }
    }

    private int changeColorValueIfOutOfBound(int color) {
        if (color > 255) color = 255;
        else if (color < 0) color = 0;
        return color;
    }

    private Image imageChangedColorsToARGB(WritableImage wr, PixelWriter pw) {
        int Red = 0, Green = 0, Blue = 0;
        for (int i = 0; i < getImagePixelsAmount(); i++) {
            for (int j = 0; j < arrRGB[i].length; j++) {
                int color = changeColorValueIfOutOfBound(arrRGB[i][j]);
                if (j == 0) Red = color;
                else if (j == 1) Green = color;
                else Blue = color;
            }
            pw.setArgb(i % getImageWidth(), i / getImageHeight(), (0xFF << 24) | Red << 16 | Green << 8 | Blue);
        }
        return wr;
    }

    private int sumArrayValues(int[][] filter) {
        int sum = 0;
        for (int[] ints : filter) {
            for (int anInt : ints) {
                sum += anInt;
            }
        }
        return sum;
    }

    private int getPixelColorValue(int maskSize, int[][] filter, int maskCoordX, int maskCoordY, int imgCoordX, int imgCoordY){
        int filterCoordValue = filter[maskSize + maskCoordY][maskSize + maskCoordX];
        int pixelColorValue = ((image.getPixelReader().getArgb(imgCoordX + maskCoordX, imgCoordY + maskCoordY)));
        return filterCoordValue * pixelColorValue;
    }

    private int[] filterColorsWithMask(int[] pixelRGB,int maskSize,int[][] filter,int imgCoordX,int imgCoordY) {
        for (int maskCoordY = -maskSize; maskCoordY <= maskSize; maskCoordY++) {
            for (int maskCoordX = -maskSize; maskCoordX <= maskSize; maskCoordX++) {
                pixelRGB[0] += (getPixelColorValue(maskSize, filter, maskCoordX, maskCoordY, imgCoordX, imgCoordY) >> 16) & 0xff;
                pixelRGB[1] += (getPixelColorValue(maskSize, filter, maskCoordX, maskCoordY, imgCoordX, imgCoordY) >> 8) & 0xff;
                pixelRGB[2] += getPixelColorValue(maskSize, filter, maskCoordX, maskCoordY, imgCoordX, imgCoordY) & 0xff;
            }
        }
        return pixelRGB;
    }

    private void maskFiltration(int maskSize, int[][] filter) {
        int height = getImageHeight();
        int width = getImageWidth();
        for (int i = 0; i < getImagePixelsAmount(); i++) {
            int imgCoordX = i % width;
            int imgCoordY = i / width;
            if (imgCoordX >= maskSize && imgCoordY >= maskSize) {
                if (imgCoordX < width - maskSize && imgCoordY < height - maskSize) {
                    int[] pixelRGB = filterColorsWithMask(arrRGB[i], maskSize, filter, imgCoordX, imgCoordY);
                    arrRGB[i][0] = (pixelRGB[0] / sumArrayValues(filter));
                    arrRGB[i][1] = (pixelRGB[1] / sumArrayValues(filter));
                    arrRGB[i][2] = (pixelRGB[2] / sumArrayValues(filter));
                }
            }
        }
    }

    private void brightness(int brightness) {
        for (int i = 0; i < getImagePixelsAmount(); i++) {
            arrRGB[i][0] =+ brightness;
            arrRGB[i][1] =+ brightness;
            arrRGB[i][2] =+ brightness;
        }
    }

    private void contrast(int contrast) {
        for (int i = 0; i < getImagePixelsAmount(); i++) {
            for (int j = 0; arrRGB[i].length >= 2; j++) {
                if (arrRGB[i][j] > 125) {
                    arrRGB[i][j] *= contrast / 10;
                }
            }
        }
    }

    private void negativePositive(int negative, int negativeCoefficient) {
        for (int i = 0; i < getImagePixelsAmount(); i++) {
            arrRGB[i][0] *= (negative - negativeCoefficient);
            arrRGB[i][1] *= (negative - negativeCoefficient);
            arrRGB[i][2] *= (negative - negativeCoefficient);
        }
    }

    private ImageView imageView(Image image) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    private void showChangedImg(GridPane grid) {
        WritableImage wr = new WritableImage(getImageWidth(), getImageHeight());
        PixelWriter pw = wr.getPixelWriter();
        ImageView imageView = imageView(imageChangedColorsToARGB(wr, pw));
        GridPane.setConstraints(imageView, 1, 0);
        grid.getChildren().add(imageView);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();


        primaryStage.setTitle("Image processing");

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Button fileButton = new Button("New file");
        GridPane.setConstraints(fileButton, 0, 1);
        fileButton.setOnAction(e -> {
            ImageView imageView = imageView(image());
            GridPane.setConstraints(imageView, 0, 0);
            grid.getChildren().add(imageView);
            pixelToColors();
        });


        TextField brighter = new TextField("0");
        GridPane.setConstraints(brighter, 0, 3);

        Button brighterButton = new Button("Brighter/Darker");
        GridPane.setConstraints(brighterButton, 0, 2);
        brighterButton.setOnAction(e -> {
            int brightness = Integer.parseInt(brighter.getText());
            brightness(brightness);
            showChangedImg(grid);
        });

        TextField darker = new TextField("10");
        GridPane.setConstraints(darker, 1, 3);

        Button contrastButton = new Button("contrast");
        GridPane.setConstraints(contrastButton, 1, 2);
        contrastButton.setOnAction(e -> {
            int contrast = Integer.parseInt(darker.getText());
            contrast(contrast);
            showChangedImg(grid);
        });

        //blue = negative - negativeCoefficient*blue;
        Button negativeButton = new Button("Negative");
        GridPane.setConstraints(negativeButton, 0, 4);
        negativeButton.setOnAction(e -> {
            int negative = 255;
            int negativeCoefficient = 1;
            negativePositive(negative, negativeCoefficient);
            showChangedImg(grid);
        });

        Button filterAvg3x3 = new Button("Filter average 3x3");
        GridPane.setConstraints(filterAvg3x3, 1, 4);
        filterAvg3x3.setOnAction(e -> {
            int[][] filter = getFiltrationMask(3, 3);
            int maskSize = 1;
            maskFiltration(maskSize, filter);
            showChangedImg(grid);
        });

        Button filterAvg5x5 = new Button("Filter average 9x9");
        GridPane.setConstraints(filterAvg5x5, 2, 4);
        filterAvg5x5.setOnAction(e -> {

            int[][] filter = getFiltrationMask(5, 5);
            int maskSize = 2;
            maskFiltration(maskSize, filter);
            showChangedImg(grid);
        });


        TextField Input1 = new TextField("1");
        GridPane.setConstraints(Input1, 0, 6);

        TextField Input2 = new TextField("1");
        GridPane.setConstraints(Input2, 1, 6);

        TextField Input3 = new TextField("1");
        GridPane.setConstraints(Input3, 2, 6);

        TextField Input4 = new TextField("1");
        GridPane.setConstraints(Input4, 0, 7);

        TextField Input5 = new TextField("-5");
        GridPane.setConstraints(Input5, 1, 7);

        TextField Input6 = new TextField("1");
        GridPane.setConstraints(Input6, 2, 7);

        TextField Input7 = new TextField("1");
        GridPane.setConstraints(Input7, 0, 8);

        TextField Input8 = new TextField("1");
        GridPane.setConstraints(Input8, 1, 8);

        TextField Input9 = new TextField("1");
        GridPane.setConstraints(Input9, 2, 8);
        Button filterButton = new Button("Filter");
        GridPane.setConstraints(filterButton, 0, 5);


        filterButton.setOnAction(e -> {

            int[][] filter = new int[][]{
                    {Integer.parseInt(Input1.getText()), Integer.parseInt(Input2.getText()), Integer.parseInt(Input3.getText())},
                    {Integer.parseInt(Input4.getText()), Integer.parseInt(Input5.getText()), Integer.parseInt(Input6.getText())},
                    {Integer.parseInt(Input7.getText()), Integer.parseInt(Input8.getText()), Integer.parseInt(Input9.getText())}
            };
            int maskSize = 1;
            maskFiltration(maskSize, filter);
            showChangedImg(grid);
        });

        grid.getChildren().addAll(fileButton, brighterButton, brighter, contrastButton, darker, negativeButton, filterAvg3x3,
                filterAvg5x5, filterButton, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9);
        Scene scene = new Scene(grid, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
