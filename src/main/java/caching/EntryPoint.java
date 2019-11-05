package caching;

import caching.Title;
import caching.cache.Cache;
import caching.cache.LFUCache;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.rdd.CassandraTableScanJavaRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

public class EntryPoint {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("Demo")
                .setMaster("local")
                .set("spark.cassandra.connection.host", "127.0.0.1");

        JavaSparkContext sc = new JavaSparkContext(conf);

        // Create cache with max size of 3 RDD's for demo purposes.
        Cache cache = new LFUCache(3);

        // Connect to Cassandra instance, storing movie details
        CassandraTableScanJavaRDD<Title> rdd =
                CassandraJavaUtil.javaFunctions(sc).cassandraTable(
                        "demo",
                        "movies",
                        mapRowTo(Title.class)
                );

        rdd.setName("Table Scan RDD");

        // Cache table scan RDD
        cache.cache(rdd);

        System.out.println(cache.toString());

        // Create a Map RDD and cache it.
        JavaRDD mappedRDD = rdd.map(title -> title.getTitle()).setName("Mapped RDD");
        cache.cache(mappedRDD);

        /* Cache table scan RDD twice again to simulate two accesses
        * this increments it's frequency in the LFU cache.*/
        cache.cache(rdd);
        cache.cache(rdd);

        System.out.println(cache.toString());

        // Create and cache filter RDD.
        JavaRDD filterRDD = mappedRDD.filter(title -> title.equals("Star Wars")).setName("Filter RDD");
        cache.cache(filterRDD);

        // CACHE IS NOW FULL (3 RDDs)

        System.out.println(cache.toString());

        // Cache this RDD, will result in the LFU policy being invoked.
        JavaRDD secondFilterRDD = filterRDD.map(title -> title + " Movie").setName("Filter RDD 2");
        cache.cache(secondFilterRDD);


        System.out.println(cache.toString());
        /*
        Above line will print this:
            CURRENT CACHE STATUS:
            [
                Frequency: 1, RDD: Filter RDD,
                Frequency: 3, RDD: Table Scan RDD,
                Frequency: 1, RDD: Filter RDD 2,
            ]
         */

        rdd.foreach(Object::toString);

        sc.stop();
    }

}
