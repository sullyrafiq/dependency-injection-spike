package model.mapreduce;

import model.WordCount;

import java.util.Map;

/**
 *
 */
public class ReduceData<K, V> {

    private final Map<K, V> reducedData;

    public ReduceData(Map<K, V> reducedData) {
        this.reducedData = reducedData;
    }

    public Map<K, V> getData() {
        return reducedData;
    }
}
