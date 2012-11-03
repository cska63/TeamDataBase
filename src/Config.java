import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bassin
 * Date: 03.11.12
 * Time: 21:28
 * To change this template use File | Settings | File Templates.
 */
public class Config {
    public String router;
    public List<NodeAddr> shards = new ArrayList<NodeAddr>();

    public Config(String _router, List<NodeAddr> _ar) {
        router = _router;
        shards = _ar;
    }
}
