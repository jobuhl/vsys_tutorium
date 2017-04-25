package ex1.part3.b_advanced;

import java.io.Serializable;

public class MySerializableClass implements Serializable {
    private static final long serialVersionUID = 1;
    private int id;
    private String string;
    private MyNonSerializableClass myNonSerializableClass;

    MySerializableClass() {
        this.id = 1234;
        this.myNonSerializableClass = new MyNonSerializableClass();    }

    public void set(String string) {
        this.string = string;
    }

    public String toString() {

        return "id: " + id + "; string: "
                + myNonSerializableClass.toString()
        ;
    }
}
