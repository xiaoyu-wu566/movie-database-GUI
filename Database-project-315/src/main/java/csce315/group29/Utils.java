package csce315.group29;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import csce315.group29.models.ActorMoviePair;
import csce315.group29.models.MovieYearPair;
import csce315.group29.models.ReturnObj;
import csce315.group29.models.YearActorPair;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static void generateFilteredQueryResults(String title, String cast,
                                                    int releaseYear, int runtime,
                                                    String movietype, String genre,
                                                    boolean isAdult, int limit,
                                                    Object titleOptions, Object castOptions,
                                                    Object yearOptions, Object timeOptions,
                                                    Object typeOptions, Object genreOptions, boolean saveToFile) {

        // need to pad this with a true condition (1=1) to get my AND pattern to work
        String query = "SELECT primaryTitle from movies where (1=1) ";

        if (titleOptions == null) titleOptions = "matches";
        if (yearOptions == null) yearOptions = "=";
        if (timeOptions == null) timeOptions = "=";
        if (typeOptions == null) typeOptions = "is";
        if (genreOptions == null) genreOptions = "is";

        if (!title.equals("")) {
            query += " AND ";
            query += "(primaryTitle " + (titleOptions.toString().equals("contains") ? "LIKE ?" : "= ?") + ")";
        }
        if (releaseYear != -1) {
            query += " AND ";
            query += "(startYear " + yearOptions.toString() + " ?)";
        }
        if (runtime != -1) {
            query += " AND ";
            query += "(runtimeminutes " + timeOptions.toString() + " ?)";
        }

        if (!movietype.equals("")) {
            query += " AND ";
            String conditional = typeOptions.toString().equals("is") ? "=" : "!=";
            query += "(titletype " + conditional + " ?)";
        }

        if (!genre.equals("")) {
            query += " AND ";
            String conditional = genreOptions.toString().equals("is") ? "=" : "!=";
            query += "(? " + conditional + " ANY(genres))";
        }
        query += " AND ";
        query += "(isadult = ?)";

        if (limit != -1) {
            query += " LIMIT ?";
        }
        query += ";";

        List<String> output = new ArrayList<>();
        System.out.println(query);
        try {
            int count = 1;
            PreparedStatement statement = DBApplication.conn.prepareStatement(query);
            if (!title.equals("")) statement.setString(count++, "%" + title + "%");
            if (releaseYear != -1) statement.setInt(count++, releaseYear);
            if (runtime != -1) statement.setInt(count++, runtime);
            if (!movietype.equals("")) statement.setString(count++, movietype);
            if (!genre.equals("")) statement.setString(count++, genre);

            statement.setInt(count++, isAdult ? 1 : 0);
            if (limit != -1) statement.setInt(count, limit);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                output.add(results.getString(1));
            }
            if (!saveToFile) {
                drawResultsTable(output);
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter("Save_Result_Data.csv"));
                for (String movie : output) {
                    //System.out.println(movie);
                    writer.append(movie).append(",\n");
                }
                writer.close();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    public static void drawResultsTable(List<String> data_list) {
        Stage resultWindow = new Stage();
        resultWindow.setTitle("Results");

        TableView<ReturnObj> resultTable = new TableView<>();
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<ReturnObj, String> dataCol = new TableColumn<>("");

        dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));
        resultTable.getColumns().add(dataCol);
        resultTable.setItems(FXCollections.observableArrayList(data_list.stream().map(ReturnObj::new).collect(Collectors.toList())));

        Scene scene = new Scene(resultTable);
        resultWindow.setScene(scene);
        resultWindow.show();

    }

    public static void generateCommonCoworkers(String nconst) {
        String query = "select d.primaryname as actor, count(*) as num_movie from movie_cast a, movie_cast b, people c, people d " +
                "where b.nconst = d.nconst " +
                "and a.tconst=b.tconst " +
                "and c.nconst=a.nconst " +
                "and c.primaryname = ? " +
                "and a.nconst < b.nconst " +
                "group by d.primaryname order by count(*) desc limit 1;";
        try {
            PreparedStatement statement = DBApplication.conn.prepareStatement(query);
            statement.setString(1, nconst);
            ResultSet results = statement.executeQuery();
            List<String> output = new ArrayList<>();
            while (results.next()) {
                output.add(results.getString("actor"));
                System.out.println(output);

            }
            drawResultsTable(output);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNameByGenericId(String id) {
        String query;
        if (id.substring(0, 2).equals("tt")) {
            query = "SELECT primaryTitle from movies where tconst = ?";
        } else {
            query = "SELECT primaryname from people where nconst = ?";
        }
        try {
            PreparedStatement statement = DBApplication.conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getActorsWithMostYears(int start_year, int end_year, ArrayList<Integer> excludeYears,
                                                 boolean directorSearch, String excludeActor) {
        StringBuilder seed_string;
        if (directorSearch) {
            seed_string = new StringBuilder("SELECT nconst from director_yearactive WHERE yearactive >= ? and yearactive <=? ");
        } else {
            seed_string = new StringBuilder("SELECT nconst from cast_yearactive WHERE yearactive >= ? and yearactive <=? ");
        }
        for (int year : excludeYears) {
            seed_string.append("AND yearactive != ?");
        }
        if (!excludeActor.isEmpty()) {
            seed_string.append("AND nconst != ?");
        }
        seed_string.append("GROUP BY nconst ORDER BY COUNT(yearactive) DESC LIMIT 1");
        //ArrayList<String> actors = new ArrayList<>();
        String actor = "";
        try {
            int count = 1;
            PreparedStatement stmt = DBApplication.conn.prepareStatement(seed_string.toString());
            stmt.setInt(count++, start_year);
            stmt.setInt(count++, end_year);
            for (int year : excludeYears) {
                stmt.setInt(count++, year);
            }
            if (!excludeActor.isEmpty()) {
                stmt.setString(count, excludeActor);
            }
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            actor = resultSet.getString("nconst");
//            while(resultSet.next()){
//                actors.add(resultSet.getString("nconst"));
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return actor;
    }

    private static ArrayList<MovieYearPair> getYearsWorked(String nconst, boolean directorSearch) {

        String seed_string;
        if (directorSearch) {
            seed_string = "SELECT yearactive, tconst from director_yearactive WHERE nconst = ?;";
        } else {
            seed_string = "SELECT yearactive, tconst from cast_yearactive WHERE nconst = ?;";
        }
        ArrayList<MovieYearPair> years_worked = new ArrayList<>();
        try {
            PreparedStatement stmt = DBApplication.conn.prepareStatement(seed_string);
            stmt.setString(1, nconst);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                MovieYearPair temp = new MovieYearPair();
                temp.year = results.getInt("yearactive");
                temp.movie = results.getString("tconst");
                years_worked.add(temp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return years_worked;
    }

    private static ArrayList<Integer> makeExcludeList(HashMap<Integer,ActorMoviePair> pairs, int startYear, int endYear) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = startYear; i <= endYear; i++) {
            if (pairs.get(i).actor != "N/A") {
                ret.add(i);
            }
        }
        return ret;
    }

    public static String getPersonID(String name) {
        String seedQuery = "select nconst from people where primaryname = ? limit 1";
        String id = "";
        try {
            PreparedStatement stmt = DBApplication.conn.prepareStatement(seedQuery);
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            id = resultSet.getString("nconst");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        System.out.println("EXCLUDED PERSON: " + id);
        return id;
    }

    public static String getTitle(String tconst){
        String seed_string = "SELECT primarytitle from movies where tconst = ?;";
        try {
            PreparedStatement stmt = DBApplication.conn.prepareStatement(seed_string);
            stmt.setString(1,tconst);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getString("primarytitle");
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return "";
    }

    public static void getYearSpan(int start_year, int end_year, String exclude, Boolean directorSearch) {
        String excludeID = "N/A";
        if (!exclude.isEmpty()) {
            excludeID = getPersonID(exclude);
        }
        ArrayList<YearActorPair> year_span = new ArrayList<>();
        HashMap<Integer, ActorMoviePair> year_span_map = new HashMap<>();


        for (int i = start_year; i <= end_year; i++) {
            ActorMoviePair t = new ActorMoviePair();
            t.actor="N/A";
            year_span_map.put(i, t);
        }

        ArrayList<Integer> excludeList = makeExcludeList(year_span_map, start_year, end_year);

        while (excludeList.size() <= end_year - start_year) {
            String newActor = getActorsWithMostYears(start_year, end_year, excludeList, directorSearch, excludeID);
            System.out.println("ACTOR: " + newActor);
            ArrayList<MovieYearPair> yearsWorked = getYearsWorked(newActor, directorSearch);

            for (MovieYearPair year : yearsWorked) {
                ActorMoviePair tempPair = new ActorMoviePair();
                tempPair.actor=newActor;
                tempPair.movie=year.movie;
                year_span_map.put(year.year, tempPair);
            }
            excludeList = makeExcludeList(year_span_map, start_year, end_year);
            System.out.println("---------------------------------------------");
            for (int year : excludeList) {
                System.out.println(year);
            }
        }

        ArrayList<String> displayList = new ArrayList<>();
        for (int i = start_year; i <= end_year; i++) {
            displayList.add("Year: " + i + "  |  " + (directorSearch ? "Director: " : "Actor:") + getNameByGenericId(year_span_map.get(i).actor) + " | Movie: " + getTitle(year_span_map.get(i).movie));
        }
        drawResultsTable(displayList);

    }


}
