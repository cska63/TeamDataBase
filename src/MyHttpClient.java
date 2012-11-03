import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;


/**
 * User: vovan
 * Date: 20.10.12
 * Time: 16:48
 * Класс http-клиента  пытаелтся отправить GET-запрос: который создается после ввода команды
 * с клавиатуры. Ответ принимается в формате html(для того: чтобы можно было лазить в браузер) и из него вытаскивается
 * нужный для нас ответ.
 * Порт по умолчанию устанавливается 2121. Для просмотра в браузере нужно зайти на
 * http://localhost:<port> или 127.0.0.1:<port>
 */

/**
 * /?command=new+qwe&submit=submit
 */
public class MyHttpClient extends AbstractHttpServer {
     public MyHttpClient(int port){
         this.create(port);
     }
    public String enterCommand() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        System.out.println("Input command");
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.equals("quit")) {
            System.exit(1);
        }

        return line;
    }


    @Override
    public void handle(HttpExchange exc) throws IOException {
        exc.sendResponseHeaders(200, 0);
        PrintWriter out = new PrintWriter(exc.getResponseBody());
        final URI u = exc.getRequestURI();
        final String str = u.toString();
        String k = "";
        if (str.length() > 2) {
            String q = LoadBalancer.processingQuerry(str);
            String ans = null;
                ans=LoadBalancer.doQuery(q,"127.0.0.1:8080");
                //ans = this.make(q.split(" "));
                //System.out.println(ans);

            out.println("<html>" + ans + "</html>");
        }

        out.println("<html>" +
                "<form>" +
                "<label>" +
                "<input type='text' type='text' name='command' id='textfield' >" +
                "</label>" +
                "<label>" +
                "<input type='submit' name='submit' id = 'value' value='submit'>" +
                "</form>" +
                "</html>");
        out.close();
        exc.close();
    }

//    public static void main(String[] args) throws IOException {
//        int portOfLoadBalanser=8080;
//        while (true) {
//            MyHttpClient m = new MyHttpClient();
//            String q = m.enterCommand();
//            LoadBalancer.doQuery(q, portOfLoadBalanser);
//        }
//    }
}

