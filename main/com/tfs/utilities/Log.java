package com.tfs.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Log {
	private static Logger _logger;
	private static final String fileName = "UpdateTfsStatus";
	private static final String dateAndTimeFormat = "MM-dd-yyyy_hh.mm.ss";
	private static final String logProperttFilePath = "log4j.xml";
	
	static {
		try {
			_logger = createLogger();
			SimpleDateFormat sdf = new SimpleDateFormat(dateAndTimeFormat);
			String dateTime = sdf.format(new Date());
			String FileName = fileName + "-" + dateTime + ".log";
			System.setProperty("logFile.name", FileName);
			File file = new File("log\\" + FileName);

			if (file.createNewFile()) {
				 URL u = Log.class.getClassLoader().getResource(logProperttFilePath);
				 DOMConfigurator.configure(u);
//				DOMConfigurator.configure(ClassLoader.getSystemResource(logProperttFilePath).getPath());
			}
		} catch (IOException | FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("IO Exception in static method of Logger Class. "
					+ e.getMessage());
			System.exit(-1);
		}

	}
	/**
	 * This method creates instance of the Logger class coming from log4j jar by
	 * implementing a singelton
	 * 
	 * @return _logger - new instance if no instance exist else an existing
	 *         instance if the method is invoked previously
	 */
	public static Logger createLogger() {
		if (_logger == null) {
			_logger = LogManager.getLogger(Log.class.getName());
			return _logger;
		} else
			return _logger;
	}

	public static void info(String message) {

		_logger.info("**********" + message + "**********");

	}

	public static void msg(String message) {

		_logger.info(message);
	}

	public static void warn(String message) {

		_logger.warn("**********" + message + "**********");

	}

	public static void error(String message) {

		_logger.error("**********" + message + "**********");

	}

	public static void fatal(String message) {

		_logger.fatal("**********" + message + "**********");

	}

	public static void debug(String message) {

		_logger.debug("**********" + message + "**********");

	}

}
