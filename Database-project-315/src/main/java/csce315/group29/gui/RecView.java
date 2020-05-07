package csce315.group29.gui;

import csce315.group29.RecommendationClass;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RecView extends DBSearchView{

    private static final double INT_FIELD_WIDTH = 60.0;

    public RecView() {
        super();
        Label year_span_label = new Label("Recommendations Maker");
        year_span_label.setStyle("-fx-font-weight: bold;");
        add(year_span_label, 0, 0);
        Label start_year_label = new Label("Year");
        add(start_year_label, 0, 1);
        Label end_year_label = new Label("Genre");
        add(end_year_label, 0, 2);
        TextField year_field = new TextField();
        year_field.setMinWidth(INT_FIELD_WIDTH);
        year_field.setMaxWidth(INT_FIELD_WIDTH);

        add(year_field, 1, 1);

        ComboBox<genres> genre_box = new ComboBox<>();
        genre_box.getItems().setAll(genres.values());

        genre_box.setMinWidth(160);
//        end_year_field.setMaxWidth(INT_FIELD_WIDTH);
        add(genre_box, 1, 2);

        Label fav_actor_label = new Label("Favorite Actor");
        add(fav_actor_label, 0, 3);
        TextField fav_actor_field = new TextField();
        add(fav_actor_field, 1, 3);

        search.setOnMouseClicked(event -> {
            if (year_field.getCharacters().toString().isEmpty()){
                System.out.println("ERROR YEAR MUST BE SPECIFIED");
                return;
            }
            if (genre_box.getValue().toString().isEmpty()){
                System.out.println("ERROR GENRE MUST BE SPECIFIED");
                return;
            }
            if (fav_actor_field.getCharacters().toString().isEmpty()){
                System.out.println("ERROR FAV ACTOR MUST BE SPECIFIED");
                return;
            }
            int year = Integer.parseInt(year_field.getCharacters().toString());
            String genre = genre_box.getValue().toString().replace('_','-');
            String fav_actor = fav_actor_field.getCharacters().toString();
            RecommendationClass recommendationClass = new RecommendationClass();
            recommendationClass.makeRecc(fav_actor,genre,year);
        });
    }

}
