/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.logger.AConsoleLogger;

/**
 * Hides message that are tagged as not important.
 * 
 * @author Michael Kalkusch
 *
 */
public class ConsoleLogger 
extends AConsoleLogger 
implements ILoggerManager {

	/**
	 * Specify LoggerType that will be send to System.err instead of System.out
	 */
	private static final LoggerType logLevelForSystemError = LoggerType.MINOR_ERROR;
	
	/**
	 * @param setGeneralManager
	 */
	public ConsoleLogger(IGeneralManager setGeneralManager) {
		super(setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Logger );
		
		/**
		 * Since log message are send to System.out log is always flushed
		 */
		bIsLogFlushed = true;
		
		logMsg("ConsoleLogger messages with log-level equal or less than " +
				logLevelForSystemError.name() + " will be sent to System.err.* ",LoggerType.VERBOSE);
	}


	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#logMsg(java.lang.String, short)
	 */
	public void logMsg( final String info, 
			final LoggerType useLogLevel) {
		
		if ( systemLogLevel.showLog( useLogLevel ) ) {
			
			if ( useLogLevel.compareTo(logLevelForSystemError) > 0 ) {
				System.out.println( useLogLevel + info );
				return;
			} else {
				System.err.println( useLogLevel + info );
			}
		}
	}

	/**
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see cerberus.manager.ILoggerManager#flushLog()
	 */
	public void flushLog() {
		assert false : "logger is always flushed, since it prints to system.out";
	}

}
