package nl.timnederhoff.tools.maischperfect;

import java.util.ArrayList;
import java.util.List;

public class BrewProcess implements Runnable {
	private int[][] maischModel;
	private List<int[]> appliedModel;
	private int thresHold = 3;
	private List<Integer> tempLog;
	private List<int[]> switchLog;
	private int counter;
	private boolean heaterOn;
	private TemperatureRun temperatureRun;

	public BrewProcess(int[][] maischModel) {
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
		appliedModel.add(new int[] {counter,temperatureRun.getCurrentTemp()});
		for (int targetTemp[] : maischModel) {
			//turn heater on
			switchHeater(1);
			while (temperatureRun.getCurrentTemp() < targetTemp[1]) {
				tempLog.add(temperatureRun.getCurrentTemp());
				counter++;
				sleep(500); //and keep warming
			}
			//turn heater off
			switchHeater(0);
			appliedModel.add(new int[] {tempLog.size(), targetTemp[1]});
			appliedModel.add(new int[] {tempLog.size()+targetTemp[0], targetTemp[1]});
			for (int i = 0; i < targetTemp[0]; i++) {
				int currentTemp = temperatureRun.getCurrentTemp();
				tempLog.add(currentTemp);
				if (currentTemp < targetTemp[1] - thresHold) {
					//turn heater on
					switchHeater(1);
				} else if (currentTemp > targetTemp[1] + thresHold) {
					//turn heater off
					switchHeater(0);
				}
				sleep(500);
			}
		}
	}

	public List<Integer> getTempLog() {
		return tempLog;
	}

	public List<int[]> getAppliedModel() {
		return appliedModel;
	}

	public List<int[]> getSwitchLog() {
		return switchLog;
	}

	public int getCurrentTemperature() {
		return temperatureRun.getCurrentTemp();
	}

	public boolean isHeaterOn() {
		return heaterOn;
	}

	@Override
	public String toString() {
		return "BrewProcess{" +
				"tempLog=" + tempLog +
				", switchLog=" + switchLog +
				'}';
	}

	private void sleep(int milliseonds) {
		try {
			Thread.sleep(milliseonds);
		} catch (InterruptedException e) {
			System.out.println("Something went wrong while sleeping...");
			e.printStackTrace();
		}
	}

	private void switchHeater(int enable) {
		if (enable == 0) heaterOn = false;
		if (enable == 1) heaterOn = true;
		switchLog.add(new int[]{counter, enable});
	}

}
