package csce315.group29;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ActorGraph {

    static int nodesOpened = 0;

    public static Comparator<ImmutablePair<String, String>> compareNodes = (a, b) -> {
        try {
            return getNeighborsCount(a.left).compareTo(getNeighborsCount(b.left));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    };


    String start;
    String goal;
    String ignore;

    public ActorGraph(String start, String goal, String ignore) {
        this.start = start;
        this.goal = goal;
        this.ignore = ignore;
    }

    public List<String> solve() {
        nodesOpened = 0;
        for (int depth = 0; depth < 10; depth++) {
            System.out.println(depth);
            try {
                Set<String> visited = new HashSet<>();
                Stack<ImmutablePair<String, List<String>>> frontier = new Stack<>();
                frontier.add(new ImmutablePair<>(start, List.of(start)));

                while (!frontier.isEmpty()) {
                    ImmutablePair<String, List<String>> node = frontier.pop();
                    if (node.left.equals(goal)) {
                        System.out.println(nodesOpened);
                        return node.right.stream().map(Utils::getNameByGenericId).collect(Collectors.toList());
                    }
                    if ((node.right.size() / 2) - 1 > depth || node.left.equals(ignore)) {
                        continue;
                    }
                    if (!visited.contains(node.left)) {
                        for (ImmutablePair<String, String> neighbor : getNeighbors(node.left)) {
                            if (!visited.contains(neighbor.left)) {
                                List<String> newPath = new ArrayList<>(node.right);
                                newPath.addAll(List.of(neighbor.right, neighbor.left));
                                frontier.push(new ImmutablePair<>(neighbor.left, newPath));
                            }
                        }
                        visited.add(node.left);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return List.of();
    }

    static public Integer getNeighborsCount(String id) throws SQLException {
        String query = "SELECT count from separation_graph_heuristic where nconst = ?;";
        if (QueryCache.actorNeighborCountCache.containsKey(id)) {
            return QueryCache.actorNeighborCountCache.get(id);
        }
        PreparedStatement statement = DBApplication.conn.prepareStatement(query);
        statement.setString(1, id);
        ResultSet results = statement.executeQuery();

        results.next();
        int count = results.getInt(1);
        QueryCache.actorNeighborCountCache.put(id, count);
        return count;
    }

    public Set<ImmutablePair<String, String>> getNeighbors(String id) throws SQLException {
        String query = "SELECT n2,tconst from separation_graph where n1 = ?;";
        nodesOpened++;
        if (QueryCache.actorGraphCache.containsKey(id)) {
            return QueryCache.actorGraphCache.get(id);
        }
        PreparedStatement statement = DBApplication.conn.prepareStatement(query);
        statement.setString(1, id);
        long startTime = System.currentTimeMillis();
        ResultSet results = statement.executeQuery();
        Set<ImmutablePair<String, String>> neighbors = new TreeSet<>(compareNodes);
        while (results.next()) {
            neighbors.add(new ImmutablePair<>(results.getString(1), results.getString(2)));
        }
        long endTime = System.currentTimeMillis();
        System.out.println(statement.toString() + " took " + (endTime - startTime) + " milliseconds");
        QueryCache.actorGraphCache.put(id, neighbors);
        return neighbors;
    }


}
