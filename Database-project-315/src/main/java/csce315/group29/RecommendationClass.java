package csce315.group29;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecommendationClass {
    private void getKnownForDirectors(String nconst, String genre, int year){
        String seed_string = "SELECT primarytitle\n" +
                "FROM movies \n" +
                "INNER JOIN title_principals ON movies.tconst = title_principals.tconst\n" +
                "WHERE title_principals.nconst IN \n" +
                "\t(SELECT nconst \n" +
                "\tFROM title_principals\n" +
                "\tWHERE tconst  IN \n" +
                "\t\t(SELECT UNNEST(knownfortitles) \n" +
                "\t\tFROM people\n" +
                "\t\tWHERE nconst = ?\n" +
                "\t\t)\n" +
                "\tAND category LIKE '%director%'\n" +
                "\t)\n" +
                "AND movies.tconst NOT IN \n" +
                "\t(SELECT tconst \n" +
                "\tFROM title_principals\n" +
                "\tWHERE tconst  IN \n" +
                "\t\t(SELECT UNNEST(knownfortitles) \n" +
                "\t\tFROM people\n" +
                "\t\tWHERE nconst = ?\n" +
                "\t\t)\n" +
                "\tAND category LIKE '%director%'\n" +
                "\t)\n" +
                "AND ?=ANY(movies.genre)\n" +
                "ORDER BY ABS(startyear - ?) ASC\n" +
                "Limit 5;";
        try{
            PreparedStatement stmt = DBApplication.conn.prepareStatement(seed_string);
            stmt.setString(1,nconst);
            stmt.setString(2,nconst);
            stmt.setString(3,genre);
            stmt.setInt(4,year);
            ResultSet resultSet = stmt.executeQuery();
            ArrayList<String> reccMovies= new ArrayList<>();
            while(resultSet.next()){
                reccMovies.add(resultSet.getString("primarytitle"));
            }
            Utils.drawResultsTable(reccMovies);
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
    public void makeRecc(String name, String genre, int year){
        String pid = Utils.getPersonID(name);
        getKnownForDirectors(pid,genre,year);
    }
}
