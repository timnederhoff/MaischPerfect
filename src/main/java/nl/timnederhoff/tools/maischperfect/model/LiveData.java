package nl.timnederhoff.tools.maischperfect.model;

public class LiveData {

	private double temperature;
	private boolean brewProcessRunning;
	private boolean heaterOn;

	public LiveData(double temperature, boolean brewProcessRunning, boolean heaterOn) {
		this.temperature = temperature;
		this.brewProcessRunning = brewProcessRunning;
		this.heaterOn = heaterOn;
	}

	public double getTemperature() {
		return temperature;
	}

	public boolean isBrewProcessRunning() {
		return brewProcessRunning;
	}

	public boolean isHeaterOn() {
		return heaterOn;
	}
}
