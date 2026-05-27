package com.hp.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerManager {
	
    //getLogger method to return the logger instance for the calling class
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }

}
