package csce315.group29.gui;

import csce315.group29.Utils;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CommonCoworkerView extends DBSearchView {

    public CommonCoworkerView() {
        super();
        Label newcast = new Label("Find the most common co-worker");
        newcast.setMinWidth(400);

        add(newcast, 0, 1);
        TextField newcastTextField = new TextField();
        newcastTextField.setMinWidth(200);
        add(newcastTextField, 0, 2);
        search.setOnMouseClicked(event -> Utils.generateCommonCoworkers(newcastTextField.getCharacters().toString()));
    }
}
