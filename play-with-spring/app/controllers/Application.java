package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import model.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import play.*;
import play.libs.F;
import play.mvc.*;

import play.mvc.Controller;
import services.GreetingService;
import spring.SpringExtension;
import views.html.*;

import javax.inject.Inject;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    private final ActorSystem actorSystem;
    private final GreetingService greetingService;
    private final ActorRef wordCountActor;

    @Inject
    public Application(GreetingService greetingService, ActorSystem actorSystem) {
        this.greetingService = greetingService;
        this.actorSystem = actorSystem;

        wordCountActor = actorSystem.actorOf(
                SpringExtension.SpringExtProvider.get(actorSystem).props("WordCountActor"), "WordCountActor");
    }

    public F.Promise<Result> index() {
        wordCountActor.tell(new Sentence("The quick brown fox tried to jump over the lazy dog and fell on the dog"), ActorRef.noSender());
        wordCountActor.tell(new Sentence("Dog is man's best friend"), ActorRef.noSender());
        wordCountActor.tell(new Sentence("Dog and Fox belong to the same family"), ActorRef.noSender());

        return F.Promise.wrap(Patterns.ask(wordCountActor, new model.Result(), 1000)).map(
            new F.Function<Object, Result>() {
                @Override
                public Result apply(Object result) throws Throwable {
                    return ok(greeting.render(greetingService.hello(), (String) result));
                }
            }
        );
    }
}
