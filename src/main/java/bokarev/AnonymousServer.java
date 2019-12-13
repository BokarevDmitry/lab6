package bokarev;

import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

public class AnonymousServer {
    private static String URL = "url";
    private static String COUNT = "count";
    AsyncHttpClient asyncHttpClient;
    ActorRef storageActor;
    ZooKeeper zooKeeper;

    public AnonymousServer(ActorRef storageActor, AsyncHttpClient asyncHttpClient, ZooKeeper zooKeeper) {
        this.asyncHttpClient = asyncHttpClient;
        this.storageActor = storageActor;
        this.zooKeeper = zooKeeper;
    }

    public static Route c


}
