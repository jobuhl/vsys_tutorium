package ex1.part3.c;

import java.io.*;

public class MySerializer {
    private MySerializableClass mySerializableClass;

    MySerializer(MySerializableClass serializableClass) {
        mySerializableClass = serializableClass;
    }

    private String readFilename() throws IOException {
        String filename;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("filename> ");
        filename = reader.readLine();

        return filename;
    }

    public void write(String text) throws IOException {
        mySerializableClass.set(text);
        String filename = readFilename();

            OutputStream fileOutputStream = new FileOutputStream("dummy.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(mySerializableClass);


        // Implementierung erforderlich
        // Serialisiere mySerializableClass in Datei

    }

    public String read() throws IOException, ClassNotFoundException, NullPointerException {
        String filename = readFilename();

        FileInputStream fileInputStream = new FileInputStream("dummy.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        MySerializableClass mySerializableClass = (MySerializableClass) objectInputStream.readObject();

        // Implementierung erforderlich
        // Serialisiere mySerializableClass von Datei

        return mySerializableClass.toString();
    }
} 
