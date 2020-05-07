package csce315.group29.gui;

import csce315.group29.DBApplication;
import csce315.group29.TableData;
import csce315.group29.Utils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class FilterView extends DBSearchView {


    public FilterView() {
        super();


        Text scenetitle1 = new Text("Input");
        add(scenetitle1, 1, 0, 1, 1);

        Text scenetitle2 = new Text("Options");
        add(scenetitle2, 2, 0, 1, 1);

        /*--------------------Title----------------------*/
        Label title = new Label("Title");
        title.setMinWidth(LABEL_FIELD_WIDTH);
        add(title, 0, 1);
        TextField titleTextField = new TextField();
        titleTextField.setMinWidth(TEXT_FIELD_WIDTH);
        add(titleTextField, 1, 1);
        final ComboBox<String> TitleBox = new ComboBox<>();
        TitleBox.setMinWidth(OPTION_FIELD_WIDTH);
        TitleBox.getItems().addAll(
                "contains",
                "matches"
        );
        add(TitleBox, 2, 1);

        try {
            String url = "jdbc:postgresql://10.254.152.35:5432/postgres";
            Properties props = new Properties();
            props.setProperty("user", "service");
            props.setProperty("password", "studentpwd");
            props.setProperty("ssl", "false");
            DBApplication.conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TableData.populate();
        /*--------------------Cast----------------------*/
        Label cast = new Label("Cast");
        cast.setMinWidth(LABEL_FIELD_WIDTH);

        add(cast, 0, 2);
        TextField castTextField = new TextField();
        castTextField.setMinWidth(TEXT_FIELD_WIDTH);
        add(castTextField, 1, 2);
        final ComboBox<String> CastBox = new ComboBox<>();
        CastBox.setMinWidth(OPTION_FIELD_WIDTH);

        CastBox.getItems().addAll(
                "is in",
                "is not in"
        );
        add(CastBox, 2, 2);

        /*--------------------Release Year----------------------*/
        Label year = new Label("Release Year");
        year.setMinWidth(LABEL_FIELD_WIDTH);

        add(year, 0, 3);
        TextField yearTextField = new TextField();
        yearTextField.setMinWidth(TEXT_FIELD_WIDTH);
        add(yearTextField, 1, 3);
        final ComboBox<String> YearBox = new ComboBox<>();
        YearBox.setMinWidth(OPTION_FIELD_WIDTH);

        YearBox.getItems().addAll(
                "<",
                "=",
                ">"
        );
        add(YearBox, 2, 3);

        /*--------------------Run Time----------------------*/
        Label runTime = new Label("Run Time");
        runTime.setMinWidth(LABEL_FIELD_WIDTH);
        add(runTime, 0, 4);
        TextField runTimeTextField = new TextField();
        runTimeTextField.setMinWidth(TEXT_FIELD_WIDTH);

        add(runTimeTextField, 1, 4);
        final ComboBox<String> TimeBox = new ComboBox<>();
        TimeBox.setMinWidth(OPTION_FIELD_WIDTH);

        TimeBox.getItems().addAll(
                "<",
                "=",
                ">"
        );
        add(TimeBox, 2, 4);

        /*--------------------Film Type----------------------*/
        final ComboBox<filmtype> TypeBox = new ComboBox<>();
        TypeBox.getItems().addAll(filmtype.values());
        Label filmTypeLabel = new Label("Film Type");
        filmTypeLabel.setMinWidth(LABEL_FIELD_WIDTH);
        add(filmTypeLabel, 0, 5);
        add(TypeBox, 1, 5);
        final ComboBox<String> TypeOptionBox = new ComboBox<>();
        TypeOptionBox.setMinWidth(OPTION_FIELD_WIDTH);

        TypeOptionBox.getItems().addAll(
                "is",
                "is not"
        );
        add(TypeOptionBox, 2, 5);

        /*--------------------Genre----------------------*/
        final ComboBox<genres> GenreBox = new ComboBox<>();
        GenreBox.getItems().addAll(genres.values());
        Label genreLabel = new Label("Genre");
        genreLabel.setMinWidth(LABEL_FIELD_WIDTH);
        add(genreLabel, 0, 6);
        add(GenreBox, 1, 6);
        final ComboBox<String> GenreOptionBox = new ComboBox<>();
        GenreOptionBox.setMinWidth(OPTION_FIELD_WIDTH);

        GenreOptionBox.getItems().addAll(
                "is",
                "is not"
        );
        add(GenreOptionBox, 2, 6);

        /*--------------------Adult Check----------------------*/
        Label isAdultLabel = new Label("Adult?");
        isAdultLabel.setMinWidth(LABEL_FIELD_WIDTH);
        add(isAdultLabel, 0, 7);
        CheckBox adultCB = new CheckBox();
        adultCB.setIndeterminate(false);
        add(adultCB, 1, 7);

        Label limitLabel = new Label("Limit Results:");
        limitLabel.setMinWidth(LABEL_FIELD_WIDTH);
        add(limitLabel, 0, 8);
        TextField limitTextField = new TextField();
        limitTextField.setMinWidth(TEXT_FIELD_WIDTH);
        add(limitTextField, 1, 8);


        CheckBox saveCheckBox = new CheckBox();
        saveCheckBox.setIndeterminate(false);
        add(saveCheckBox, 1, 9);
        Label saveLabel = new Label("Save result to file?");
        saveLabel.setMinWidth(LABEL_FIELD_WIDTH);
        add(saveLabel, 0, 9);

        search.setOnMousePressed(event -> {
            String titleSelection = titleTextField.getCharacters().toString();
            String castSelection = castTextField.getCharacters().toString();
            int releaseYearSelection = -1;
            if (!yearTextField.getCharacters().toString().isEmpty()) {
                releaseYearSelection = Integer.parseInt(yearTextField.getCharacters().toString());
            }

            int runtimeSelection = -1;
            if (!runTimeTextField.getCharacters().toString().isEmpty()) {
                runtimeSelection = Integer.parseInt(runTimeTextField.getCharacters().toString());
            }
            String genreSelection = "";
            if (GenreBox.getValue() != null) {
                genreSelection = (GenreBox.getValue()).toString().replace('_', '-');
            }
            String filmtypeSelection = "";
            if (TypeBox.getValue() != null) {
                filmtypeSelection = (TypeBox.getValue()).toString().replace("shortfilm", "short");

            }
            boolean isAdultSelection = adultCB.isSelected();
            int limitSelection = -1;
            if (!limitTextField.getCharacters().toString().isEmpty()) {
                limitSelection = Integer.parseInt(limitTextField.getCharacters().toString());

            }

            Utils.generateFilteredQueryResults(
                    titleSelection,
                    castSelection,
                    releaseYearSelection,
                    runtimeSelection,
                    filmtypeSelection,
                    genreSelection,
                    isAdultSelection,
                    limitSelection,
                    TitleBox.getValue(),
                    CastBox.getValue(),
                    YearBox.getValue(),
                    TimeBox.getValue(),
                    TypeOptionBox.getValue(),
                    GenreOptionBox.getValue(),
                    saveCheckBox.isSelected()
            );

        });


    }
}

enum filmtype {
    shortfilm,
    tvSeries,
    videoGame,
    tvSpecial,
    tvShort,
    tvMovie,
    tvEpisode,
    video,
    movie,
    tvMiniSeries
}

