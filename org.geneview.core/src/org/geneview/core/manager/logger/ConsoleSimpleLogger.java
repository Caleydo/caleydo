/**
 * 
 */
package org.geneview.core.manager.logger;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.logger.AConsoleLogger;

/**
 * 
 * @see org.geneview.core.manager.ILoggerManager
 * @see org.geneview.core.manager.IGeneralManager
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
	 * @see org.geneview.core.manager.ILoggerManager#logMsg(Stringt, short)
	 */
	public void logMsg(String info, LoggerType logLevel) {
		System.out.println( logLevel + ": " + info );
	}

	/**
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see org.geneview.core.manager.ILoggerManager#flushLog()
	 */
	public void flushLog() {
		assert false : "logger is always flushed, since it prints to system.out";
	}

}
