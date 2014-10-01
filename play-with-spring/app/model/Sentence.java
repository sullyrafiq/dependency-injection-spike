package model;

/**
 * Created with IntelliJ IDEA.
 * User: sully.rafiq
 * Date: 27/01/14
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */
public class Sentence {

    private final String sentence;

    public Sentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public String[] getWordsInSentence() {
        String[] words = sentence.split(" ");
        for(int i=0; i<words.length; i++) {
            words[i] = words[i].toLowerCase();
        }
        return words;
    }

    @Override
    public String toString() {
        return "Sentence{" +
                "sentence='" + sentence + '\'' +
                '}';
    }
}
