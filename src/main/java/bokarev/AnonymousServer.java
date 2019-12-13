package bokarev;

import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

public class AnonymousServer {
    private static String URL = "url";
    private static String COUNT = "count";
    static AsyncHttpClient asyncHttpClient;
    static ActorRef storageActor;
    static ZooKeeper zooKeeper;

    public AnonymousServer()
}
