package actors.wordcount;

import akka.actor.*;
import akka.japi.Function;
import akka.routing.RoundRobinRouter;
import model.Result;
import model.Sentence;
import model.mapreduce.MapData;
import model.mapreduce.ReduceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import scala.concurrent.duration.Duration;
import spring.SpringExtension;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.resume;

/**
 *
 */
@Named("WordCountActor")
@org.springframework.context.annotation.Scope("prototype")
public class WordCountActor extends UntypedActor {

    private ActorRef mapActor;
    private ActorRef reduceActor;
    private ActorRef aggregateActor;
    private ActorRef fileActor;

    @Inject
    public WordCountActor(@Qualifier("FileActorProps") Props fileProps,
                          @Qualifier("MapActorProps") Props mapProps,
                          @Qualifier("ReduceActorProps") Props reduceProps,
                          @Qualifier("AggregatorActorProps") Props aggregatorProps) {

        fileActor = getContext().actorOf(
                fileProps.withRouter(new RoundRobinRouter(5)),
                "FileActor"
        );

        mapActor = getContext().actorOf(
                mapProps.withRouter(new RoundRobinRouter(5)),
                "MapActor"
        );

        reduceActor = getContext().actorOf(
                reduceProps.withRouter(new RoundRobinRouter(5)),
                "ReduceActor"
        );

        aggregateActor = getContext().actorOf(
                aggregatorProps.withRouter(new RoundRobinRouter(5)),
                "AggregatorActor"
        );

    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"),
                    new Function<Throwable, SupervisorStrategy.Directive>() {
                        @Override
                        public SupervisorStrategy.Directive apply(Throwable t) {
                            if (t instanceof FileNotFoundException) {
                                return resume();
                            } else {
                                return escalate();
                            }
                        }
                    });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Sentence) {
            mapActor.tell(message, getSelf());

        } else if (message instanceof MapData) {
            reduceActor.tell(message, getSelf());

        } else if (message instanceof ReduceData) {
            aggregateActor.tell(message, getSelf());

        } else if (message instanceof File) {
            fileActor.tell(message, getSelf());

        } else if (message instanceof Result) {
            // Forward the message from one actor to another. This means that the original
            // sender address/reference is maintained even though the message is going through
            // a 'mediator'. This can be useful when writing actors that work as routers, load-
            // balancers, replicators, etc. You need to pass along your context variable as well.
            aggregateActor.forward(message, getContext());

        } else {
            unhandled(message);
        }
    }
}
