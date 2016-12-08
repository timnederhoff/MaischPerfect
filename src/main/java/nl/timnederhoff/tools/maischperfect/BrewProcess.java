package nl.timnederhoff.tools.maischperfect;

import java.util.ArrayList;
import java.util.List;

public class BrewProcess implements Runnable {
	private List<Integer[]> maischModel;
	private List<Integer[]> appliedModel;
	private int thresHold = 2;
	private List<Integer[]> tempLog;
	private List<Object[]> switchLog;
	private int counter;
	private boolean heaterOn;
	private TemperatureRun temperatureRun;

	public BrewProcess(List<Integer[]> maischModel) {
		this.maischModel = maischModel;
		Thread myThread = new Thread(this, "my runnable thread");
		myThread.start();
	}

	@Override
	public void run() {
		System.out.println("Process started...");
		tempLog = new ArrayList<>();
		switchLog = new ArrayList<>();
		temperatureRun = new TemperatureRun();
		counter = 0;
		appliedModel = new ArrayList<>();
		sleep(3000);
		appliedModel.add(new Integer[] {counter, temperatureRun.getCurrentTemp()});
		for (Integer[] modelStep : maischModel) {
			//turn heater on
			switchHeater(true);
			while (temperatureRun.getCurrentTemp() < modelStep[0]) {
				tempLog.add(new Integer[] {counter, temperatureRun.getCurrentTemp()});
				counter++;
				sleep(500); //and keep warming
			}
			//turn heater off
			switchHeater(false);
			appliedModel.add(new Integer[] {tempLog.size(), modelStep[0]});
			appliedModel.add(new Integer[] {tempLog.size() + modelStep[1], modelStep[0]});
			for (int i = 0; i < modelStep[1]; i++) {
				int currentTemp = temperatureRun.getCurrentTemp();
				tempLog.add(new Integer[] {counter, currentTemp});

				if (currentTemp < modelStep[0] - thresHold) {
					//turn heater on
					switchHeater(true);
				} else if (currentTemp > modelStep[0] + thresHold) {
					//turn heater off
					switchHeater(false);
				}
				counter++;
				sleep(500);
			}
		}
	}

	public List<Integer[]> getTempLog() {
		return tempLog;
	}

	public List<Integer[]> getAppliedModel() {
		return appliedModel;
	}

	public List<Object[]> getSwitchLog() {
		return switchLog;
	}

	public int getCurrentTemperature() {
		return temperatureRun.getCurrentTemp();
	}

	public boolean isHeaterOn() {
		return heaterOn;
	}

	private void sleep(int milliseonds) {
		try {
			Thread.sleep(milliseonds);
		} catch (InterruptedException e) {
			System.out.println("Something went wrong while sleeping...");
			e.printStackTrace();
		}
	}

	private void switchHeater(boolean enable) {
		if (heaterOn != enable) {
			heaterOn = enable;
			switchLog.add(new Object[] {counter, enable});
		}
	}

}
