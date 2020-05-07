package csce315.group29.nodes;

import csce315.group29.DBApplication;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutoCompleteComboBox extends ComboBox<String> {

    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
    String prev = "";

    public AutoCompleteComboBox() {
        setEditable(true);

        setOnKeyReleased(event -> {
            String content = getEditor().getText();
            if (!prev.equals(content)) {
                prev = content;
                this.getSuggestions(prev).whenComplete((suggestions, b) -> Platform.runLater(() -> getItems().setAll(suggestions)));
            }
            if (event.getCode() == KeyCode.DOWN) {
                if (!isShowing()) {
                    show();
                }
            }
        });
    }

    private CompletableFuture<List<String>> getSuggestions(String request) {
        String query = "SELECT primaryname FROM people where primaryname ILIKE ? limit 10;";
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        try {
            PreparedStatement statement = DBApplication.conn.prepareStatement(query);
            statement.setString(1, request + "%");
            System.out.println(statement);
            executor.submit(() -> {
                try {
                    ResultSet results = statement.executeQuery();
                    List<String> output = new ArrayList<>();
                    while (results.next()) {
                        output.add(results.getString(1));
                    }
                    future.complete(output);
                } catch (SQLException e) {
                    future.complete(List.of());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            future.complete(List.of());
        }
        return future;
    }
}
