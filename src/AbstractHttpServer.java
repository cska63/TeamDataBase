import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public abstract class AbstractHttpServer implements HttpHandler {
    private HttpServer server;
    protected int myPort;

    /**
     * Create server
     *
     * @param port -number of port
     */
    public void create(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", this);
            myPort = port;
            this.serverStart();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Старт сервера
     */
    protected void serverStart() {
        server.start();
        //System.out.println("Server started");
    }


    protected void serverStop(int ch) {
        server.stop(ch);
        System.out.println("Server stoped");
        System.exit(1);
    }

    public abstract void handle(HttpExchange exc) throws IOException;
}
