package bokarev;

import java.util.ArrayList;

public class PutMessage {
    Arrayst<String> servers;

    public PutMessage(ArrayList<String> servers) {
        this.servers = servers;
    }

    public ArrayList<String> getServers() {
        return servers;
    }
}
