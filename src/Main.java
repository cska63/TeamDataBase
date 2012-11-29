import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        Config c = LoadBalancer.loadConfig("config.json");

        try {

            if (args[0].equals("loadbalancer")) {

                new LoadBalancer("config.json");
                MyHttpClient m = new MyHttpClient(LoadBalancer.cutPortFromAddress(c.client));
                while (true) {
                    String q = m.enterCommand();
                    System.out.println(LoadBalancer.doQuery(q, c.router));
                }
            } else if (args[0].equals("master")) {

                int num = Integer.parseInt(args[1]);
                int port = LoadBalancer.cutPortFromAddress(c.shards.get(num).masterAddr); //
                new Node(port,true,c.shards.get(num).slavesAddr);
            } else if (args[0].equals("slave")) {

                int shardNum = Integer.parseInt(args[1]);
                int slaveNum = Integer.parseInt(args[2]);
                ArrayList<String> newl = new ArrayList<String>(c.shards.get(shardNum).slavesAddr.subList(1,c.shards.get(shardNum).slavesAddr.size()-1));
                newl.add(c.shards.get(shardNum).masterAddr);
                int port = LoadBalancer.cutPortFromAddress(c.shards.get(shardNum).slavesAddr.get(slaveNum));
                new Node(port,false,newl);
            }

        }
        catch (Exception e) {

            System.out.println("Wrong arguments");
        }
    }
}