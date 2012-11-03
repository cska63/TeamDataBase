import java.io.IOException;
import java.util.ArrayList;


public class Main_sh1_m {

    public static void main(String[] args) throws IOException {

        Config c = LoadBalancer.loadConfig("config.json");
        int port=LoadBalancer.cutPortFromAddress(c.shards.get(0).masterAddr);
        Node sh1_m = new Node(port,true,c.shards.get(0).slavesAddr);
    }
}