package processing.image;


import javafx.scene.image.*;
import java.util.Arrays;

class Edition {
    private Image image;
    private static int[][] arrRGB;


    void setImage(Image image) {
        this.image = image;
        pixelToColors();
    }

    void average(int maskSize, int width, int height) {
        int[][] mask = new int[width][height];
        for (int i = 0; i < height; i++) {
            Arrays.fill(mask[i], 1);
        }
        maskFiltration(maskSize, mask);
    }

    void brightness(int brightness) {
        changeColorsValues(brightness, '+');
    }

    void contrast(int contrast) {
        for (int i = 0; i < imagePixelsAmount(image); i++) {
            for (int j = 0; j < arrRGB[i].length; j++) {
                if (arrRGB[i][j] > 125) {
                    arrRGB[i][j] *= contrast / 10.0D;
                }
            }
        }
    }

    void negativePositive(int negative, int negativeCoefficient) {
        changeColorsValues(negative - negativeCoefficient, '*');
    }

    void maskFiltration(int maskSize, int[][] mask) {
        int imageHeight = imageHeight(image);
        int imageWidth = imageWidth(image);
        for (int i = 0; i < imagePixelsAmount(image); i++) {
            int imgCoordX = i % imageWidth;
            int imgCoordY = i / imageWidth;
            if (imgCoordX >= maskSize && imgCoordY >= maskSize) {
                if (imgCoordX < imageWidth - maskSize && imgCoordY < imageHeight - maskSize) {
                    int[] pixelRGB = filterColorsWithMask(arrRGB[i], maskSize, mask, imgCoordX, imgCoordY);
                    arrRGB[i] = doArithmetic(pixelRGB, sumArrayValues(mask), '/');
                }
            }
        }
    }

    Image getImage() {
        int imageHeight = imageHeight(image);
        int imageWidth = imageWidth(image);
        WritableImage wr = new WritableImage(imageWidth, imageHeight);
        PixelWriter pw = wr.getPixelWriter();
        int Red = 0, Green = 0, Blue = 0;

        for (int i = 0; i < imagePixelsAmount(image); i++) {
            for (int j = 0; j < arrRGB[i].length; j++) {
                int color = changeColorValueIfOutOfBound(arrRGB[i][j]);
                if (j == 0) Red = color;
                else if (j == 1) Green = color;
                else Blue = color;
            }
            pw.setArgb(i % imageWidth, i / imageHeight, (0xFF << 24) | Red << 16 | Green << 8 | Blue);
        }
        return wr;
    }

    private static int imageHeight(Image image) {
        return (int) image.getHeight();
    }

    private static int imageWidth(Image image) {
        return (int) image.getWidth();
    }

    private static int imagePixelsAmount(Image image) {
        return imageHeight(image) * imageWidth(image);
    }

    private void pixelToColors() {
        int pixelNumber = 0;
        int width = imageWidth(image);
        arrRGB = new int[imagePixelsAmount(image)][3];
        for (int coordinateY = 0; coordinateY < imageHeight(image); coordinateY++) {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                pixelsToRedGreenBlue(pixelNumber, coordinateX, coordinateY);
                pixelNumber++;
            }
        }
    }

    private void pixelsToRedGreenBlue(int pixelNumber, int coordinateX, int coordinateY) {
        int color = image.getPixelReader().getArgb(coordinateX, coordinateY);

        arrRGB[pixelNumber][0] = (color >> 16) & 0xff;
        arrRGB[pixelNumber][1] = (color >> 8) & 0xff;
        arrRGB[pixelNumber][2] = color & 0xff;
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

    private int getPixelColorValue(int maskSize, int[][] filter, int maskCoordX, int maskCoordY, int imgCoordX, int imgCoordY) {
        int filterCoordValue = filter[maskSize + maskCoordY][maskSize + maskCoordX];
        int pixelColorValue = ((image.getPixelReader().getArgb(imgCoordX + maskCoordX, imgCoordY + maskCoordY)));
        return filterCoordValue * pixelColorValue;
    }

    private int[] filterColorsWithMask(int[] pixelRGB, int maskSize, int[][] filter, int imgCoordX, int imgCoordY) {
        for (int maskCoordY = -maskSize; maskCoordY <= maskSize; maskCoordY++) {
            for (int maskCoordX = -maskSize; maskCoordX <= maskSize; maskCoordX++) {
                pixelRGB[0] += (getPixelColorValue(maskSize, filter, maskCoordX, maskCoordY, imgCoordX, imgCoordY) >> 16) & 0xff;
                pixelRGB[1] += (getPixelColorValue(maskSize, filter, maskCoordX, maskCoordY, imgCoordX, imgCoordY) >> 8) & 0xff;
                pixelRGB[2] += getPixelColorValue(maskSize, filter, maskCoordX, maskCoordY, imgCoordX, imgCoordY) & 0xff;
            }
        }
        return pixelRGB;
    }

    private int[] doArithmetic(int[] value1, int value2, char operation) {
        int[] arr = new int[value1.length];
        for (int j = 0; j < arr.length; j++) {
            arr[j] = Common.arithmeticOperation(value1[j], value2, operation);
        }
        return arr;
    }

    private void changeColorsValues(int value, char operation) {
        for (int i = 0; i < imagePixelsAmount(image); i++) {
            arrRGB[i] = doArithmetic(arrRGB[i], value, operation);
        }
    }

    private int changeColorValueIfOutOfBound(int color) {
        if (color > 255) color = 255;
        else if (color < 0) color = 0;
        return color;
    }
}
