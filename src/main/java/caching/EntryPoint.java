package caching;

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

        Cache cache = new LFUCache(5);

        CassandraTableScanJavaRDD<Title> rdd =
                CassandraJavaUtil.javaFunctions(sc).cassandraTable(
                        "demo",
                        "movies",
                        mapRowTo(Title.class)
                );


        rdd.setName("Table Scan RDD");
        cache.cache(rdd); // Cache table scan

        JavaRDD filteredRDD = rdd.filter(title -> title.getTitle().equals("Star Wars"));
        cache.cache(filteredRDD); // Cache filtered RDD

        cache.cache(rdd); // Caching table scan again results in it's frequency stat being increased.

        rdd.foreach(Object::toString);

        sc.stop();
    }

}
