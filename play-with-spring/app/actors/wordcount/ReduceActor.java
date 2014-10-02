package actors.wordcount;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.WordCount;
import model.mapreduce.MapData;
import model.mapreduce.ReduceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import services.GreetingService;

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
            Integer existingCount = reducedData.get(wordCount.getWord());
            if (existingCount == null) {
                existingCount = Integer.valueOf(0);
            }

            reducedData.put(wordCount.getWord(), existingCount + wordCount.getCount());
        }

        return new ReduceData<String, Integer>(reducedData);
    }
}
