package controllers;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import play.*;
import play.mvc.*;

import play.mvc.Controller;
import services.GreetingService;
import views.html.*;

import javax.inject.Inject;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    private final ActorSystem actorSystem;
    private final GreetingService greetingService;

    @Inject
    public Application(GreetingService greetingService, ActorSystem actorSystem) {
        this.greetingService = greetingService;
        this.actorSystem = actorSystem;
    }

    public Result index() {
        return ok(greeting.render(greetingService.hello()));
    }
}
