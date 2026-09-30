package edu.spu.se411.lab08.observers;

import edu.spu.se411.lab08.sensor.Sensor;

/**
 * First observer: shows the reading on a console "dashboard".
 */
public class DashboardObserver extends SensorObserver {

	public DashboardObserver(String observerName) {
		super(observerName);
	}

	@Override
	protected void onReading(Sensor sensor) {
		System.out.printf("  [%-9s] %-14s %-12s %8.2f %s%n",
				getObserverName(), sensor.getName(), sensor.getQuantity(),
				sensor.getReading(), sensor.getUnit());
	}
}
