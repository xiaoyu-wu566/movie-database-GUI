package csce315.group29.gui;

import csce315.group29.Utils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class YearSpanView extends DBSearchView {
    private static final double INT_FIELD_WIDTH = 60.0;

    public YearSpanView() {
        super();
        Label year_span_label = new Label("Year Span");
        year_span_label.setStyle("-fx-font-weight: bold;");
        add(year_span_label, 0, 0);
        Label start_year_label = new Label("Start Year");
        add(start_year_label, 0, 1);
        Label end_year_label = new Label("End Year");
        add(end_year_label, 0, 2);
        TextField start_year_field = new TextField();
        start_year_field.setMinWidth(INT_FIELD_WIDTH);
        start_year_field.setMaxWidth(INT_FIELD_WIDTH);

        add(start_year_field, 1, 1);

        TextField end_year_field = new TextField();
        end_year_field.setMinWidth(INT_FIELD_WIDTH);
        end_year_field.setMaxWidth(INT_FIELD_WIDTH);
        add(end_year_field, 1, 2);
        Label exclude_actor_label = new Label("Exclude");
        add(exclude_actor_label, 0, 3);
        TextField exclude_actor_field = new TextField();
        add(exclude_actor_field, 1, 3);

        Label search_type_label = new Label("Search Type");
        add(search_type_label, 0, 4);

        final ComboBox<String> searchTypeBox = new ComboBox<>();
        searchTypeBox.setMinWidth(OPTION_FIELD_WIDTH);

        searchTypeBox.getItems().addAll(
                "Actor",
                "Director"
        );
        searchTypeBox.getSelectionModel().selectFirst();
        add(searchTypeBox, 1, 4);

        search.setOnMouseClicked(event -> {
            if (start_year_field.getCharacters().toString().isEmpty() || end_year_field.getCharacters().toString().isEmpty()) {
                System.out.println("ERROR: years for year span not specified");
                return;
            }

            int start_year = Integer.parseInt(start_year_field.getCharacters().toString());
            int end_year = Integer.parseInt(end_year_field.getCharacters().toString());
            String exclude_actor = exclude_actor_field.getCharacters().toString();
            boolean directorSearch = false;
            if (searchTypeBox.getValue().equals("Director")) {
                directorSearch = true;
            }

            Utils.getYearSpan(start_year, end_year, exclude_actor, directorSearch);


        });
    }

}
