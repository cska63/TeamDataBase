import com.sun.net.httpserver.HttpExchange;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;

public class LoadBalancer extends AbstractHttpServer {
    /**
     * @param port1 - сервер
     * @param port2 - мой
     * @throws IOException
     */
    int portOfNode;
    public LoadBalancer(int port1, int port2) {
        this.create(port1);
        portOfNode=port2;
    }

    public static String parseHtml(String html) {
        String string = html.replaceFirst("<html>", "").replaceFirst("</html>", "");
        string = string.replaceAll("<html>(.*)</html>", "");
        return string.trim();
    }

    public static String doQuery(String query, int port2) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:" + port2 + "/?command=" + query.replace(" ", "+") + "&submit=submit");
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String answer = "";
        try {
            while ((query = rd.readLine()) != null) {
                answer = answer.concat(query);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(LoadBalancer.parseHtml(answer));
        rd.close();
        return LoadBalancer.parseHtml(answer);
    }

    private String[] processingQuerry(String string) {
        string = string.substring("/?command=".length());
        final int i = string.lastIndexOf('&');
        string = string.substring(0, i);
        String[] ans = string.split("\\+");
        return ans;
    }

    public void handle(HttpExchange exc) throws IOException {
        exc.sendResponseHeaders(200, 0);
        PrintWriter out = new PrintWriter(exc.getResponseBody());
        final URI u = exc.getRequestURI();
        final String query = u.toString();
        String[] q = processingQuerry(query);
        String k = "";
        for (String e : q) {
            k = k.concat(e + " ");
        }
        String answer = doQuery(k, this.portOfNode);
        System.out.println(answer);
        out.println("<html>" + answer + "</html>");
        out.close();
        exc.close();
    }

    public static void main(String[] args) throws IOException {
        LoadBalancer l = new LoadBalancer(2121, 8080);
    }

}