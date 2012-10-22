package databases;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class DataBaseB implements Serializable {
    private String bName;
    private TreeMap<Integer, Person> baseTree = new TreeMap<Integer, Person>();
    private TreeMap<String, LinkedList<Integer>> name_ID = new TreeMap<String, LinkedList<Integer>>(),
            number_ID = new TreeMap<String, LinkedList<Integer>>();

    private int currID = 0;

    public DataBaseB(String name) {
        this.bName = name;
    }


    public String resetName(String name) {
        String tmpStr = this.bName;
        this.bName = name;
        return tmpStr;
    }

    public String getNameBD() {
        return bName;
    }

    public void add(String name, String number) {
        Person per = new Person(name, number, currID++);
        baseTree.put(per.getID(), per);
        addToBase(name_ID, per.getName(), per.getID());
        addToBase(number_ID, per.getNumber(), per.getID());
    }

    private void addToBase(TreeMap<String, LinkedList<Integer>> base,
                           String key, int id) {
        if (!base.containsKey(key)) {
            LinkedList<Integer> tmpList = new LinkedList<Integer>();
            tmpList.add(id);
            base.put(key, tmpList);
        } else {
            base.get(key).add(id);
        }
    }

    public boolean delete(int id) {
        Person per = baseTree.remove(id);
        if (per == null)
            return false;
        delFromBase(name_ID, per.getName(), per.getID());
        delFromBase(number_ID, per.getNumber(), per.getID());
        return true;
    }

    private void delFromBase(TreeMap<String, LinkedList<Integer>> base,
                             String key, int id) {
        LinkedList<Integer> tmpList = base.get(key);
        tmpList.remove(tmpList.indexOf(id));
        if (tmpList.isEmpty())
            base.remove(key);
    }

    public String getByID(int id) {
        Person p = baseTree.get(id);
        return p == null ? "Nothing found" : p.toString();
    }

    public String getByName(String name) {
        return getFromBase(name_ID, name);
    }

    public String getByNumber(String num) {
        return getFromBase(number_ID, num);
    }

    private String getFromBase(TreeMap<String, LinkedList<Integer>> base,
                               String key) {
        if (!base.containsKey(key))
            return "Nothing found";
        LinkedList<Integer> tmpList = base.get(key);
        String tmpStr = "";
        for (int id : tmpList) {
            tmpStr += baseTree.get(id).toString() + "\n";
        }
        return tmpStr;
    }

    public boolean update(String name, String number, int id) {
        if (!baseTree.containsKey(id))
            return false;
        Person newPer = new Person(name, number, id);
        Person oldPer = baseTree.put(id, newPer);
        updBase(name_ID, oldPer.getName(), newPer.getName(), id);
        updBase(number_ID, oldPer.getNumber(), newPer.getNumber(), id);
        return true;
    }

    private void updBase(TreeMap<String, LinkedList<Integer>> base,
                         String oldVal, String newVal, int id) {
        delFromBase(base, oldVal, id);
        addToBase(base, newVal, id);
    }

    public String showBD() {
        String ans = "";
        for (Map.Entry<String, LinkedList<Integer>> entry : name_ID.entrySet()) {
            for (int id : entry.getValue()) {
                ans += baseTree.get(id).toString() + '\n';
            }
        }
        return ans;
    }

    public void save() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                bName + ".bb"));
        oos.writeObject(this);
        oos.close();
    }

    public static DataBaseB load(String adr) throws IOException,
            ClassNotFoundException {
        System.out.println(adr);
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(adr
                + ".bb"));
        return (DataBaseB) oin.readObject();
    }
}