package caching.cache;


import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class LIFOCache implements Serializable, Cache {

    public LIFOCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        cache = new ArrayDeque<>();
        contents = new HashSet<>();
    }

    private int maxCacheSize;
    private Deque<JavaRDD> cache;
    private Set<JavaRDD> contents;

    public void cache(JavaRDD item) {
        if (contents.contains(item)) {
            System.out.println(String.format("ALREADY CACHED %s", item.toString()));
            return;
        }

        if (cache.size() >= maxCacheSize) {
            System.out.println("CACHE FULL");
            JavaRDD lru = cache.removeLast();
            lru.unpersist(false);
            contents.remove(lru);
            System.out.println(String.format("REMOVED %s", lru.toString()));
        }


        item.cache();
        cache.addLast(item);
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
