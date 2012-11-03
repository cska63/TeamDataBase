import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public abstract class AbstractHttpServer implements HttpHandler {
    public HttpServer server;
    private int portOfSlave1=0;

    /**
     * Create server
     * @param port -number of port
     */
    public void create(int port) {
        try{
        server = HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/", this);
        this.serverStart();
            System.out.println("port= "+port);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
    /**
     * Старт сервера
     */
    private void serverStart() {
        server.start();
        //System.out.println("Server started");
    }

    /**
     * остановка сервера
     * @param ch
     */
    private void serverStop(int ch) {
        server.stop(ch);
        System.out.println("Server stoped");
    }

    public abstract void handle(HttpExchange exc) throws IOException;
}
