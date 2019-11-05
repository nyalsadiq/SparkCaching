package caching.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RDDFrequency implements Serializable{

    private int frequency;
    private JavaRDD rdd;

}
