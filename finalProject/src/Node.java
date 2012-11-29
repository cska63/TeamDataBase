import com.sun.net.httpserver.HttpExchange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;


@SuppressWarnings("UnusedAssignment")
public class Node extends AbstractHttpServer {

    private boolean isMaster;
    private ArrayList<String> slavesAddr = null;

    public Node(int port, boolean _isMaster, ArrayList<String> _slavesAddr ) {
        this.create(port);
        isMaster=_isMaster;
        slavesAddr = _slavesAddr;

        System.out.println(isMaster ? "Master":"Slave" + " started on port:" + port);
    }

    /**
     * @param line - входная команда
     * @return - результат обработки команды
     *         Метод осуществляет выполнение команды: в случаи: если она введена вравильно
     */
    public String make(String[] line) throws IOException, ClassNotFoundException {
        if (line[0].equalsIgnoreCase("NEW")) {
            this.db = new DataBaseB(line[1]);
            if(isMaster) {
                String k="";
                for (String e : line) {
                    k = k.concat(e + " ");
                }

                for (String aSlavesAddr : slavesAddr) {
                    LoadBalancer.doQuery(k, aSlavesAddr);
                }
            }
            System.out.println("Phonebook created with name: " + line[1]);
            return "Phonebook created";
        } else if(line[0].equalsIgnoreCase("shutdown_all")){

            System.out.println("Shutdown command accepted. 5 seconds before shutdown");
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            this.serverStop(1);

            return "OK";

        } else if (line[0].equalsIgnoreCase("ADD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            this.db.add(Integer.parseInt(line[3]),line[1], line[2]);
            if(isMaster) {
                String k="";
                for (String e : line) {
                    k = k.concat(e + " ");
                }

                for (String aSlavesAddr : slavesAddr) {
                    LoadBalancer.doQuery(k, aSlavesAddr);
                }
            }

            String res = "Record added" + "ID = " + Integer.parseInt(line[3]);
            System.out.println(res);

            return res;
        } else if (line[0].equalsIgnoreCase("UPDATE")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            String res = new String();

            if (this.db.update(line[3], line[4],
                    Integer.parseInt(line[1]))) {
                if(isMaster) {
                    String k="";
                    for (String e : line) {
                        k = k.concat(e + " ");
                    }

                    for (String aSlavesAddr : slavesAddr) {
                        LoadBalancer.doQuery(k, aSlavesAddr);
                    }
                }

                res = "Record with id=" + line[1] + "updated";

            } else
                res = "Record with ID = "
                    + line[1]
                    + "was not found";

            System.out.println(res);
            return res;
        } else if (line[0].equalsIgnoreCase("EXIT_BD")) {
            this.db.flush();
            String res = "Exit from " + db.getNameBD() + " successful";
            System.out.println(res);
            return res + " on " + (this.isMaster ? "Master:" : "Slave:") + this.getIp();

        } else if (line[0].equalsIgnoreCase("DELETE")) {

            if (this.db.getNameBD().equals("")) {

                return "Data base is not created";
            }

            String res = null;

            if (this.db.delete(Integer.parseInt(line[1]))) {
                if(isMaster) {
                    String k="";
                    for (String e : line) {
                        k = k.concat(e + " ");
                    }

                    for (String aSlavesAddr : slavesAddr) {
                        LoadBalancer.doQuery(k, aSlavesAddr);
                    }
                }
                res = "Record with id=" + line[1] + " deleted";
            } else
                res = "Record with ID = "
                    + line[1]
                    + " was not found";

            System.out.println(res);
            return res;

        } else if (line[0].equalsIgnoreCase("RENAME_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            String res = "Base " + this.db.resetName(line[1]) +
                    " renamed to " + line[1];
            System.out.println(res);
            return res;

        } else if (line[0].equalsIgnoreCase("GET_BY_ID")) {

            System.out.println("Get request for id = " + line[1] + " accepted");

            return this.db.getByID(Integer.parseInt(line[1]));

        }  else if (line[0].equalsIgnoreCase("SET_MASTER")) {

            System.out.println("SET_MASTER accepted. this node is Master now");
            this.setMaster(true);
            return "";

        } else if (line[0].equalsIgnoreCase("UNSET_MASTER")) {

            System.out.println("UNSET_MASTER accepted. this node is not master now");
            this.setMaster(false);
            return "";

        } else if (line[0].equalsIgnoreCase("GET_BY_NAME")) {

            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            System.out.println("Get request for name = " + line[1] + " accepted");

            return this.db.getByName(line[1]);
        } else if (line[0].equalsIgnoreCase("GET_BY_NUMBER")) {

            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            System.out.println("Get request for number = " + line[1] + " accepted");

            return this.db.getByNumber(line[1]);
        } else if (line[0].equalsIgnoreCase("SHOW_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            System.out.println("show_bd accepted. returning all records");

            return this.db.showBD();
        } else if (line[0].equalsIgnoreCase("SAVE_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            System.out.println("Saving to json");

            this.db.savetoJson(myPort);
            return "\"Base has been saved.\"";
        } else if (line[0].equalsIgnoreCase("LOAD_BD")) {
            try {

                System.out.println("Loading database");
                this.db = DataBaseB.loadFromJson(line[1],this.myPort);
                return "OK";
            } catch (FileNotFoundException e) {
                return "NO";
            }
        }else if(line[0].equals("lastID")){
              return Integer.toString(this.db.getCurrId());
        }
        else {
            return "Unknown command. Please look at README.";
        }
    }

    public String getIp() throws SocketException {

        String res = null;

        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                    res = inetAddress.toString();
                }
            }
        }

        return res;
    }

    /**
     * @param exc читаем доки
     *            парсит и выполняет команду из запроса
     *            Вывод формы html для браузера
     */
    @Override
    public void handle(HttpExchange exc) throws IOException {
        exc.sendResponseHeaders(200, 0);
        PrintWriter out = new PrintWriter(exc.getResponseBody());
        final URI u = exc.getRequestURI();
        final String str = u.toString();
        if (str.length() > 2) {
            String q = LoadBalancer.processingQuerry(str);
            String ans = null;
            try {
                ans = this.make(q.split(" "));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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

    public void setMaster(boolean _master) {
        isMaster = _master;
    }

    private DataBaseB db = new DataBaseB("");

}