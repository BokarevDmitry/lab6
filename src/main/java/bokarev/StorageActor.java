package bokarev;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.*;

public class StorageActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Map<String, Long> testResults = new HashMap<>();

    public StorageActor() {
    }

    public static Props props() {
        return Props.create(StorageActor.class);
    }

    @Override
    public void preStart() {
        log.info("Starting StorageActor {}", this);
    }

    @Override
    public void postStop() {
        log.info("Stopping StorageActor {}", this);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestWithResult.class, test -> {
                        log.info("REQUEST: store test results of  - " + test.getUrl());
                        testResults.put(test.getUrl().getUrl(), test.getResult());
                })

                .match(UrlWithCount.class, r -> {
                    log.info("REQUEST: tests for package - " + r.getUrl() + " " + r.getCount());
                   // log.info(Integer.toString(testResults.size()));
                    System.out.println(testResults.size());
                    Long l = testResults.get(r.getUrl());
                    TestWithResult t = new TestWithResult(r, l);
                    getSender().tell(t, ActorRef.noSender());
                })

                .build();
    }
}