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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadBalancer extends AbstractHttpServer {

    ArrayList<NodeAddr> addresses = null;
    private final int DEFAULT_DENOMINATOR = 97;
    private int lastID = 0;

    public LoadBalancer(int port1, ArrayList<NodeAddr> _servers) {
        this.create(port1);
        addresses = _servers;
    }

    public static String parseHtml(String html) {
        String string = html.replaceFirst("<html>", "").replaceFirst("</html>", "");
        string = string.replaceAll("<html>(.*)</html>", "");
        return string.trim();
    }

    public static String doQuery(String query, String addr) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://" + addr + "/?command=" + query.replace(" ", "+") + "&submit=submit");
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

    public static String processingQuerry(String string) {
        string = string.substring("/?command=".length());
        final int i = string.lastIndexOf('&');
        string = string.substring(0, i);
        //String[] ans = string.split("\\+");
        string = string.replace("+", " ");
        return string;
    }

    public int getHash(String value) {
        int sum = 0;
        for (char a : value.toCharArray()) {
            sum += (int) a;
        }

        sum %= DEFAULT_DENOMINATOR;
        sum %= addresses.size();
        return sum;
    }

    public void handle(HttpExchange exc) throws IOException {
        exc.sendResponseHeaders(200, 0);
        PrintWriter out = new PrintWriter(exc.getResponseBody());
        final URI u = exc.getRequestURI();
        final String query = u.toString();

        String q = processingQuerry(query);
//        String k = "";
//        for (String e : q) {
//            k = k.concat(e + " ");
//        }
        String answer = "";
        if (q.contains("get_by_name") == true) {
            String name = q.substring(q.indexOf(" ") + 1);
            answer = doQuery(q, addresses.get(getHash(name)).slavesAddr.get(0)); //wtf
            //System.out.println("Result: " + answer);

        } else if (q.contains("get_by_id") || q.contains("get_by_number")) {
            for (NodeAddr a : addresses) {
                answer += doQuery(q, a.slavesAddr.get(0)) + "\n";
            }
            answer = answer.replaceAll("Nothing found\n", "");

            if (answer.trim().equals(""))
                answer = "Nothing found";
//            System.out.println(answer);
        } else if (q.contains("new")) {
            for (NodeAddr a : addresses) {
                answer += doQuery(q, a.masterAddr) + "\n";
            }
        } else if (q.contains("delete")) {
            for (NodeAddr a : addresses) {
                answer += doQuery(q, a.masterAddr) + "\n";
            }

        } else if (q.contains("update")) {
            //id+old_name+new_name+tel
            Pattern pattern=Pattern.compile("([update]{1}) (\\d+) ([A-Za-z]+) ([A-Za-z]+) (.+)");
            Matcher matcher=pattern.matcher(q);
            matcher.find();
            String name = matcher.group(3);
            doQuery("delete "+matcher.group(2),addresses.get(getHash(name)).masterAddr) ;
            answer = doQuery("add "+matcher.group(4)+" "+matcher.group(5)+" "+matcher.group(2), addresses.get(getHash(matcher.group(4))).masterAddr);
        } else if (q.contains("add")) {
            String name = q.trim().substring(q.indexOf(" ") + 1, q.lastIndexOf(" "));
            q += " " + lastID++;
            answer += doQuery(q, addresses.get(getHash(name)).masterAddr);
        }


        out.println("<html>" + answer + "</html>");
        out.close();
        exc.close();
    }

//    public static void main(String[] args) throws IOException {
//        LoadBalancer l = new LoadBalancer(8080, 2122, 2123, 2124);
//    }

}



