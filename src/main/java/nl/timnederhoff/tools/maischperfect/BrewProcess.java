package nl.timnederhoff.tools.maischperfect;

import nl.timnederhoff.tools.maischperfect.model.highcharts.Label;
import nl.timnederhoff.tools.maischperfect.model.highcharts.PlotLine;
import nl.timnederhoff.tools.maischperfect.model.highcharts.Point;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BrewProcess implements Runnable {
	private List<Integer[]> maischModel;
	private List<Point> appliedModel;
	private int thresHold = 2;
	private List<Point> tempLog;
	private List<PlotLine> switchLog;
	private boolean heaterOn;
	private TemperatureRun temperatureRun;
	private Thread brewProcessThread;
	private Instant startDateTime;

	public BrewProcess(List<Integer[]> maischModel) {
		this.maischModel = maischModel;
		brewProcessThread = new Thread(this, "my runnable thread");
	}

	@Override
	public void run() {
		System.out.println("Process started...");
		tempLog = new ArrayList<>();
		switchLog = new ArrayList<>();
		temperatureRun = new TemperatureRun();
		appliedModel = new ArrayList<>();
		startDateTime = Instant.now();
		appliedModel.add(new Point(elapsedTime().toMillis(), temperatureRun.getCurrentTemp()));
		for (Integer[] modelStep : maischModel) {
			//turn heater on
			switchHeater(true);
			while (temperatureRun.getCurrentTemp() < modelStep[0]) {
				tempLog.add(new Point(elapsedTime().toMillis(), temperatureRun.getCurrentTemp()));
				System.out.println("[heating] elapsed time: " + elapsedTime() + ", temp: " + temperatureRun.getCurrentTemp());
				sleep(500);
			}
			//turn heater off
			switchHeater(false);
			appliedModel.add(new Point(elapsedTime().toMillis(), modelStep[0]));
			appliedModel.add(new Point(elapsedTime().plus(modelStep[1], ChronoUnit.SECONDS).toMillis(), modelStep[0]));

			Instant endTime = startDateTime.plus(elapsedTime()).plus(modelStep[1], ChronoUnit.SECONDS);
			while (Instant.now().isBefore(endTime)) {
				int currentTemp = temperatureRun.getCurrentTemp();
				tempLog.add(new Point(elapsedTime().toMillis(), currentTemp));
				System.out.println("[waiting] elapsed time: " + elapsedTime() + ", temp: " + temperatureRun.getCurrentTemp());
				if (currentTemp < modelStep[0] - thresHold) {
					//turn heater on
					switchHeater(true);
				} else if (currentTemp > modelStep[0] + thresHold) {
					//turn heater off
					switchHeater(false);
				}
				sleep(500);
			}
		}
	}

	public void start() {
		brewProcessThread.start();
	}

	public boolean isEnded() {
		return brewProcessThread.getState() == Thread.State.TERMINATED;
	}

	public List<Point> getTempLog() {
		return tempLog;
	}

	public List<Point> getTempLog(int fromPoint) {
		for (int i = 0; i < tempLog.size(); i++) {
			if (tempLog.get(i).getX() > fromPoint) {
				return tempLog.subList(i , tempLog.size());
			}
		}
		return new ArrayList<>();
	}

	public List<Point> getAppliedModel() {
		return appliedModel;
	}

	public List<Point> getAppliedModel(int fromPoint) {
		for (int i =0; i < appliedModel.size(); i++) {
			if (appliedModel.get(i).getX() > fromPoint) {
				return appliedModel.subList(i, appliedModel.size());
			}
		}
		return new ArrayList<>();
	}

	public List<PlotLine> getSwitchLog() {
		return switchLog;
	}

	public List<PlotLine> getSwitchLog(int fromPoint) {
		for (int i = 0; i < switchLog.size(); i++) {
			if (switchLog.get(i).getValue() > fromPoint) {
				return switchLog.subList(i, switchLog.size());
			}
		}
		return new ArrayList<>();
	}

	public int getCurrentTemperature() {
		if (null == temperatureRun) {
			return 0;
		}
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
			temperatureRun.setHeaterOn(enable);
			switchLog.add(new PlotLine(
					enable ? "red" : "blue",
					"solid",
					elapsedTime().toMillis(),
					2,
					new Label(enable ? "Heater ON" : "Heater OFF")));
		}
	}

	private Duration elapsedTime() {
		return Duration.between(startDateTime, Instant.now());
	}

}
