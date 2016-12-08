package nl.timnederhoff.experiments;

public class TemperatureRun implements Runnable{
	private Thread tempThread;
	private int[] tempProcess;
	private int currentTemp;

	public TemperatureRun() {
		tempProcess = new int[] {
				20,21,22,23,24,25,26,27,28,28,29, //stijgen naar 30 graden
				30,31,32,32,31,30,30,29,29,28,28,27,27, //12x rond 30 rust
				28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45, //stijgen naar 45 graden
				45,45,44,44,43,43,42,42,41,
				41,42,43,44,45,46,47,48,49,50,51,//stijgen naar 51 graden
				51,51,50,50,49,49,48,48,47,47,//10x rond 51 hangen
				47,48,49,50,51,52,53,54,55,56,57,58,59,60,//stijgen naar de 60 graden
				60,60,59,59,58,58,57,57,56,56,55,55,54
		};
		tempThread = new Thread(this, "temperature thread");
		tempThread.start();
	}

	@Override
	public void run() {
		for (int i : tempProcess) {
			currentTemp = i;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.out.println("something went wrong while sleeping");
				e.printStackTrace();
			}
		}
	}

	public int getCurrentTemp() {
		return currentTemp;
	}
}
