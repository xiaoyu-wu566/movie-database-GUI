package csce315.group29;

import csce315.group29.models.SQLTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableData {

    static HashMap<String, SQLTable> tables = new HashMap<>();

    public static void populate() {
        String listTableQuery = "SELECT * FROM information_schema.tables WHERE table_schema = 'public';";
        try {
            Statement st = DBApplication.conn.createStatement();
            ResultSet rs = st.executeQuery(listTableQuery);
            List<String> names = new ArrayList<>();
            while (rs.next()) {
                String tableName = rs.getString(3);
                names.add(tableName);

            }
            st.close();
            rs.close();

            for(String tableName: names) {
                SQLTable table = new SQLTable(tableName);
                String listColumnQuery = "SELECT * FROM %s limit 1;";

                Statement st2 = DBApplication.conn.createStatement();
                ResultSet rs2 = st2.executeQuery(String.format(listColumnQuery, tableName));
                for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
                    table.addCol(rs2.getMetaData().getColumnName(i));
                }
                tables.put(tableName, table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
