//import com.sun.net.httpserver.HttpExchange;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.URI;
//
//public class LoadBalancer extends AbstractHttpServer {
//    /**
//     * @param port1 - сервер
//     * @param port2 - мой
//     * @throws IOException
//     */
//    private int portOfNode1;
//    private int portOfNode2;
//    private int kNode1;
//    private int kNode2;
//
//    public LoadBalancer(int port1, int port2, int port3) {
//        this.create(port1);
//        portOfNode1 = port2;
//        portOfNode2 = port3;
//        kNode1 = 0;
//        kNode2 = 0;
//    }
//
//    public static String parseHtml(String html) {
//        String string = html.replaceFirst("<html>", "").replaceFirst("</html>", "");
//        string = string.replaceAll("<html>(.*)</html>", "");
//        return string.trim();
//    }
//
//    public static String doQuery(String query, int port2) throws IOException {
//        HttpClient client = new DefaultHttpClient();
//        HttpGet request = new HttpGet("http://localhost:" + port2 + "/?command=" + query.replace(" ", "+") + "&submit=submit");
//        HttpResponse response = null;
//        try {
//            response = client.execute(request);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//        String answer = "";
//        try {
//            while ((query = rd.readLine()) != null) {
//                answer = answer.concat(query);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//       // System.out.println(LoadBalancer.parseHtml(answer));
//        rd.close();
//        return LoadBalancer.parseHtml(answer);
//    }
//
//    private String[] processingQuerry(String string) {
//        string = string.substring("/?command=".length());
//        final int i = string.lastIndexOf('&');
//        string = string.substring(0, i);
//        String[] ans = string.split("\\+");
//        return ans;
//    }
//
//    public void handle(HttpExchange exc) throws IOException {
//        exc.sendResponseHeaders(200, 0);
//        PrintWriter out = new PrintWriter(exc.getResponseBody());
//        final URI u = exc.getRequestURI();
//        final String query = u.toString();
//        String[] q = processingQuerry(query);
//        String k = "";
//        for (String e : q) {
//            k = k.concat(e + " ");
//        }
//        int wantedPort = 0;
//
//
//        String answer="";
//        if (k.matches("new .*")) {
//            System.out.println("da");
//            answer = doQuery(k, portOfNode2);
//            doQuery(k, portOfNode1);
//        } else if (k.trim().equals("show_nodes")) {
//            System.out.println("Node №1: " + doQuery("show_bd", portOfNode1));
//            System.out.println("Node №2: " + doQuery("show_bd", portOfNode2));
//
//        } else {
//            System.out.println("ADD");
//            System.out.println("------------------");
//            System.out.println("kNode1= "+kNode1+" kNode2= "+kNode2 );
//            if (this.kNode1 <this.kNode2) {
//                wantedPort = this.portOfNode1;
//                kNode1+=1;
//            } else {
//                kNode2+=1;
//                wantedPort = this.portOfNode2;
//            }
//            System.out.println(k+"\n"+wantedPort);
//            answer = doQuery(k, wantedPort);
//
//
//        }
//        System.out.println(answer);
//        out.println("<html>" + answer + "</html>");
//        out.close();
//        exc.close();
//    }
//
//    public static void main(String[] args) throws IOException {
//        LoadBalancer l = new LoadBalancer(2121, 8080, 2122);
//    }
//
//}


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
    private int portOfMaster = 0;
    private int portOfSlave1 = 0;
    private int portOfSlave2 = 0;

    public LoadBalancer(int port1, int _portOfMaster, int _portOfSlave1, int _portOfSlave2) {
        this.create(port1);
        portOfMaster = _portOfMaster;
        portOfSlave1 = _portOfSlave1;
        portOfSlave2 = _portOfSlave2;

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
        String answer = "";
        if (k.contains("GET_BY_NAME") == true) {
            answer = doQuery(k, this.portOfSlave1);
            System.out.println("Slave 1: " + answer);

        } else {
            answer = doQuery(k, this.portOfMaster);
            System.out.println(answer);
        }
        out.println("<html>" + answer + "</html>");
        out.close();
        exc.close();
    }

    public static void main(String[] args) throws IOException {
        LoadBalancer l = new LoadBalancer(8080, 2122, 2123, 2124);
    }

}



