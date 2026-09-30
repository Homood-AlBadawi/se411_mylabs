package edu.spu.se411.lab08.sensor;

/**
 * A humidity sensor. Same inheritance story as {@link TemperatureSensor} - the
 * whole observer mechanism and the reading/notify rule come from above.
 */
public class HumiditySensor extends Sensor {

	public HumiditySensor(String name) {
		super(name);
	}

	@Override
	public String getQuantity() {
		return "Humidity";
	}

	@Override
	public String getUnit() {
		return "%";
	}
}
