package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.Sentence;
import model.WordCount;
import model.mapreduce.MapData;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MapActor extends UntypedActor {

    private static final List<String> STOP_WORDS = Arrays.asList(
        "a",
        "am",
        "an",
        "and",
        "are",
        "as",
        "at",
        "be",
        "do",
        "go",
        "if",
        "in",
        "is",
        "it",
        "of",
        "on",
        "the",
        "to"
    );

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static final Props mkProps() {
        return Props.create(MapActor.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Sentence) {
            getSender().tell(evaluateSentence((Sentence) message), getSelf());
        } else {
            unhandled(message);
        }
    }

    private MapData evaluateSentence(Sentence sentence) {
        log.debug("Going to map '" + sentence.getSentence() + "'");

        MapData<WordCount> mapData = new MapData<>();
        for (String word : sentence.getWordsInSentence()) {
            if (!STOP_WORDS.contains(word)) {
                mapData.map(new WordCount(word, 1));
            }
        }

        return mapData;
    }
}
