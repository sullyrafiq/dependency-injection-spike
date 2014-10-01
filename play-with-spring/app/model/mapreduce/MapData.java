package model.mapreduce;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MapData<T> {

    private final List<T> wordCountList;

    public MapData() {
        wordCountList = new ArrayList<T>();
    }

    public void map(T obj) {
        wordCountList.add(obj);
    }

    public List<T> getData() {
        return this.wordCountList;
    }
}
