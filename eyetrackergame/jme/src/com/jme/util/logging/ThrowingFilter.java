package com.jme.util.logging;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ThrowingFilter implements Filter {

    /* (non-Javadoc)
     * @see java.util.logging.Filter#isLoggable(java.util.logging.LogRecord)
     */
    public boolean isLoggable(LogRecord record) {
        return Level.INFO.intValue() <= record.getLevel().intValue() || record.getMessage().equals("THROW");
    }
    
}