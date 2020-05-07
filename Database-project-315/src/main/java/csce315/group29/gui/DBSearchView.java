package csce315.group29.gui;

import csce315.group29.DBApplication;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;


public class DBSearchView extends GridPane {

    static final double TEXT_FIELD_WIDTH = 140.0;
    static final double OPTION_FIELD_WIDTH = 110.0;
    static final double LABEL_FIELD_WIDTH = 130.0;
    Button search = new Button("Search");

    public DBSearchView() {
        getColumnConstraints().addAll(new ColumnConstraints(LABEL_FIELD_WIDTH), new ColumnConstraints(TEXT_FIELD_WIDTH), new ColumnConstraints(OPTION_FIELD_WIDTH));
        setPadding(new Insets(25, 25, 25, 25));
        setHgap(10);
        setVgap(10);
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 10; j++) {
                add(new Text(""), i, j);
            }
        }

        Button filter = new Button("Filter");
        Button degrees_of_separation = new Button("Degrees of Separation");
        Button year_span = new Button("Year Span");
        Button common_coworkers = new Button("Common Coworkers");
        Button rec_maker = new Button("Make Recommendations");

        filter.setOnMousePressed(event -> DBApplication.primaryScene.setRoot(DBApplication.filter));
        degrees_of_separation.setOnMousePressed(event -> DBApplication.primaryScene.setRoot(DBApplication.dos));

        year_span.setOnMousePressed(event -> DBApplication.primaryScene.setRoot(DBApplication.yearspan));
        rec_maker.setOnMousePressed(event -> DBApplication.primaryScene.setRoot(DBApplication.recView));

        common_coworkers.setOnMousePressed(event -> DBApplication.primaryScene.setRoot(DBApplication.coworkerView));


        add(filter, 4, 1);
        add(degrees_of_separation, 4, 2);
        add(year_span, 4, 3);
        add(common_coworkers, 4, 4);
        add(rec_maker, 4,5);
        add(search,2,10);

    }
}
