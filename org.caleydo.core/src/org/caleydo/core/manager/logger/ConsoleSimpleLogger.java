/**
 * 
 */
package org.caleydo.core.manager.logger;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.logger.AConsoleLogger;

/**
 * 
 * @see org.caleydo.core.manager.ILoggerManager
 * @see org.caleydo.core.manager.IGeneralManager
 * 
 * @author Michael Kalkusch
 *
 */
public class ConsoleSimpleLogger 
	extends AConsoleLogger {

	
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
	 * @see org.caleydo.core.manager.ILoggerManager#logMsg(Stringt, short)
	 */
	public void logMsg(String info, LoggerType logLevel) {
		System.out.println( logLevel + ": " + info );
	}

	/**
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see org.caleydo.core.manager.ILoggerManager#flushLog()
	 */
	public void flushLog() {
		assert false : "logger is always flushed, since it prints to system.out";
	}

}
