import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class NodeAddr {
    public String masterAddr;
    public ArrayList<String> slavesAddr;

    public NodeAddr(String _master, ArrayList<String> _slaves) {
        masterAddr = _master;
        slavesAddr = _slaves;
    }
}
