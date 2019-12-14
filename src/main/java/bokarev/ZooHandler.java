package bokarev;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ZooHandler {
    static final Logger logger = Logger.getLogger(ZooHandler.class.getName());
    static ZooKeeper zooKeeper;
    static String path;
    ActorRef storageActor;

    ZooHandler(ZooKeeper zooKeeper, ActorRef storageActor, String path) {
        this.zooKeeper = zooKeeper;
        this.storageActor = storageActor;
        this.path = path;
        checkChildren(null);
    }

    public void checkChildren (WatchedEvent e) {
        try {
            this.storageActor.tell(new PutMessage(zooKeeper.getChildren(path, this::checkChildren).stream()
            .map(s-> path + "/" + s)
            .collect(Collectors.toList())), ActorRef.noSender()
            );
        } catch (KeeperException | InterruptedException err) {
            throw new RuntimeException(err);
        }
    }

    public void createServer (String name, String host, int port) throws Exception {
        String servPath = zooKeeper.create(
                path + "/" + name,
                (host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        logger.info("server path: " + servPath);
    }
}