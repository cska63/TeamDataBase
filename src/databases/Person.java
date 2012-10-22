package databases;

import java.io.Serializable;

public class Person implements Serializable {
    private String name, pnumber;
    final private int ID;

    public Person(String name, String number, int ID) {
        this.name = name;
        this.pnumber = number;
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return pnumber;
    }

    public int getID() {
        return ID;
    }

    public String toString() {
        return "(ID = " + ID + ") " + name + " " + pnumber;
    }
}