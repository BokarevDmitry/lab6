package bokarev;

import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Handler {
    static final Logger logger = Logger.getLogger(Handler.class.getName());
    static ZooKeeper zooKeeper;
    static String path;
    ActorRef storageActor;

    Handler(ZooKeeper zooKeeper, ActorRef storageActor, String path) {
        this.zooKeeper = zooKeeper;
        this.storageActor = storageActor;
        this.path = path;

    }

    public void checkChildren (WatchedEvent e) {
        try {
            this.storageActor.tell(new PutMessage(zooKeeper.getChildren(path, this::checkChildren).stream()
            .map(s-> path + "/" + s)
            .collect(Collectors.toList())), ActorRef.noSender()
            );
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
