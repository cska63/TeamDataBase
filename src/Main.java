import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config c = LoadBalancer.loadConfig("config.json");
        new LoadBalancer("config.json");
        MyHttpClient m = new MyHttpClient(LoadBalancer.cutPortFromAddress(c.client));
        while (true) {
            String q = m.enterCommand();
            System.out.println(LoadBalancer.doQuery(q, c.router));
        }
    }
}