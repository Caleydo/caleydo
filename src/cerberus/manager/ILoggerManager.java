/**
 * 
 */
package cerberus.manager;

import cerberus.manager.IGeneralManager;

/**
 * @author java
 *
 */
public interface ILoggerManager 
extends IGeneralManager 
{
	public static enum LoggerType {
		
//		ERROR_ONLY	(  0, "  error: ", "show error only" ),
//		STATUS		( 10, " status: ", "show errors and status messages" ),
//		VERBOSE		( 20, "verbose: ", "show error, status and verbose messages" ),
//		FULL		( 99, "full___: ", "show any message");
		
		ERROR_ONLY	(  0, "E: ", "show error only" ),
		STATUS		( 10, "S: ", "show errors and status messages" ),
		VERBOSE		( 20, "V: ", "show error, status and verbose messages" ),
		FULL		( 99, "F: ", "show any message");
		
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
	}

	public void logMsg( final String info );
	
	public void logMsg( final String info, final LoggerType logLevel );
	
	public void setLogLevel( final LoggerType level );
	
	/**
	 * Set log level to be compared with current loglevel.
	 * If current log-level is bigger than system log level no
	 * output is visible.
	 * 
	 * @param systemLogLevel
	 */
	public void setSystemLogLevel( LoggerType systemLogLevel );
	
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
