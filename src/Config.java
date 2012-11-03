import java.util.ArrayList;
import java.util.List;

public class Config {
    public String router;
    public String client;
    public List<NodeAddr> shards = new ArrayList<NodeAddr>();

    public Config(String _router, String _client, List<NodeAddr> _ar) {
        router = _router;
        client = _client;
        shards = _ar;
    }
}
