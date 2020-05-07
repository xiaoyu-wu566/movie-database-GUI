package csce315.group29;


import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.CheckComboBox;

public class LabelledControl extends GridPane {

    Control second;

    LabelledControl(String labelString, Control second) {
        this.second = second;
        Label label = new Label(labelString);
        second.setMaxWidth(Double.MAX_VALUE);
        setConstraints(label, 0, 0);
        setConstraints(second, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setPercentWidth(20);
        col2.setPercentWidth(80);
        getColumnConstraints().addAll(col1, col2);
        getChildren().addAll(label, second);
    }

    public String getText() {
        if (second instanceof CheckComboBox) {
            return String.join(",", ((CheckComboBox) second).getCheckModel().getCheckedItems());
        }
        if (second instanceof ComboBox) {
            return (String) ((ComboBox) second).getSelectionModel().getSelectedItem();
        }
        return "";
    }
}
