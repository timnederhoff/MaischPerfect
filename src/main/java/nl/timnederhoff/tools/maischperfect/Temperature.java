package nl.timnederhoff.tools.maischperfect;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Master;

public class Temperature implements Runnable{

	private TemperatureSensor temperatureSensor;
	private static double currentTemperature = 0d;
	private static boolean instantiated = false;
	private static Temperature instance;
	private Thread temperatureThread;

	private Temperature() {
		temperatureSensor = (TemperatureSensor) new W1Master().getDevices(TmpDS18B20DeviceType.FAMILY_CODE).get(0);
		temperatureThread = new Thread(this, "temperature thread");
		temperatureThread.start();
		instantiated = true;
	}

	@Override
	public void run() {
		try {
			while (true) {
				currentTemperature = temperatureSensor.getTemperature();
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Temperature get() {
		if (!instantiated) {
			instance = new Temperature();
		}
		return instance;
	}

	public static double currentTemperature() {
		return currentTemperature;
	}
}
