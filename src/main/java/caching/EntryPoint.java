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

        Cache cache = new LFUCache(3);

        CassandraTableScanJavaRDD<Title> rdd =
                CassandraJavaUtil.javaFunctions(sc).cassandraTable(
                        "demo",
                        "movies",
                        mapRowTo(Title.class)
                );

        rdd.setName("Table Scan RDD");

        cache.cache(rdd); // Cache table scan

        System.out.println(cache.toString());

        JavaRDD mappedRDD = rdd.map(title -> title.getTitle()).setName("Mapped RDD");
        cache.cache(mappedRDD); // Cache filtered RDD

        cache.cache(rdd); // Caching table scan again results in it's frequency stat being increased.
        cache.cache(rdd);

        System.out.println(cache.toString());

        JavaRDD filterRDD = mappedRDD.filter(title -> title.equals("Star Wars")).setName("Filter RDD");
        cache.cache(filterRDD);

        System.out.println(cache.toString());

        JavaRDD secondFilterRDD = filterRDD.map(title -> title + " Movie").setName("Filter RDD 2");
        cache.cache(secondFilterRDD);

        System.out.println(cache.toString());

        rdd.foreach(Object::toString);

        System.out.println(cache.toString());

        sc.stop();
    }

}
