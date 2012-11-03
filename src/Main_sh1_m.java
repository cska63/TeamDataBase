import java.io.IOException;
import java.util.ArrayList;


public class Main_sh1_m {

    public static void main(String[] args) throws IOException {

        Config c = LoadBalancer.loadConfig("config.json");
        Node sh1_m = new Node(Integer.parseInt(c.shards.get(0).masterAddr.substring(c.shards.get(0).masterAddr.indexOf(":")+1)),true,c.shards.get(0).slavesAddr);
    }
}