package aQute.semantic_logging.api;

import junit.framework.TestCase;


public class CatalogLoggerTest extends TestCase {

	final DeviceCatalog catalog = CatalogLogger.catalog(DeviceCatalog.class);
	
	public void testSimple() {
		
		catalog.measurement("room", 25);
		catalog.comparing("room", 25, 23);
		catalog.tempTooHigh("room", 25);
		catalog.measurement("room", 54);
		catalog.comparing("room", 54, 23);
		catalog.fire("room", "Floor 4,\toffice 45\n");

	}
	

}
