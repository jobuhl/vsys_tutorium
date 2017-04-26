package ex1.part3.c;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.Serializable;

public class MySerializableClass implements Serializable {
    private static final long serialVersionUID = 1;
    private int id;
    private String string;
    transient MyNonSerializableClass myNonSerializableClass;


    MySerializableClass() {
        id = 1234;
        this.myNonSerializableClass = new MyNonSerializableClass();

    }

    public void set(String string) {
        this.string = string;
    }

    public String toString() throws NullPointerException {
String bla;
        if (this.myNonSerializableClass == null)  bla = "";
        return "id: " + id + "; string: " + string + myNonSerializableClass;
    }
}
