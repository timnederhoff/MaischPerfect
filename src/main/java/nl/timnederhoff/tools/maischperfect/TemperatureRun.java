package nl.timnederhoff.tools.maischperfect;

public class TemperatureRun implements Runnable{
	private Thread tempThread;
	private int currentTemp;
	private boolean heaterOn;

	public TemperatureRun() {
		heaterOn =false;
		currentTemp = 20;
		tempThread = new Thread(this, "temperature thread");
		tempThread.start();
	}

	@Override
	public void run() {
		while (true) {
			if (heaterOn) {
				currentTemp += 2;
			} else {
				currentTemp -= 1;
			}
			sleep(1000);
		}
	}

	public int getCurrentTemp() {
		return currentTemp;
	}

	public void setHeaterOn(boolean heaterOn) {
		this.heaterOn = heaterOn;
	}

	private void sleep(int milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			System.out.println("something went wrong while sleeping");
			e.printStackTrace();
		}
	}
}
