package actors;

import akka.actor.*;
import akka.japi.Function;
import akka.routing.RoundRobinRouter;
import model.Result;
import model.Sentence;
import model.mapreduce.MapData;
import model.mapreduce.ReduceData;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.resume;

/**
 * Created with IntelliJ IDEA.
 * User: sully.rafiq
 * Date: 28/01/14
 * Time: 09:20
 * To change this template use File | Settings | File Templates.
 */
public class MasterActor extends UntypedActor {

    private ActorRef mapActor;
    private ActorRef reduceActor;
    private ActorRef aggregateActor;
    private ActorRef fileActor;

    public static final Props mkProps() {
        return Props.create(MasterActor.class);
    }

    public MasterActor() {
        fileActor = getContext().actorOf(
                FileActor.mkProps().withRouter(new RoundRobinRouter(5)), "file");
        mapActor = getContext().actorOf(
                MapActor.mkProps().withRouter(new RoundRobinRouter(5)), "map");
        reduceActor = getContext().actorOf(
                ReduceActor.mkProps().withRouter(new RoundRobinRouter(5)), "reduce");
        aggregateActor = getContext().actorOf(
                AggregatorActor.mkProps());
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
