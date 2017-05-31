package ex2.part3;

public class ThreadCounter extends Thread {

	int aktuelleZahl = 0;
	int gezaehlt;
	Thread th[] = new Thread[5];

	public void run() {
		while (true) {
			gezaehlt = Thread.activeCount();

			if (aktuelleZahl != gezaehlt) {
				System.out.println("Threadcounter: " + gezaehlt + " Threads.");
				aktuelleZahl = gezaehlt;
				
				// returns the number of threads put into the array
				Thread.enumerate(th);

				// prints active threads
				for (int i = 0; i < 5; i++) {
					System.out.println(i + ": " + th[i]);
				}

			}
		}

	}
}
