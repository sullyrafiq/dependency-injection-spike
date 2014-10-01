package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.Result;
import model.mapreduce.ReduceData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sully.rafiq
 * Date: 27/01/14
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class AggregatorActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Map<String, Integer> finalReducedMap;

    public static final Props mkProps() {
        return Props.create(AggregatorActor.class);
    }

    @Override
    public void preStart() throws Exception {
        finalReducedMap = new HashMap<String, Integer>();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReduceData) {
            aggregateInMemoryReduce((ReduceData) message);

        } else if (message instanceof Result) {
            getSender().tell(finalReducedMap.toString(), getSelf());

        } else {
            unhandled(message);
        }
    }

    private void aggregateInMemoryReduce(ReduceData reduceData) {
        Map<String, Integer> data = reduceData.getData();
        log.debug("Aggregating the reduced data " + reduceData.getData());

        for (String word : data.keySet()) {
            Integer count = finalReducedMap.get(word);
            if (count == null) {
                count = Integer.valueOf(0);
            }

            count = count + data.get(word);
            finalReducedMap.put(word, count);
        }
    }
}
