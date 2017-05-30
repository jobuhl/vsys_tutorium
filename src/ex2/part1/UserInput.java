package ex2.part1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInput implements Runnable {

	private BufferedReader reader;

	@Override
	public void run() {
		while (true) {
			reader = new BufferedReader(new InputStreamReader(System.in));
			String temp = null;
			try {
				temp = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (temp.equals("x")) {
				ClientMain.inputSet();
				System.out.println("---Wurde abgebrochen---");
				break;
			}
		}
	}
}
