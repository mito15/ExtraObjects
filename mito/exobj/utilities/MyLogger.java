package com.mito.exobj.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mito.exobj.Main;

public class MyLogger {

	public static Logger logger = LogManager.getLogger("exobj");

	public static void trace(String msg) {
		logger.trace(msg);
	}

	public static void info(String msg) {
		if (Main.debug)
			logger.info(msg);
	}

	public static void warn(String msg) {
		logger.warn(msg);
	}

	public static void info() {
		logger.info("message");
	}

	public static void info(double... list) {
		String s = "";
		for (int n = 0; n < list.length; n++) {
			s = s + " value" + n + " = " + list[n] + " |";
		}
		logger.info(s);
	}

}
