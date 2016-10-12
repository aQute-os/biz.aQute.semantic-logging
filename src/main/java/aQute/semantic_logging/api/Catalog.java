package aQute.semantic_logging.api;

/**
 * A message catalog. Extend this interface and add methods
 * that return one of the level classes. You should use a method
 * name that is descriptive and also properly name the parameters.
 * These names appear in the log.
 * 
 * You can construct an instance with the {@link CatalogLogger#catalog(Class)} method.
 * This creates an SLF4J logger for you.
 *
 */
public interface Catalog {
	/**
	 * TRACE level
	 */
	class TRACE {}; 
	/**
	 * DEBUG level
	 */
	class DEBUG extends TRACE {}; 
	/**
	 * INFO level
	 */
	class INFO extends DEBUG {}; 
	/**
	 * WARN level
	 */
	class WARN extends INFO {}; 
	/**
	 * ERROR level
	 */
	class ERROR extends WARN {};
	/**
	 * FATAL level
	 */
	class FATAL extends ERROR {};

	/**
	 * AUDIT level
	 */
	class AUDIT extends ERROR {};
}
