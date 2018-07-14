package com.singhpra.masti.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Loggeable {
    
    default Logger logger() {
        return LogManager.getLogger(getClass());
    }
}
