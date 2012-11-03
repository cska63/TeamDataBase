import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;


public class Node extends AbstractHttpServer {

    private final int DEFAULT_SLAVE_PORT_1 = 2122;
    private final int DEFAULT_SLAVE_PORT_2 = 2123;

    private boolean isMaster;
    private int portOfSlave1 = DEFAULT_SLAVE_PORT_1;
    private int portOfSlave2 = DEFAULT_SLAVE_PORT_2;


    public Node(int port,boolean _isMaster) {
        this.create(port);
        isMaster=_isMaster;
    }
    public Node(int port,boolean _isMaster,int _portOfSlave1,int _portOfSlave2){
        this.create(port);
        isMaster=_isMaster;
        portOfSlave1=_portOfSlave1;
        portOfSlave2=_portOfSlave2;
    }

//    private String[] processingQuerry(String string) {
//        string = string.substring("/?command=".length());
//        final int i = string.lastIndexOf('&');
//        string = string.substring(0, i);
//        String[] ans = string.split("\\+");
//        return ans;
//    }

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
                LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave1);
                LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave2);
            }
            //System.out.println("Phonebook created");
            return "Phonebook created";
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
                LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave1);
                LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave2);
            }
            //System.out.println("Record added");
            return "Record added";
        } else if (line[0].equalsIgnoreCase("UPDATE")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            if (this.db.update(line[3], line[4],
                    Integer.parseInt(line[1]))) {
                if(isMaster) {
                    String k="";
                    for (String e : line) {
                        k = k.concat(e + " ");
                    }
                    LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave1);
                    LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave2);
                }
                return "Record updated";
            } else
             //   System.out.println("Record with ID = "
             //           + Integer.parseInt(line[1])
             //           + "was not found");
            return "Record with ID = "
                    + Integer.parseInt(line[1])
                    + "was not found";
        } else if (line[0].equalsIgnoreCase("EXIT_BD")) {
            this.db.resetName("");
            return "OK";
        } else if (line[0].equalsIgnoreCase("DELETE")) {
            if (this.db.getNameBD().equals("")) {

                return "Data base is not created";
            }
            if (this.db.delete(Integer.parseInt(line[1]))) {
                if(isMaster) {
                    String k="";
                    for (String e : line) {
                        k = k.concat(e + " ");
                    }
                    LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave1);
                    LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave2);
                }
               // System.out.println("Record deleted");
                return "Record deleted";
            } else
                //System.out.println("Record with ID = "
                //        + Integer.parseInt(line[1])
                //        + " was not found");
            return "Record with ID = "
                    + Integer.parseInt(line[1])
                    + " was not found";
        } else if (line[0].equalsIgnoreCase("RENAME_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            //System.out.println("Base \"" + this.db.resetName(line[1])
              //      + "\" renamed to \"" + line[1] + "\"");
            return "\"Base \\\"\" + this.db.resetName(line[1])\n" +
                    "                    + \"\\\" renamed to \\\"\" + line[1] + \"\\\"\"";
        } else if (line[0].equalsIgnoreCase("GET_BY_ID")) {
            //System.out
              //      .println(this.db.getByID(Integer.parseInt(line[1])));
            return this.db.getByID(Integer.parseInt(line[1]));
        } else if (line[0].equalsIgnoreCase("GET_BY_NAME")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            //System.out.println(this.db.getByName(line[1]));
            return this.db.getByName(line[1]);
        } else if (line[0].equalsIgnoreCase("GET_BY_NUMBER")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            //System.out.println(this.db.getByNumber(line[1]));
            return this.db.getByNumber(line[1]);
        } else if (line[0].equalsIgnoreCase("SHOW_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            //System.out.println(this.db.showBD());
            return this.db.showBD();
        } else if (line[0].equalsIgnoreCase("SAVE_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            if(isMaster) {
                String k="";
                for (String e : line) {
                    k = k.concat(e + " ");
                }
                LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave1);
                LoadBalancer.doQuery(k,"127.0.0.1:" + portOfSlave2);
            }
            this.db.save();
            //System.out.println("Base has been saved.");
            return "\"Base has been saved.\"";
        } else if (line[0].equalsIgnoreCase("LOAD_BD")) {
            try {
                this.db = DataBaseB.load(line[1]);
              //  System.out.println(this.db.showBD());
                return this.db.showBD();
            } catch (FileNotFoundException e) {
                //System.out.println("File not found.");
                return "NO";
            }
        } else {
            //System.out
            //        .println("Unknown command. Please look at README.");
            return "Unknown command. Please look at README.";
        }
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
        String k = "";
        if (str.length() > 2) {
            String q = LoadBalancer.processingQuerry(str);
//            for (String e : q) {
//                k = k.concat(e + " ");
//            }
           // System.out.println(k);
            String ans = null;
            try {
                ans = this.make(q.split(" "));
                //System.out.println(ans);
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

    private DataBaseB db = new DataBaseB("");

//    public static void main(String[] args) {
//        Node server1 = new Node(2122,true,2123,2124);
//        Node slave1 = new Node(2123,false);
//        Node slave2 = new Node(2124,false);
//    }
}