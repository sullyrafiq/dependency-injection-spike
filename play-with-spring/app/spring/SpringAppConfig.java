package spring;

import actors.wordcount.AggregatorActor;
import actors.wordcount.FileActor;
import actors.wordcount.MapActor;
import actors.wordcount.ReduceActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import play.libs.Akka;

/**
 *
 */
@Configuration
@ComponentScan({"controllers", "services", "actors"})
public class SpringAppConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = Akka.system();
        // initialize the application context in the Akka Spring Extension
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);
        return system;
    }

    @Bean(name = "MapActorProps")
    public Props mapActor() {
        return MapActor.mkProps();
    }

    @Bean(name = "ReduceActorProps")
    public Props reduceActor() {
        return ReduceActor.mkProps();
    }

    @Bean(name = "FileActorProps")
    public Props fileActor() {
        return FileActor.mkProps();
    }

}
