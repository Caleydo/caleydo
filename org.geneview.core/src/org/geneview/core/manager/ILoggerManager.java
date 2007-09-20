/**
 * 
 */
package org.geneview.core.manager;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.util.IGeneViewDefaultType;

/**
 * @author Michael Kalkusch
 *
 */
public interface ILoggerManager 
extends IGeneralManager 
{
	public static enum LoggerType 
	implements IGeneViewDefaultType <LoggerType> {
		
		ERROR			(  0, "E: ", "show error only" ),
		MINOR_ERROR		(  5, "M: ", "show any errors" ),
		MINOR_ERROR_XML	(  9, "X: ", "show xml errors" ),
		STATUS			( 10, "S: ", "show errors and status messages" ),		
		TRANSITION		( 20, "T: ", "show errors, status messages and module transitions"),
		VERBOSE			( 30, "V: ", "show error, status and verbose messages" ),
		VERBOSE_EXTRA	( 40, "W: ", "show error, status and long verbose messages" ),
		FULL			( 99, "F: ", "show any message");
		
		private short level;
		
		private String description;
		
		private String logMessage;
		
		private LoggerType( int level,
				String sLogMessage,
				String sDescription ){
			
			this.level = (short) level;
			this.description = sDescription;
			this.logMessage = sLogMessage;
		}
		
		public short getLevel() {
			return level;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getLogMessage() {
			return logMessage;
		}
		
		/**
		 * Tests if a log message shall be printed.
		 * if test.getLevel() is less or equal than the this.getLevel() TRUE is rreturned.
		 * 
		 * @param test LoggerType to be tested
		 * @return TRUE, if LoggerTpye
		 */
		public boolean showLog( final LoggerType test ) {
			if ( test.getLevel() <= this.level ) 
			{
				return true;
			}
			return false;
		}
		
		public String toString() {
			return logMessage;
		}

		/*
		 * (non-Javadoc)
		 * @see org.geneview.core.util.IGeneViewDefaultType#getTypeDefault()
		 */
		public LoggerType getTypeDefault() {

			return LoggerType.ERROR;
		}
	}

	//public void logMsg( final String info );
	
	public void logMsg( final String info, final LoggerType logLevel );
	
	public void setLogLevel( final LoggerType level );
	
	/**
	 * Set log level to be compared with current loglevel.
	 * If current log-level is bigger than system log level no
	 * output is visible.
	 * 
	 * @param systemLogLevel
	 */
	public void setSystemLogLevel( final LoggerType systemLogLevel );
	
	/**
	 * Get current system log level.
	 * 
	 * @return system log level
	 */
	public LoggerType getSystemLogLevel();
	
	public LoggerType getLogLevel();
	
	public void flushLog();
	
	public boolean isLogFlushed();
}
