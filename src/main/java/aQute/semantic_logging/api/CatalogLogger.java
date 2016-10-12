package aQute.semantic_logging.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;

import org.osgi.annotation.versioning.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Semantic Logger.
 *
 * This class can be used to front an SLF4J Logger. Messages are logged in the
 * format:
 * 
 * <pre>
 * 		methodName parameterName=value ...
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * [main] INFO de.sma.logging.api.TestCatalog1 - comparing sensor=room temperatur=25 maxTemperature=23
 * [main] WARN de.sma.logging.api.TestCatalog1 - tempTooHigh sensor=room temperature=25
 * [main] INFO de.sma.logging.api.TestCatalog1 - comparing sensor=room temperatur=54 maxTemperature=23
 * [main] ERROR de.sma.logging.api.TestCatalog1 - fire sensor=room location="Floor 4,\toffice 45\n"
 * </pre>
 * 
 * Values are escaped if they contain codes that could upset the log
 */

@ProviderType
public class CatalogLogger implements InvocationHandler {

	/**
	 * Create a semantic logger with the name of the catalog class.
	 * 
	 * @param catalog
	 *            the catalog and name for the Logger
	 * @param <T>
	 *            The catalog type
	 * @return a semantic logger
	 */
	public static <T extends Catalog> T catalog(Class<T> catalog) {
		return catalog(catalog, catalog);
	}

	/**
	 * Create a semantic logger with a specific Logger.
	 * 
	 * @param catalog
	 *            the catalog and name for the Logger
	 * @param logger
	 *            the logger to use
	 * @param <T>
	 *            The catalog type
	 * @return a semantic logger
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Catalog> T catalog(Class<T> catalog,
			Logger logger) {
		return (T) Proxy.newProxyInstance(catalog.getClassLoader(),
				new Class[] { catalog }, new CatalogLogger(catalog, logger));
	}

	/**
	 * Create a semantic logger with a specific class that is used for the
	 * logger name.
	 * 
	 * @param catalog
	 *            the catalog and name for the Logger
	 * @param type
	 *            the logger name to use
	 * @param <T>
	 *            The catalog type
	 * @return a semantic logger
	 */
	public static <T extends Catalog> T catalog(Class<T> catalog,
			Class<?> type) {
		Logger logger = LoggerFactory.getLogger(type);
		return catalog(catalog, logger);
	}

	/**********************************/

	private final Logger logger;

	private <T extends Catalog> CatalogLogger(Class<T> catalog, Logger logger) {
		this.logger = logger;
	}

	private static boolean isEnabled(Class<?> level, Logger logger) {

		if (Catalog.TRACE.class.isAssignableFrom(level)
				&& logger.isTraceEnabled())
			return true;

		if (Catalog.DEBUG.class.isAssignableFrom(level)
				&& logger.isDebugEnabled())
			return true;
		if (Catalog.INFO.class.isAssignableFrom(level)
				&& logger.isInfoEnabled())
			return true;
		if (Catalog.WARN.class.isAssignableFrom(level)
				&& logger.isWarnEnabled())
			return true;
		if (Catalog.ERROR.class.isAssignableFrom(level)
				&& logger.isErrorEnabled())
			return true;
		if (Catalog.FATAL.class.isAssignableFrom(level))
			return true;

		return false;
	}

	/**
	 * Private
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Class<?> level = method.getReturnType();
		if (!isEnabled(level, logger))
			return null;

		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());

		Parameter[] parameters = method.getParameters();
		for (int p = 0; p < parameters.length; p++) {
			sb.append(" ");
			sb.append(parameters[p].getName());
			sb.append("=");
			toString(sb, args[p]);
		}

		String message = sb.toString();

		if (level == Catalog.TRACE.class) {
			logger.trace(message);
		} else if (level == Catalog.DEBUG.class) {
			logger.debug(message);
		} else if (level == Catalog.INFO.class) {
			logger.info(message);
		} else if (level == Catalog.WARN.class) {
			logger.warn(message);
		} else if (level == Catalog.ERROR.class) {
			logger.error(message);
		} else if (level == Catalog.FATAL.class) {
			logger.error(message);
		}

		return null;
	}

	private void toString(StringBuilder sb, Object object) {
		String s = object.toString();
		int start = sb.length();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			boolean escape = c == '\\' || c == '=' || c == '\'' || c == '"';
			boolean control = c < ' ';

			if (Character.isWhitespace(c) || escape || control) {
				if (start >= 0) {
					sb.insert(start, '"');
					start = -1;
				}
				if (control) {
					switch (c) {
					case '\n':
						sb.append("\\n");
						break;
					case '\r':
						sb.append("\\r");
						break;
					case '\t':
						sb.append("\\t");
						break;
					case '\b':
						sb.append("\\b");
						break;
					case '\f':
						sb.append("\\f");
						break;
					default:
						String.format("\\u%04x", c);
						break;
					}
				} else if (escape) {
					sb.append("\\");
					sb.append(c);
				} else
					sb.append(c);

			} else
				sb.append(c);
		}
		if (start < 0)
			sb.append('"');
	}

}
