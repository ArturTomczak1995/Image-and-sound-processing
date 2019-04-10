package processing.image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import processing.CommonMethods;

import java.io.File;

class Provider {

    Image getImage() {
        File file = new CommonMethods().file("All Images", "*.*");
        Image image = new Image(file.toURI().toString());
        System.out.println(file);
        return image;
    }

    ImageView imageView(Image image) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }

    void showChangedImg(GridPane grid, Image image) {
        ImageView imageView = imageView(image);
        GridPane.setConstraints(imageView, 1, 0);
        grid.getChildren().add(imageView);
    }
}
