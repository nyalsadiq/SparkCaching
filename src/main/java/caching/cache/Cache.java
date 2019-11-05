package caching.cache;

import org.apache.spark.api.java.JavaRDD;

public interface Cache {
    void cache(JavaRDD item);
    String toString();
}
