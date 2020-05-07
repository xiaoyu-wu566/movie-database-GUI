package csce315.group29;


import csce315.group29.gui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBApplication extends Application {

    public static Connection conn;

    public static ExecutorService executor
            = Executors.newCachedThreadPool();

    public static Scene primaryScene;
    public static FilterView filter = new FilterView();
    public static DegreeView dos = new DegreeView();
    public static YearSpanView yearspan = new YearSpanView();
    public static RecView recView = new RecView();
    public static CommonCoworkerView coworkerView = new CommonCoworkerView();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        conn.setAutoCommit(false);

        QueryCache.populateActorNeighborCountCache();
        QueryCache.populateActorGraphCacheFromFile();

        primaryStage.setTitle("DBApplication");
        primaryStage.setMinWidth(1080 * .6);
        primaryStage.setMinHeight(720 * .6);

        GridPane view = filter;
        primaryStage.setTitle("DBUI");


        Scene scene = new Scene(view, 300, 275);
        primaryScene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop() {
        executor.shutdown();
        QueryCache.saveActorGraphCache();
        QueryCache.saveActorNeighborCache();
    }
}
