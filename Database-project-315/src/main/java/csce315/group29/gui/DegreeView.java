package csce315.group29.gui;


import csce315.group29.ActorGraph;
import csce315.group29.DBApplication;
import csce315.group29.QueryCache;
import csce315.group29.Utils;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DegreeView extends DBSearchView {

    public DegreeView() {
        super();

        Label dos_label = new Label("Degrees of Separation");
        dos_label.setStyle("-fx-font-weight: bold;");
        add(dos_label, 0, 0,2,1);

        Label start = new Label("Start");
        Label goal = new Label("Goal");
        Label ignore = new Label("Ignore");
        TextField startField = new TextField();
        TextField goalField = new TextField();
        TextField ignoreField = new TextField();


        add(start, 0, 1);
        add(goal, 0, 2);
        add(ignore, 0, 3);
        add(startField, 1, 1);
        add(goalField, 1, 2);
        add(ignoreField, 1, 3);

        search.setOnMouseClicked(event -> {
            String startID = guessNconst(startField.getText());
            String goalID = guessNconst(goalField.getText());
            String ignoreID = guessNconst(ignoreField.getText());
            ActorGraph graph = new ActorGraph(startID, goalID, ignoreID);
            long startTime = System.currentTimeMillis();
            List<String> solution = graph.solve();
            long endTime = System.currentTimeMillis();
            System.out.println("That took " + (endTime - startTime) + " milliseconds");
            Utils.drawResultsTable(solution);
        });
    }


    private String guessNconst(String input) {
        if (input.equals("")) {
            return input;
        } else if(input.substring(0, 2).equals("nm")) {
            return input;
        } else {
            if (QueryCache.nameNconstCache.containsKey(input)) {
                return QueryCache.nameNconstCache.get(input);
            }
            String query = "SELECT nconst FROM people where primaryname = ?;";
            try {
                PreparedStatement statement = DBApplication.conn.prepareStatement(query);
                statement.setString(1, input);
                ResultSet results = statement.executeQuery();
                if (results.next()) {
                    String id = results.getString(1);
                    QueryCache.nameNconstCache.put(input, id);
                    return id;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

