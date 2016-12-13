package nl.timnederhoff.tools.maischperfect;

public class TemperatureRun implements Runnable{
	private Thread tempThread;
	private double currentTemp;
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
				currentTemp += 1.2 + Math.random() - 0.5;
			} else {
				currentTemp -= 0.8 + Math.random() - 0.5;
			}
			sleep(1000);
		}
	}

	public double getCurrentTemp() {
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
