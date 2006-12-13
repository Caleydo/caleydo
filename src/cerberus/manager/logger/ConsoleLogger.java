/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
//import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.logger.AConsoleLogger;

/**
 * @author java
 *
 */
public class ConsoleLogger 
extends AConsoleLogger 
implements ILoggerManager {

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
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#logMsg(java.lang.String)
	 */
	public void logMsg( final String info) {
		if ( systemLogLevel.showLog( logLevel ) ) {
			System.out.println( logLevel + info );
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#logMsg(java.lang.String, short)
	 */
	public void logMsg( final String info, 
			final LoggerType useLogLevel) {
		
		if ( systemLogLevel.showLog( useLogLevel ) ) {
			System.out.println( useLogLevel + info );
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
