package caching;


import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.*;

public class FIFOCache implements Serializable, Cache {

    public FIFOCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        cache = new ArrayList<>();
        contents = new HashSet<>();
    }

    private int maxCacheSize;
    private List<JavaRDD> cache;
    private Set<JavaRDD> contents;

    public void cache(JavaRDD item) {
        if (contents.contains(item)) {
            System.out.println(String.format("ALREADY CACHED %s", item.toString()));
            return;
        }

        if (cache.size() >= maxCacheSize) {
            System.out.println("CACHE FULL");
            JavaRDD lru = cache.remove(0);
            lru.unpersist(false);
            contents.remove(lru);
            System.out.println(String.format("REMOVED %s", lru.toString()));
        }


        item.cache();
        cache.add(item);
        contents.add(item);

        System.out.println(String.format("INSERTED %s", item.toString()));
        System.out.println(String.format("CACHE SIZE %s", cache.size()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("CURRENT CACHE STATUS: \n");
        sb.append("[\n");
        for (JavaRDD item : cache) {
            sb.append(item.toString());
            sb.append(",\n");
        }
        sb.append("]");

        return sb.toString();
    }
}
