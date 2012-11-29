import com.sun.net.httpserver.HttpExchange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;


@SuppressWarnings("UnusedAssignment")
public class Node extends AbstractHttpServer {

    private boolean isMaster;
    private ArrayList<String> slavesAddr = null;

    public Node(int port, boolean _isMaster, ArrayList<String> _slavesAddr ) {
        this.create(port);
        isMaster=_isMaster;
        slavesAddr = _slavesAddr;
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

                for (String aSlavesAddr : slavesAddr) {
                    LoadBalancer.doQuery(k, aSlavesAddr);
                }
            }
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

                    for (String aSlavesAddr : slavesAddr) {
                        LoadBalancer.doQuery(k, aSlavesAddr);
                    }
                }
                return "Record updated";
            } else
            return "Record with ID = "
                    + Integer.parseInt(line[1])
                    + "was not found";
        } else if (line[0].equalsIgnoreCase("EXIT_BD")) {
            this.db.flush();
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

                    for (String aSlavesAddr : slavesAddr) {
                        LoadBalancer.doQuery(k, aSlavesAddr);
                    }
                }
                return "Record deleted";
            } else
           return "Record with ID = "
                    + Integer.parseInt(line[1])
                    + " was not found";
        } else if (line[0].equalsIgnoreCase("RENAME_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            return "\"Base \\\"\" + this.db.resetName(line[1])<br>" +
                    "                    + \"\\\" renamed to \\\"\" + line[1] + \"\\\"\"";
        } else if (line[0].equalsIgnoreCase("GET_BY_ID")) {
            return this.db.getByID(Integer.parseInt(line[1]));

        }  else if (line[0].equalsIgnoreCase("SET_MASTER")) {
            this.setMaster(true);
            return "";
        } else if (line[0].equalsIgnoreCase("UNSET_MASTER")) {
            this.setMaster(false);
            return "";
        } else if (line[0].equalsIgnoreCase("GET_BY_NAME")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            return this.db.getByName(line[1]);
        } else if (line[0].equalsIgnoreCase("GET_BY_NUMBER")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            return this.db.getByNumber(line[1]);
        } else if (line[0].equalsIgnoreCase("SHOW_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }
            return this.db.showBD();
        } else if (line[0].equalsIgnoreCase("SAVE_BD")) {
            if (this.db.getNameBD().equals("")) {
                return "Data base is not created";
            }

            this.db.savetoJson(myPort);
            return "\"Base has been saved.\"";
        } else if (line[0].equalsIgnoreCase("LOAD_BD")) {
            try {
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

    public static void main(String[] args) {
        ArrayList<String> addr1 = new ArrayList<String>(); addr1.add("127.0.0.1:2223"); addr1.add("127.0.0.1:2224");
        ArrayList<String> addr2 = new ArrayList<String>(); addr2.add("127.0.0.1:2222"); addr2.add("127.0.0.1:2224");

        ArrayList<String> addr3 = new ArrayList<String>(); addr3.add("127.0.0.1:2222"); addr3.add("127.0.0.1:2223");

        Node server1 = new Node(2222,true,addr1);
        Node slave1 = new Node(2223,false,addr2);
        Node slave2 = new Node(2224,false,addr3);
    }
}