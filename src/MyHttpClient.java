import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
public class MyHttpClient {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        while (true) {
            System.out.println("Inout command");
            line = br.readLine();
            if (line.equals("quit")) {
                System.exit(1);
            }
            int port = (args.length>0) ? Integer.parseInt(args[0]) : 2121;
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://localhost:" + port + "/?command=" + line.replace(" ", "+")+"&submit=submit");
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String answer="";
            while ((line = rd.readLine()) != null) {
                answer=answer.concat(line);
            }
            String string=answer.replaceFirst("<html>","").replaceFirst("</html>","");
            string=string.replaceAll("<html>(.*)</html>","");
            System.out.println(string.trim());


        }
    }
}