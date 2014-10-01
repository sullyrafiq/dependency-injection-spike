package model;

/**
 * Created with IntelliJ IDEA.
 * User: sully.rafiq
 * Date: 27/01/14
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class WordCount {

    private final String word;
    private final Integer count;

    public WordCount(String word, Integer count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "{" + "word='" + word + "', count=" + count + "}";
    }
}
