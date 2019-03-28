import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

class CommonMethods {

    File file(String description, String extensions) {
        FileChooser fileChooser = new FileChooser();
        Stage primaryStage = new Stage();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(description, extensions));
        return fileChooser.showOpenDialog(primaryStage);
    }
}


