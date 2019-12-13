package bokarev;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.*;

public class StorageActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    List<String> storage = new ArrayList<>();
    Random random = new Random();

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
                .match(PutMessage.class, m -> {
                        log.info("RECEIVED SERVERS: " + m.servers.toString());
                        this.storage.clear();
                        this.storage.addAll(m.getServers());
                })
                .match(GetMessage.class, m -> {
                    log.info("GETMESSAGE REQUEST");
                    System.out.println("storage-size = " + storage.size());
                    getSender().tell(new ReturnMessage(storage.get(random.nextInt(storage.size()))),
                            ActorRef.noSender());

                })
                .match(DeleteMessage.class, m -> {
                    log.info("DELETE SERVERS: " + m.getServer());
                    this.storage.remove(m.getServer());
                })
                .build();
    }
}