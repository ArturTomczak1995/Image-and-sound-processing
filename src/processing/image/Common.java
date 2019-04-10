package processing.image;


import javafx.scene.image.Image;

class Common {

    static int getImageHeight(Image image) {
        return (int) image.getHeight();
    }

    static int getImageWidth(Image image) {
        return (int) image.getWidth();
    }

    static int getImagePixelsAmount(Image image) {
        return getImageHeight(image) * getImageWidth(image);
    }

    static int arithmeticOperation(int a, int b, char operator) {
        if (operator == '+') return a + b;
        else if (operator == '-') return a - b;
        else if (operator == '*') return a * b;
        else if (operator == '/') return a / b;
        else throw new java.lang.Error("No such operation");
    }
}
