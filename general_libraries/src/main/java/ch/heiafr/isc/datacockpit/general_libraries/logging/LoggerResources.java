package ch.heiafr.isc.datacockpit.general_libraries.logging;

public class LoggerResources {

	static final ClassLoader loader = LoggerResources.class.getClassLoader();

	public static final String DEFAULT_PROPERTIES_JAVANCO = loader.getResource("javanco.properties.xml").getFile();
	public static final String DEFAULT_PROPERTIES_LOG4J = loader.getResource("log4j.properties").getFile();

}
