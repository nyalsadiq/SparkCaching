package caching;


import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class LFUCache implements Serializable, Cache {

    public LFUCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        this.cache = new PriorityQueue<>(Comparator.comparingInt(RDDFrequency::getFrequency));
        this.contents = new HashMap<>();
    }

    private int maxCacheSize;
    private PriorityQueue<RDDFrequency> cache;
    private Map<JavaRDD, RDDFrequency> contents;

    public void cache(JavaRDD item) {
        if (contents.containsKey(item)) {

            RDDFrequency frequency = contents.get(item);
            cache.remove(frequency);

            frequency.setFrequency(frequency.getFrequency() + 1);

            cache.add(frequency);

            System.out.println(String.format("ALREADY CACHED %s", item.toString()));

            return;
        }

        if (cache.size() >= maxCacheSize) {
            System.out.println("CACHE FULL");

            JavaRDD lru = cache.poll().getRdd();
            lru.unpersist(false);
            contents.remove(lru);

            System.out.println(String.format("REMOVED %s", lru.toString()));
        }


        item.cache();
        RDDFrequency newQueueItem = new RDDFrequency(1, item);
        cache.add(newQueueItem);
        contents.put(item, newQueueItem);

        System.out.println(String.format("INSERTED %s", item.toString()));
        System.out.println(String.format("CACHE SIZE %s", cache.size()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("CURRENT CACHE STATUS: \n");
        sb.append("[\n");
        for (RDDFrequency item : cache) {
            sb.append("Frequency: ");
            sb.append(item.getFrequency());
            sb.append(", RDD: ");
            sb.append(item.getRdd().name());
            sb.append(",\n");
        }
        sb.append("]");

        return sb.toString();
    }
}
