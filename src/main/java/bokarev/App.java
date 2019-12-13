package bokarev;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.server.AllDirectives;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;
//import scala.concurrent.Future;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;
//import java.util.concurrent.Future;

import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.parameter;
import static org.asynchttpclient.Dsl.asyncHttpClient;


public class App extends AllDirectives {
    public static void main(String[] args) throws Exception, InterruptedException, IOException {
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AsyncHttpClient asyncHttpClient = asyncHttpClient();

        RouterActor router = new RouterActor(system, materializer, asyncHttpClient);

        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                router.createRoute();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );

        System.out.println("Server online at http://localhost:8080/");


    }
}
