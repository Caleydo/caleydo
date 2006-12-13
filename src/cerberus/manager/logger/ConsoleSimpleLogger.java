/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.logger.AConsoleLogger;

/**
 * @author java
 *
 */
public class ConsoleSimpleLogger 
	extends AConsoleLogger
	implements ILoggerManager, IGeneralManager {

	
	/**
	 * @param setGeneralManager
	 */
	public ConsoleSimpleLogger(IGeneralManager setGeneralManager) {
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
	public void logMsg(String info) {
		System.out.println( info );
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#logMsg(java.lang.String, short)
	 */
	public void logMsg(String info, LoggerType logLevel) {
		System.out.println( logLevel + ": " + info );
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
