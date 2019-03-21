import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


public class ImageProcessing extends Application {

    private static Image image;
    private static int total_pixels;
    private static int[][] ColorsBeforeFiltration;
    private static TextField brighter;
    private static TextField darker;
    private GridPane grid = new GridPane();
    private static int contrast = 10;
    private static int brightness = 0;
    private static int negative = 0;
    private int negativeCoefficient = -1;
    private int[][] filter;
    private int maskSize;


    private int[][] mask3x3 = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
    };

    private int[][] mask5x5 = new int[][]{
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1}
    };

    

    public static void main(String[] args) {
        launch(args);
    }

    private void pixelReader() {
        PixelReader pixelReader = image.getPixelReader();
        System.out.println();
        System.out.println("Image Height: " + image.getHeight() + " Image Width: "
                + image.getWidth() + " Pixel Format: " + pixelReader.getPixelFormat());
        total_pixels = (int) image.getHeight() * (int) image.getWidth();
        int i = 0;
        ColorsBeforeFiltration = new int[total_pixels][3];
        for (int readY = 0; readY < image.getHeight(); readY++) {
            for (int readX = 0; readX < image.getWidth(); readX++) {
                int color = image.getPixelReader().getArgb(readX, readY);
                int  red = (color >> 16) & 0xff;
                int green = (color >> 8) & 0xff;
                int blue = color & 0xff;
                ColorsBeforeFiltration[i][0]= red;
                ColorsBeforeFiltration[i][1]= green;
                ColorsBeforeFiltration[i][2]= blue;
                i++;
            }
        }
    }


    private Image getImageFromData() {
        WritableImage wr = new WritableImage((int)image.getWidth(), (int)image.getHeight());
        PixelWriter pw = wr.getPixelWriter();

        for (int i = 0; i < total_pixels; i++) {

            int Red = ColorsBeforeFiltration[i][0];
            int Green = ColorsBeforeFiltration[i][1];
            int Blue = ColorsBeforeFiltration[i][2];

            if (Red >= 225) {
                Red = 225;
            }
            if (Green >= 225) {
                Green = 225;
            }
            if (Blue >= 225) {
                Blue = 225;
            }

            if (Red < 0) {
                Red = 0;
            }
            if (Green < 0) {
                Green = 0;
            }
            if (Blue < 0) {
                Blue = 0;
            }

            pw.setArgb(i % (int)image.getWidth(), i/(int)image.getHeight(), (0xFF << 24) | Red << 16 | Green << 8 | Blue);
        }

        return wr;
    }


    private void maskFiltration(){
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();

            for (int i = 0; i < total_pixels; i++) {

                int x = i % width;
                int y = i / width;

                int red = ColorsBeforeFiltration[i][0];
                int green = ColorsBeforeFiltration[i][1];
                int blue = ColorsBeforeFiltration[i][2];

                if (x >= maskSize && y >= maskSize) {
                    if (x < width - maskSize && y < height - maskSize) {
                        for (int j = -maskSize; j <= maskSize; j++) {
                            for (int k = -maskSize; k <= maskSize; k++) {
                                red += (filter[maskSize + j][maskSize + k] * ((image.getPixelReader().getArgb(x+ k, y + j)))>> 16) & 0xff;
                                green += (filter[maskSize + j][maskSize + k] * ((image.getPixelReader().getArgb(x + k, y + j))>> 8) & 0xff);
                                blue += filter[maskSize + j][maskSize + k] * ((image.getPixelReader().getArgb(x + k, y + j)) & 0xff);
                            }
                        }
                        red = (red / sum(filter));
                        green = (green / sum(filter));
                        blue = (blue / sum(filter));
                    }
                }
                    ColorsBeforeFiltration[i][0] = red;
                    ColorsBeforeFiltration[i][1] = green;
                    ColorsBeforeFiltration[i][2] = blue;
        }
    }

    private void Brightness() {
        for (int i = 0; i < total_pixels; i++) {
            int red = ColorsBeforeFiltration[i][0] + brightness;
            int green = ColorsBeforeFiltration[i][1] + brightness;
            int blue = ColorsBeforeFiltration[i][2] + brightness;


            ColorsBeforeFiltration[i][0]= red;
            ColorsBeforeFiltration[i][1]= green;
            ColorsBeforeFiltration[i][2]= blue;
        }
    }

    private void Contrast() {
        for (int i = 0; i < total_pixels; i++) {

            int red = ColorsBeforeFiltration[i][0];
            int green = ColorsBeforeFiltration[i][1];
            int blue = ColorsBeforeFiltration[i][2];

            if (ColorsBeforeFiltration[i][0] > 125) {
                red = red * contrast / 10;
            }
            if (ColorsBeforeFiltration[i][1] > 125) {
                green = green * contrast / 10;
            }
            if (ColorsBeforeFiltration[i][2] > 125) {
                blue = blue * contrast / 10;
            }
            ColorsBeforeFiltration[i][0]= red;
            ColorsBeforeFiltration[i][1]= green;
            ColorsBeforeFiltration[i][2]= blue;
        }

    }

    private void NegativePositive() {

        for (int i = 0; i < total_pixels; i++) {
            int red = ColorsBeforeFiltration[i][0];
            int green = ColorsBeforeFiltration[i][1];
            int blue = ColorsBeforeFiltration[i][2];

            red = negative - negativeCoefficient * red;
            green = negative - negativeCoefficient * green;
            blue = negative - negativeCoefficient * blue;

            ColorsBeforeFiltration[i][0]= red;
            ColorsBeforeFiltration[i][1]= green;
            ColorsBeforeFiltration[i][2]= blue;
        }
    }

    private int sum(int[][] filter) {
        int sum = 0;
        for (int[] ints : filter) {
            for (int anInt : ints) {
                sum += anInt;
            }
        }
        return sum;
    }

    private static Image image() {
        FileChooser fileChooser = new FileChooser();
        Stage primaryStage = new Stage();
        setExtFilters(fileChooser);
        File path = fileChooser.showOpenDialog(primaryStage);
        image = new Image(path.toURI().toString());
        System.out.println(path);

        return image;
    }

    private static void setExtFilters(FileChooser chooser){
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Edycja Obrazu");

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

//        final FileChooser fileChooser = new FileChooser();
        Button fileButton = new Button("Wybierz plik");
        GridPane.setConstraints(fileButton, 0, 1);
        fileButton.setOnAction(e->{
            ImageView imageView = new ImageView();
            imageView.setImage(image());
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            pixelReader();
            GridPane.setConstraints(imageView, 0, 0);
            grid.getChildren().add(imageView);
        });


        Button brighterButton = new Button("Jasniej/Ciemniej");
        GridPane.setConstraints(brighterButton, 0, 2);
        brighterButton.setOnAction(e-> {
            brightness = Integer.parseInt(brighter.getText());
            Brightness();
            AddImg();
        });

        Button contrastButton = new Button("Contrast");
        GridPane.setConstraints(contrastButton, 1, 2);
        contrastButton.setOnAction(e-> {
            contrast = Integer.parseInt(darker.getText());
            Contrast();
            AddImg();
        });

        //blue = negative - negativeCoefficient*blue;
        Button negativeButton = new Button("Negatyw/Pozytyw");
        GridPane.setConstraints(negativeButton, 0, 4);
        negativeButton.setOnAction(e-> {
            NegativePositive();
            if(negativeCoefficient == -1){
                negative = 225;
                negativeCoefficient = 1;
            }
            else if (negativeCoefficient == 1){
                negative = 255- negative;
                negativeCoefficient = -1;
            }
            AddImg();
        });

        Button filterAvg3x3 = new Button("Filtr Srednia 3x3");
        GridPane.setConstraints(filterAvg3x3, 1, 4);
        filterAvg3x3.setOnAction(e-> {

                filter = mask3x3;
                maskSize = 1;
                maskFiltration();
                AddImg();
        });

        Button filterAvg5x5 = new Button("Filtr Srednia 9x9");
        GridPane.setConstraints(filterAvg5x5, 2, 4);
        filterAvg5x5.setOnAction(e-> {

                filter = mask5x5;
                maskSize = 2;
            maskFiltration();
                AddImg();
        });

        brighter = new TextField("0");
        GridPane.setConstraints(brighter, 0, 3);

        darker = new TextField("10");
        GridPane.setConstraints(darker, 1, 3);


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
        Button filterButton = new Button("Filtruj Obraz");
        GridPane.setConstraints(filterButton, 0, 5);


        filterButton.setOnAction(e-> {

            filter = new int[][] {
                    {Integer.parseInt(Input1.getText()), Integer.parseInt(Input2.getText()), Integer.parseInt(Input3.getText())},
                    {Integer.parseInt(Input4.getText()), Integer.parseInt(Input5.getText()), Integer.parseInt(Input6.getText())},
                    {Integer.parseInt(Input7.getText()), Integer.parseInt(Input8.getText()), Integer.parseInt(Input9.getText())}
            };
                maskSize = 1;
            maskFiltration();
                AddImg();
        });

        grid.getChildren().addAll(fileButton,brighterButton,brighter,contrastButton, darker,negativeButton,filterAvg3x3,
                filterAvg5x5,filterButton,Input1,Input2,Input3,Input4,Input5,Input6,Input7,Input8,Input9);
        Scene scene = new Scene(grid, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void AddImg() {
        ImageView imageView2 = new ImageView();
        imageView2.setImage(getImageFromData());
        imageView2.setFitWidth(400);
        imageView2.setPreserveRatio(true);
        imageView2.setSmooth(true);
        imageView2.setCache(true);
        GridPane.setConstraints(imageView2, 1, 0);
        grid.getChildren().add(imageView2);
    }

}
