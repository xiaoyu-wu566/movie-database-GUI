package csce315.group29.models;

import java.util.ArrayList;
import java.util.List;

public class SQLTable {
    private String name;


    private List<String> cols;

    public SQLTable(String name) {
        this.name = name;
        cols = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCol(int index) {
        return cols.get(index);
    }

    public void addCol(String name) {
        cols.add(name);
    }

    public List<String> getList() {
        return cols;
    }

    public int getColumnLength() {
        return cols.size();
    }
}
