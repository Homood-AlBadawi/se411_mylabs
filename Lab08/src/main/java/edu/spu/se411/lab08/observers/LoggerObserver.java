package edu.spu.se411.lab08.observers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.spu.se411.lab08.sensor.Sensor;

/**
 * Second observer: writes every reading to the log file instead of the screen.
 *
 * The same event reaches two observers that do completely different things, and
 * neither sensor knows either of them exists. That is the pattern working.
 */
public class LoggerObserver extends SensorObserver {

	private static final Logger logger = LoggerFactory.getLogger(LoggerObserver.class);

	public LoggerObserver(String observerName) {
		super(observerName);
	}

	@Override
	protected void onReading(Sensor sensor) {
		logger.info("[{}] {} {} = {} {}",
				getObserverName(), sensor.getName(), sensor.getQuantity(),
				String.format("%.2f", sensor.getReading()), sensor.getUnit());
	}
}
