package ex1.part3.b_advanced;

public class MyNonSerializableClass {
	private int id;

	MyNonSerializableClass() {
		id=5678;
	}

	public String toString() {
		return "id: "+id;
	}
}
	