import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vovan
 * Date: 11/1/12
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Node master=new Node(2122,true,2123,2124);
        Node slave1=new Node(2123,false);
        Node slave2=new Node(2124,false);

        LoadBalancer loadBalancer=new LoadBalancer(8080, 2122, 2123, 2124);
        int portOfLoadBalanser=8080;
        while (true) {
            MyHttpClient m = new MyHttpClient();
            String q = m.enterCommand();
            LoadBalancer.doQuery(q, portOfLoadBalanser);
        }
    }
}
