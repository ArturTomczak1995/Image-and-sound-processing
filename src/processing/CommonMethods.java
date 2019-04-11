package processing;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

import static javafx.scene.layout.GridPane.setConstraints;

public class CommonMethods {

    public File file(String description, String extensions) {
        FileChooser fileChooser = new FileChooser();
        Stage primaryStage = new Stage();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(description, extensions));
        return fileChooser.showOpenDialog(primaryStage);
    }

    public static Button setButton(String text, int columnIndex, int rowIndex) {
        Button button = new Button(text);
        GridPane.setConstraints(button, columnIndex, rowIndex);
        return button;
    }

    public static TextField setTextField(String text, int columnIndex, int rowIndex) {
        TextField textField = new TextField(text);
        GridPane.setConstraints(textField, columnIndex, rowIndex);
        return textField;
    }

    public static Label setLabel(String text, int columnIndex, int rowIndex){
        Label label = new Label(text);
        setConstraints(label, columnIndex, rowIndex);
        return label;
    }
}


