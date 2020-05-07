package csce315.group29;

import com.google.flatbuffers.FlatBufferBuilder;
import csce315.group29.models.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueryCache {

    public static Map<String, Set<ImmutablePair<String, String>>> actorGraphCache = new ConcurrentHashMap<>();
    public static Map<String, Integer> actorNeighborCountCache = new ConcurrentHashMap<>();

    // key name, value nconst
    public static Map<String, String> nameNconstCache = new HashMap<>();
    // key nconst, value name
    public static Map<String, String> nconstNameCache = new HashMap<>();


    public static void populateActorNeighborCountCache() throws SQLException {
        if (new File("ActorNeighborCache.bin").exists()) {
            populateActorNeighborCountFromFile();
        } else {
            Statement st = DBApplication.conn.createStatement();
            String query = "SELECT * FROM separation_graph_heuristic as s where count > 100 ORDER BY count ;";
            st.setFetchSize(50);
            long startTime = System.nanoTime();

            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                String key = rs.getString(1);
                Integer val = rs.getInt(2);
                actorNeighborCountCache.put(key, val);
            }
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1000000000d;
            System.out.println("Populated Actor Neighbor Count Cache");
            System.out.println("It took " + duration + "s");
        }
    }

    public static void populateActorGraphCache(String min, String max) throws SQLException {
        Statement st = DBApplication.conn.createStatement();
        String query = String.format("select nconst,n2,tconst,count from (select * from separation_graph_heuristic order by count desc) as s2 inner join separation_graph as s1 on s1.n1 = s2.nconst where count > %s and count < %s;", min, max);
        st.setFetchSize(50);
        long startTime = System.nanoTime();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            String key = rs.getString(1);
            String nconst = rs.getString(2);
            String tconst = rs.getString(3);
            actorGraphCache.computeIfAbsent(key, k -> new HashSet<>());
            actorGraphCache.get(key).add(new ImmutablePair<>(nconst, tconst));


        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000000d;
        System.out.println(String.format("Populated Actor Graph Cache for %s < count < %s", min, max));
        System.out.println("It took " + duration + "s");
    }

    static void populateActorGraphCacheFromFile() {
        try {
            long startTime = System.nanoTime();
            RandomAccessFile actorGraphCacheFile = new RandomAccessFile(new File("ActorGraphCache.bin"), "r");
            byte[] actorGraphData = new byte[(int) actorGraphCacheFile.length()];
            actorGraphCacheFile.readFully(actorGraphData);
            actorGraphCacheFile.close();

            ByteBuffer actorGraphByteBuffer = ByteBuffer.wrap(actorGraphData);
            ActorGraphCache actorGraphCache = ActorGraphCache.getRootAsActorGraphCache(actorGraphByteBuffer);
            for (int i = 0; i < actorGraphCache.dataLength(); i++) {
                Actor actor = actorGraphCache.data(i);
                String key = actor.nconst();
                QueryCache.actorGraphCache.computeIfAbsent(key, k -> new TreeSet<>(ActorGraph.compareNodes));
                for (int j = 0; j < actor.connectionsLength(); j++) {
                    ActorConnection connection = actor.connections(j);
                    QueryCache.actorGraphCache.get(key).add(new ImmutablePair<>(connection.nconst(), connection.tconst()));
                }
            }
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1000000000d;
            System.out.println("Populated Actor Graph Cache from file");
            System.out.println("It took " + duration + "s");
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void populateActorNeighborCountFromFile() {
        long startTime = System.nanoTime();
        try {
            RandomAccessFile actorNeighborCacheFile = new RandomAccessFile(new File("ActorNeighborCache.bin"), "r");
            byte[] actorNeighborData = new byte[(int) actorNeighborCacheFile.length()];
            actorNeighborCacheFile.readFully(actorNeighborData);
            actorNeighborCacheFile.close();

            ByteBuffer actorNeighborByteBuffer = ByteBuffer.wrap(actorNeighborData);
            ActorNeighborCountCache actorNeighborCache = ActorNeighborCountCache.getRootAsActorNeighborCountCache(actorNeighborByteBuffer);
            for (int i = 0; i < actorNeighborCache.dataLength(); i++) {
                ActorNeighborCount actorNeighborCount = actorNeighborCache.data(i);
                QueryCache.actorNeighborCountCache.put(actorNeighborCount.nconst(), actorNeighborCount.count());
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1000000000d;
        System.out.println("Populated Actor Neighbor Count Cache from file");
        System.out.println("It took " + duration + "s");
    }

    static void saveActorGraphCache() {
        try {
            FileOutputStream actorGraphCacheFile = new FileOutputStream(new File("ActorGraphCache.bin"));
            FlatBufferBuilder fbb = new FlatBufferBuilder(1024);
            List<ImmutablePair<Integer, Integer>> actorConnData = new ArrayList<>();
            List<String> keySet = new ArrayList<>(QueryCache.actorGraphCache.keySet());
            List<Integer> keyOffsets = new ArrayList<>();
            List<Integer> actorOffsets = new ArrayList<>();
            List<Integer> actorConnOffsets = new ArrayList<>();
            List<Integer> actorConnVectorOffsets = new ArrayList<>();

            for (String set : keySet) {
                keyOffsets.add(fbb.createString(set));
                for (ImmutablePair<String, String> conn : actorGraphCache.get(set)) {
                    actorConnData.add(new ImmutablePair<>(fbb.createString(conn.left), fbb.createString(conn.right)));
                }
                for (ImmutablePair<Integer, Integer> p : actorConnData) {
                    actorConnOffsets.add(ActorConnection.createActorConnection(fbb, p.left, p.right));
                }
                actorConnVectorOffsets.add(Actor.createConnectionsVector(fbb, actorConnOffsets.stream().filter(Objects::nonNull).mapToInt(t -> t).toArray()));
                actorConnOffsets.clear();
                actorConnData.clear();
            }
            assert keyOffsets.size() == actorConnVectorOffsets.size();
            for (int i = 0; i < keyOffsets.size(); i++) {
                actorOffsets.add(Actor.createActor(fbb, keyOffsets.get(i), actorConnVectorOffsets.get(i)));
            }

            int dataOffset = ActorGraphCache.createDataVector(fbb, actorOffsets.stream().filter(Objects::nonNull).mapToInt(t -> t).toArray());
            int cacheOffset = ActorGraphCache.createActorGraphCache(fbb, dataOffset);
            fbb.finish(cacheOffset);
            fbb.sizedInputStream().transferTo(actorGraphCacheFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveActorNeighborCache() {
        try {
            FileOutputStream actorNeighborCacheFile = new FileOutputStream(new File("ActorNeighborCache.bin"));
            FlatBufferBuilder fbb = new FlatBufferBuilder(1024);
            List<Integer> actorNeighborCountList = new ArrayList<>();
            for (String key : QueryCache.actorNeighborCountCache.keySet()) {
                int keyOffset = fbb.createString(key);
                int count = actorNeighborCountCache.get(key);
                actorNeighborCountList.add(ActorNeighborCount.createActorNeighborCount(fbb, keyOffset, count));
            }
            int dataOffset = ActorNeighborCountCache.createDataVector(fbb, actorNeighborCountList.stream().filter(Objects::nonNull).mapToInt(t -> t).toArray());
            int cacheOffset = ActorNeighborCountCache.createActorNeighborCountCache(fbb, dataOffset);
            fbb.finish(cacheOffset);
            fbb.sizedInputStream().transferTo(actorNeighborCacheFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
