import java.io.IOException;
import java.util.ArrayList;


public class Main_sh2_s1 {

    public static void main(String[] args) throws IOException {

        Config c = LoadBalancer.loadConfig("config.json");
        ArrayList<String> newl = new ArrayList<String>(c.shards.get(1).slavesAddr.subList(1,c.shards.get(1).slavesAddr.size()-1));
        newl.add(c.shards.get(1).masterAddr);
        Node sh1_m = new Node(Integer.parseInt(c.shards.get(1).slavesAddr.get(0).substring(c.shards.get(1).slavesAddr.get(0).indexOf(":")+1)),false,newl);
    }
}