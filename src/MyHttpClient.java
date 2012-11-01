
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

/**
 * /?command=new+qwe&submit=submit
 */
public class MyHttpClient {

    public String enterCommand() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        System.out.println("Inout command");
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

    public static void main(String[] args) throws IOException {
        int portOfLoadBalanser=8080;
        while (true) {
            MyHttpClient m = new MyHttpClient();
            String q = m.enterCommand();
            LoadBalancer.doQuery(q, portOfLoadBalanser);
        }
    }
}

