package model.mapreduce;

import model.WordCount;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sully.rafiq
 * Date: 27/01/14
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
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
