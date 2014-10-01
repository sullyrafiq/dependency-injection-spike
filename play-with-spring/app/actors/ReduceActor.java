package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.WordCount;
import model.mapreduce.MapData;
import model.mapreduce.ReduceData;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ReduceActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static final Props mkProps() {
        return Props.create(ReduceActor.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof MapData) {
            getSender().tell(reduce((MapData) message), getSelf());
        } else {
            unhandled(message);
        }
    }

    private ReduceData reduce(MapData mapData) {
        log.debug("Going to reduce " + mapData.getData());

        Map<String, Integer> reducedData = new HashMap<String, Integer>();
        for (Object obj : mapData.getData()) {
            WordCount wordCount = (WordCount) obj;
            reducedData.put(wordCount.getWord(), wordCount.getCount());
        }

        return new ReduceData<String, Integer>(reducedData);
    }
}
