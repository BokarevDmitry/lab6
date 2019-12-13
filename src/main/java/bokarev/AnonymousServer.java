package bokarev;

import akka.actor.ActorRef;
import akka.http.impl.engine.client.PoolConductor;
import akka.pattern.Patterns;
import akka.routing.RouterActor;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import akka.http.javadsl.server.Route;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class AnonymousServer {
    private static String URL = "url";
    private static String COUNT = "count";
    static AsyncHttpClient asyncHttpClient;
    static ActorRef storageActor;
    static ZooKeeper zooKeeper;

    public AnonymousServer(ActorRef storageActor, AsyncHttpClient asyncHttpClient, ZooKeeper zooKeeper) {
        this.asyncHttpClient = asyncHttpClient;
        this.storageActor = storageActor;
        this.zooKeeper = zooKeeper;
    }

    public static Route createRoute() {
        return route (
                get (() -> parameter(URL, url ->
                            parameter(COUNT, count ->
                            )))
        );
    }

    public static Route handleGetRequest (String url, Integer count) {
        CompletionStage<Response> res;
        if (count == 0) {
            res = fetch(asyncHttpClient.prepareGet(url).build());
        } else {
            res = Redirect(url, count--);
        }
        return completeOKWithFutureString(res.thenApply(Response::getResponseBody));
    }

    public static  CompletionStage<Response> Redirect(String url, Integer count) {
        return Patterns.ask(storageActor, new GetMessage(), Duration.ofMillis(3000))
                .thenApply(o -> ((ReturnMessage)o))
    }

    public static CompletionStage<Response> fetch(Request req) {
        return asyncHttpClient.executeRequest(req).toCompletableFuture();
    }

    private static Response BadDirection (Response res, Throwable ex, String z) {
        if (ex instanceof ConnectException) {
            storageActor.tell(new DeleteMessage(), ActorRef.noSender());
        }
        return  res;
    }

    private static String getServerUrl(String node) throws KeeperException, InterruptedException {
        return new String(zooKeeper.getData(node, false, null));
    }

    public static Request createServerRequest(String serverUrl, String url, Integer count) {
        return asyncHttpClient.prepareGet(serverUrl).addQueryParam(URL, url)
                .addQueryParam(COUNT, Integer.toString(count)).build();
    }




}
