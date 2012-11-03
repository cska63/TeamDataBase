import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vovan
 * Date: 11/1/12
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {


    public static void main(String[] args) throws IOException {
//        Node master1=new Node(2122,true,2123,2124);
//        Node slave11=new Node(2123,false);
//        Node slave12=new Node(2124,false);
        ArrayList<String> addr1 = new ArrayList<String>();
        addr1.add("127.0.0.1:2223");
        addr1.add("127.0.0.1:2224");
//
//        Node master2=new Node(2222,true,2223,2224);
//        Node slave21=new Node(2223,false);
//        Node slave22=new Node(2224,false);
        ArrayList<String> addr2 = new ArrayList<String>();
        addr2.add("127.0.0.1:2123");
        addr2.add("127.0.0.1:2124");
//
        ArrayList<NodeAddr> ar = new ArrayList<NodeAddr>();
        ar.add(new NodeAddr("127.0.0.1:2222",addr1));
        ar.add(new NodeAddr("127.0.0.1:2122",addr2));
//
//
        LoadBalancer loadBalancer=new LoadBalancer(8080, ar);
        int portOfLoadBalancer=8080;
        MyHttpClient m = new MyHttpClient(8888);
        Config c = LoadBalancer.loadConfig("config.json");
        while (true) {
            String q = m.enterCommand();
            System.out.println(LoadBalancer.doQuery(q, c.router));
        }
    }
}