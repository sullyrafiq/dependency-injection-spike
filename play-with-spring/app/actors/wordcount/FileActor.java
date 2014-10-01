package actors.wordcount;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import model.Sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 */
public class FileActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static final Props mkProps() {
        return Props.create(FileActor.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof File) {
            File file = getFileIfItExists(message);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                getSender().tell(new Sentence(line), getSelf());
            }

        } else {
            unhandled(message);
        }
    }

    private File getFileIfItExists(Object message) throws FileNotFoundException {
        File file = (File) message;
        if (!file.exists()) {
            throw new FileNotFoundException("Unable to find " + file.getName());
        }
        return file;
    }
}
