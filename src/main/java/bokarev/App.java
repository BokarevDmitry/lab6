package bokarev;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;

import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;




import static org.asynchttpclient.Dsl.asyncHttpClient;


public class App extends AllDirectives {
    static Logger log = Logger.getLogger(App.class.getName());
    private static String CONN = "127.0.0.1:2181";
    private static String STRINGPATH = "/servers";
    private static String LOCALHOST = "loacalhost";
    private static int TIMEOUT = 3000;

    public static void main(String[] args) throws Exception, InterruptedException, IOException {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        final ZooKeeper zoo = new ZooKeeper(CONN, TIMEOUT, err->log.info(err.toString()));
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AsyncHttpClient httpClient = asyncHttpClient();

        ActorRef storageActor = system.actorOf (StorageActor.props(), "Storage-Actor");




        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = router.createRoute();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                materializer
        );

        System.out.println("Server online at " + host + ":" + port + "/");
    }
}
