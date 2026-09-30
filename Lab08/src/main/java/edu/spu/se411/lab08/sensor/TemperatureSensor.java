package edu.spu.se411.lab08.sensor;

/**
 * A temperature sensor. Everything except the quantity and the unit is
 * inherited.
 */
public class TemperatureSensor extends Sensor {

	public TemperatureSensor(String name) {
		super(name);
	}

	@Override
	public String getQuantity() {
		return "Temperature";
	}

	@Override
	public String getUnit() {
		return "C";
	}
}
