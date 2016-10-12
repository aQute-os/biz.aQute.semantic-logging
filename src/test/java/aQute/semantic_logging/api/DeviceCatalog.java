package aQute.semantic_logging.api;

import aQute.semantic_logging.api.Catalog;

interface DeviceCatalog extends Catalog {
	TRACE measurement(String sensor, int temperatur);
	INFO comparing(String sensor, int temperatur, int maxTemperature);
	WARN tempTooHigh(String sensor, int temperature);
	ERROR fire(String sensor, String location);
}
