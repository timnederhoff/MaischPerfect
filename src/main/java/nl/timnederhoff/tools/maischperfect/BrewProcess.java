package nl.timnederhoff.tools.maischperfect;

import com.pi4j.io.gpio.*;
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
	private double threshold;
	private List<Point> tempLog;
	private List<PlotLine> switchLog;
	private Thread brewProcessThread;
	private Instant startDateTime;
	private int measureInterval;
	private double slope;
	private double currentTemp;

	private GpioPinDigitalOutput heaterSwitch;

	public BrewProcess(List<Integer[]> maischModel, int measureInterval, double threshold) {
		this.measureInterval = measureInterval;
		this.maischModel = maischModel;
		this.threshold = threshold;
		GpioController gpio = GpioFactory.getInstance();

		heaterSwitch = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25,   // PIN NUMBER
				"Heater Switch",           // PIN FRIENDLY NAME (optional)
				PinState.LOW);      // PIN STARTUP STATE (optional)

		brewProcessThread = new Thread(this, "brew process thread");
	}

	@Override
	public void run() {
		System.out.println("Process started...");
		tempLog = new ArrayList<>();
		switchLog = new ArrayList<>();
		appliedModel = appliedModel(maischModel, 20, 1);
		startDateTime = Instant.now();
		currentTemp = Temperature.get().currentTemperature();
		appliedModel.set(0, new Point(elapsedTime().toMillis(), currentTemp));
		for (Integer[] modelStep : maischModel) {
			//turn heater on
			switchHeater(true);
			double beginTemp = currentTemp;
			Instant beginTime = Instant.now();
			while ((currentTemp = Temperature.get().currentTemperature()) < modelStep[0]) {
				tempLog.add(new Point(elapsedTime().toMillis(), currentTemp));
				double tempDiff = currentTemp - beginTemp;
				System.out.println("tempdiff " + tempDiff);
				double timeDiff = Duration.between(beginTime, Instant.now()).toMillis()/60000;
				System.out.println("timediff " + timeDiff);
				slope = tempDiff / timeDiff;
				System.out.println("slope: " + slope);
				sleep(measureInterval);
			}
			//turn heater off
			switchHeater(false);
			appliedModel.set((maischModel.indexOf(modelStep) * 2) + 1, new Point(elapsedTime().toMillis(), modelStep[0]));
			appliedModel.set((maischModel.indexOf(modelStep) * 2) + 2, new Point(elapsedTime().plus(modelStep[1], ChronoUnit.MINUTES).toMillis(), modelStep[0]));

			Instant endTime = startDateTime.plus(elapsedTime()).plus(modelStep[1], ChronoUnit.MINUTES);
			while (Instant.now().isBefore(endTime)) {
				currentTemp = Temperature.get().currentTemperature();
				tempLog.add(new Point(elapsedTime().toMillis(), currentTemp));
				if (currentTemp < modelStep[0] - threshold) {
					//turn heater on
					switchHeater(true);
				} else if (currentTemp > modelStep[0] + threshold) {
					//turn heater off
					switchHeater(false);
				}
				sleep(measureInterval);
			}
		}
		switchHeater(false);
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
		synchronized (tempLog) {
			for (int i = 0; i < tempLog.size(); i++) {
				if (tempLog.get(i).getX() > fromPoint) {
					return tempLog.subList(i , tempLog.size());
				}
			}
			return new ArrayList<>();
		}
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

	public double getCurrentTemperature() {
		return currentTemp;
	}

	public boolean isHeaterOn() {
		return heaterSwitch.isHigh();
	}

	public double getSlope() {
		return (double) Math.round(slope * 100)/100d;
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
		if (heaterSwitch.isHigh() != enable) {
			heaterSwitch.toggle();
			addToSwitchLog(enable);
		}
	}

	private void addToSwitchLog(boolean enable) {
		switchLog.add(new PlotLine(
				enable ? "red" : "blue",
				"solid",
				elapsedTime().toMillis(),
				2,
				new Label(enable ? "Heater ON" : "Heater OFF")));
	}

	private Duration elapsedTime() {
		return Duration.between(startDateTime, Instant.now());
	}

	private List<Point> appliedModel(List<Integer[]> maischModel, int startTemperature, double slope) {
		long x = 0L;
		int y = startTemperature;
		List<Point> appliedModelInit = new ArrayList<>();
		appliedModelInit.add(new Point(x, startTemperature)); //assuming stortwater is 20 degrees
		for (Integer[] step : maischModel) {
			x += (long) ((step[0]-y)/slope) * 60 * 1000;
			y = step[0];
			appliedModelInit.add(new Point(x, y));
			x += step[1] * 60 * 1000;
			appliedModelInit.add(new Point(x, y));
		}
		return appliedModelInit;
	}

}
