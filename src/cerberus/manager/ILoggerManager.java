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
		
		ERROR_ONLY	( 0,"" ),
		STATUS( 10, "" ),
		VERBOSE( 20 ,"" ),
		FULL( 99, "");
		
		private short level;
		
		private String description;
		
		private LoggerType( int level,
				String description) {
			this.level = (short) level;
			this.description = description;
		}
		
		public short getLevel() {
			return level;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	

	public void logMsg( String info );
	
	public void logMsg( String info, short logLevel );
	
	public void setLogLevel( short level );
	
	/**
	 * Set log level to be compared with current loglevel.
	 * If current log-level is bigger than system log level no
	 * output is visible.
	 * 
	 * @param systemLogLevel
	 */
	public void setSystemLogLevel( short systemLogLevel );
	
	/**
	 * Get current system log level.
	 * 
	 * @return system log level
	 */
	public short getSystemLogLevel();
	
	public short getLogLevel();
	
	public void flushLog();
	
	public boolean isLogFlushed();
}
