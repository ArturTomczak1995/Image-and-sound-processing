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
    private static int[][] ColorsBeforeFiltration;

    private int[][] getMask(int width, int height) {
        int[][] mask = new int[width][height];
        for (int i = 0; i < height; i++) {
            Arrays.fill(mask[i], 1);
        }
        return mask;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static Image image() {
        File file = new CommonMethods().file("All Images", "*.*");
        image = new Image(file.toURI().toString());
        System.out.println(file);
        return image;
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

    private void pixelReader() {
        PixelReader pixelReader = image.getPixelReader();
        int total_pixels = (int) (image.getHeight() * image.getWidth());
        System.out.println();
        System.out.println("Image Height: " + image.getHeight() + " Image Width: "
                + image.getWidth() + " Pixel Format: " + pixelReader.getPixelFormat());
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
        int total_pixels = (int) (image.getHeight() * image.getWidth());
        for (int i = 0; i < total_pixels; i++) {
            int Red = ColorsBeforeFiltration[i][0];
            int Green = ColorsBeforeFiltration[i][1];
            int Blue = ColorsBeforeFiltration[i][2];

            for (int j = 0; j < ColorsBeforeFiltration[i].length; j++) {
                int color;
//                switch (j) {
//                    case (j = 0) {
//                        color =
//                    }
//                }
                if (ColorsBeforeFiltration[i][j] > 255 ) {
                    ColorsBeforeFiltration[i][j] = 255;
                } else if (ColorsBeforeFiltration[i][j] < 0) {
                    ColorsBeforeFiltration[i][j] = 0;
                }
            }



            pw.setArgb(i % (int)image.getWidth(), i/(int)image.getHeight(), (0xFF << 24) | Red << 16 | Green << 8 | Blue);
        }

        return wr;
    }


    private void maskFiltration(int maskSize, int[][] filter){
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        int total_pixels = (int) (image.getHeight() * image.getWidth());
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

    private void Brightness(int brightness) {
        int total_pixels = (int) (image.getHeight() * image.getWidth());
        for (int i = 0; i < total_pixels; i++) {
            int red = ColorsBeforeFiltration[i][0] + brightness;
            int green = ColorsBeforeFiltration[i][1] + brightness;
            int blue = ColorsBeforeFiltration[i][2] + brightness;


            ColorsBeforeFiltration[i][0]= red;
            ColorsBeforeFiltration[i][1]= green;
            ColorsBeforeFiltration[i][2]= blue;
        }
    }

    private void Contrast(int contrast) {
        int total_pixels = (int) (image.getHeight() * image.getWidth());
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

    private void NegativePositive(int negative, int negativeCoefficient) {
        int total_pixels = (int) (image.getHeight() * image.getWidth());
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

    private void AddImg(GridPane grid) {
        ImageView imageView2 = new ImageView();
        imageView2.setImage(getImageFromData());
        imageView2.setFitWidth(400);
        imageView2.setPreserveRatio(true);
        imageView2.setSmooth(true);
        imageView2.setCache(true);
        GridPane.setConstraints(imageView2, 1, 0);
        grid.getChildren().add(imageView2);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();


        primaryStage.setTitle("Edycja Obrazu");

        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

//        final FileChooser fileChooser = new FileChooser();
        Button fileButton = new Button("New file");
        GridPane.setConstraints(fileButton, 0, 1);
        fileButton.setOnAction(e->{
            Image image = image();
            ImageView imageView = imageView(image);
            GridPane.setConstraints(imageView, 0, 0);
            grid.getChildren().add(imageView);
            pixelReader();
        });


        TextField brighter = new TextField("0");
        GridPane.setConstraints(brighter, 0, 3);

        Button brighterButton = new Button("Jasniej/Ciemniej");
        GridPane.setConstraints(brighterButton, 0, 2);
        brighterButton.setOnAction(e-> {
            int brightness = Integer.parseInt(brighter.getText());
            Brightness(brightness);
            AddImg(grid);
        });

        TextField darker = new TextField("10");
        GridPane.setConstraints(darker, 1, 3);

        Button contrastButton = new Button("Contrast");
        GridPane.setConstraints(contrastButton, 1, 2);
        contrastButton.setOnAction(e-> {
            int contrast = Integer.parseInt(darker.getText());
            Contrast(contrast);
            AddImg(grid);
        });

        //blue = negative - negativeCoefficient*blue;
        Button negativeButton = new Button("Negative");
        GridPane.setConstraints(negativeButton, 0, 4);
        negativeButton.setOnAction(e-> {
            int negative = 255;
            int negativeCoefficient = 1;
            NegativePositive(negative, negativeCoefficient);
            AddImg(grid);
        });

        Button filterAvg3x3 = new Button("Filtr Srednia 3x3");
        GridPane.setConstraints(filterAvg3x3, 1, 4);
        filterAvg3x3.setOnAction(e-> {
            int[][] filter = getMask(3,3);
            int maskSize = 1;
            maskFiltration(maskSize, filter);
            AddImg(grid);
        });

        Button filterAvg5x5 = new Button("Filtr Srednia 9x9");
        GridPane.setConstraints(filterAvg5x5, 2, 4);
        filterAvg5x5.setOnAction(e-> {

            int[][] filter = getMask(5,5);
            int maskSize = 2;
            maskFiltration(maskSize, filter);
            AddImg(grid);
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
        Button filterButton = new Button("Filtruj Obraz");
        GridPane.setConstraints(filterButton, 0, 5);


        filterButton.setOnAction(e-> {

            int[][] filter = new int[][] {
                    {Integer.parseInt(Input1.getText()), Integer.parseInt(Input2.getText()), Integer.parseInt(Input3.getText())},
                    {Integer.parseInt(Input4.getText()), Integer.parseInt(Input5.getText()), Integer.parseInt(Input6.getText())},
                    {Integer.parseInt(Input7.getText()), Integer.parseInt(Input8.getText()), Integer.parseInt(Input9.getText())}
            };
            int maskSize = 1;
            maskFiltration(maskSize, filter);
                AddImg(grid);
        });

        grid.getChildren().addAll(fileButton,brighterButton,brighter,contrastButton, darker,negativeButton,filterAvg3x3,
                filterAvg5x5,filterButton,Input1,Input2,Input3,Input4,Input5,Input6,Input7,Input8,Input9);
        Scene scene = new Scene(grid, 850, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
