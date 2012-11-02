import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 02.11.12
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
public class NodeAddr {
    public String masterAddr;
    public ArrayList<String> slavesAddr;

    public NodeAddr(String _master, ArrayList<String> _slaves) {
        masterAddr = _master;
        slavesAddr = _slaves;
    }
}
