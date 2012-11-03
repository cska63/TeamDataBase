import java.io.IOException;
import java.util.ArrayList;


public class Main_sh1_s2 {

    public static void main(String[] args) throws IOException {

        Config c = LoadBalancer.loadConfig("config.json");
        ArrayList<String> newl = new ArrayList<String>(c.shards.get(0).slavesAddr.subList(1,c.shards.get(0).slavesAddr.size()-1));
        newl.add(c.shards.get(0).masterAddr);
        Node sh1_m = new Node(Integer.getInteger(c.shards.get(0).slavesAddr.get(1).substring(c.shards.get(0).slavesAddr.get(1).indexOf(":")+1)),false,newl);
    }
}