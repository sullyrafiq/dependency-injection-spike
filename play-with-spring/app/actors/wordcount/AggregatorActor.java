package actors.wordcount;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.Result;
import model.mapreduce.ReduceData;
import org.springframework.context.annotation.Scope;
import services.PersistentWordCountStorage;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Named("AggregatorActor")
@Scope("prototype")
public class AggregatorActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final PersistentWordCountStorage persistentWordCountStorage;

    public static final Props mkProps() {
        return Props.create(AggregatorActor.class);
    }

    @Inject
    public AggregatorActor(PersistentWordCountStorage persistentWordCountStorage) {
        this.persistentWordCountStorage = persistentWordCountStorage;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReduceData) {
            aggregateInMemoryReduce((ReduceData) message);

        } else if (message instanceof Result) {
            getSender().tell(persistentWordCountStorage.getWordCount().toString(), getSelf());

        } else {
            unhandled(message);
        }
    }

    private void aggregateInMemoryReduce(ReduceData reduceData) {
        Map<String, Integer> reducedData = reduceData.getData();
        log.debug("Aggregating the reduced data " + reduceData.getData());

        for (String word : reducedData.keySet()) {
            persistentWordCountStorage.addWordCount(word, reducedData.get(word));
        }
    }
}
