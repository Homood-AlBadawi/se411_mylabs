package edu.spu.se411.lab08;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.spu.se411.lab08.observer.Observer;
import edu.spu.se411.lab08.observers.DashboardObserver;
import edu.spu.se411.lab08.observers.LoggerObserver;
import edu.spu.se411.lab08.sensor.HumiditySensor;
import edu.spu.se411.lab08.sensor.Sensor;
import edu.spu.se411.lab08.sensor.TemperatureSensor;

/**
 * SE411 - Lab 08: Observer pattern.
 *
 * Run with: clean package exec:java
 */
public class App {

	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private static final int READING_COUNT = 10;
	private static final long INTERVAL_MS = 1000;

	public static void main(String[] args) {

		logger.info("Application is starting...");

		// ---- two subjects ----
		Sensor temp = new TemperatureSensor("TEMP-01");
		Sensor humidity = new HumiditySensor("HUM-01");

		// ---- two observers ----
		Observer dashboard = new DashboardObserver("dashboard");
		Observer fileLogger = new LoggerObserver("logger");

		// ---- register both observers on both sensors ----
		temp.register(dashboard);
		temp.register(fileLogger);
		humidity.register(dashboard);
		humidity.register(fileLogger);

		System.out.println("=== Simulating " + READING_COUNT + " rounds of sensor readings ===");
		System.out.printf("temp observers: %d, humidity observers: %d%n%n",
				temp.getObserverCount(), humidity.getObserverCount());

		simulate(temp, humidity);

		demonstrateUnregister(temp, dashboard);
		demonstrateCloning(temp);

		logger.info("Application is ending...");
	}

	// ==================================================================
	// The simulation loop from the handout
	// ==================================================================

	private static void simulate(Sensor temp, Sensor humidity) {
		Random random = new Random();

		for (int i = 0; i < READING_COUNT; i++) {
			System.out.println("round " + (i + 1) + ":");

			// Each setReading triggers notifyObservers(), so both observers
			// react to both sensors - four lines of output per round.
			temp.setReading(20 + random.nextDouble() * 15);
			humidity.setReading(40 + random.nextDouble() * 20);

			try {
				Thread.sleep(INTERVAL_MS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.error("Simulation interrupted", e);
				return;
			}
		}
		System.out.println();
	}

	// ==================================================================
	// Observers can leave
	// ==================================================================

	private static void demonstrateUnregister(Sensor temp, Observer dashboard) {
		System.out.println("=== Unregistering the dashboard from the temperature sensor ===");

		temp.unregister(dashboard);
		System.out.println("temp observers now: " + temp.getObserverCount()
				+ " (only the logger is left, so the next reading prints nothing here)");

		temp.setReading(99.9);
		System.out.println("reading set to 99.9 - it went to the log file only\n");
	}

	// ==================================================================
	// Requirement 3: the clone gets its own observers
	// ==================================================================

	private static void demonstrateCloning(Sensor original) {
		System.out.println("=== Cloning the temperature sensor ===");

		try {
			Sensor copy = original.clone();

			System.out.println("original observers: " + original.getObserverCount());
			System.out.println("clone observers   : " + copy.getObserverCount()
					+ "   <-- the clone starts with none");

			// Give the clone a different observer of its own.
			Observer cloneDashboard = new DashboardObserver("clone-dash");
			copy.register(cloneDashboard);

			System.out.println("\nafter registering one observer on the clone:");
			System.out.println("original observers: " + original.getObserverCount());
			System.out.println("clone observers   : " + copy.getObserverCount());

			System.out.println("\nsetting the CLONE's reading - only the clone's observer reacts:");
			copy.setReading(42.0);

			System.out.println("\nsetting the ORIGINAL's reading - the clone's observer stays silent:");
			original.setReading(11.0);

			System.out.println("\noriginal: " + original);
			System.out.println("clone   : " + copy);

		} catch (CloneNotSupportedException e) {
			logger.error("Sensor could not be cloned", e);
		}
	}
}
