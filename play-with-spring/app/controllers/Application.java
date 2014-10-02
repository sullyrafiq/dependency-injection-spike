package controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import model.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
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

    public Result index() {
        return ok(greeting.render(greetingService.wordCountInstruction()));
    }

    public F.Promise<Result> results() {
        return F.Promise.wrap(Patterns.ask(wordCountActor, new model.Result(), 1000)).map(
                new F.Function<Object, Result>() {
                    @Override
                    public Result apply(Object result) throws Throwable {
                        return ok(results.render((String) result));
                    }
                }
        );
    }

    public Result addSentence() {
        DynamicForm requestData = Form.form().bindFromRequest();
        wordCountActor.tell(new Sentence(requestData.get("sentence")), ActorRef.noSender());
        return redirect("/");
    }

}
