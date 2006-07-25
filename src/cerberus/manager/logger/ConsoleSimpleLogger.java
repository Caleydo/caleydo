/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.GeneralManager;
import cerberus.manager.LoggerManager;
import cerberus.manager.base.AbstractManagerImpl;

/**
 * @author java
 *
 */
public class ConsoleSimpleLogger extends 
	AbstractManagerImpl implements LoggerManager {

	protected short sLogLevel = 0;
	
	/**
	 * @param setGeneralManager
	 */
	public ConsoleSimpleLogger(GeneralManager setGeneralManager) {
		super(setGeneralManager);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.LoggerManager#logMsg(java.lang.String)
	 */
	public void logMsg(String info) {
		System.out.println( info );
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.LoggerManager#logMsg(java.lang.String, short)
	 */
	public void logMsg(String info, short logLevel) {
		sLogLevel = logLevel;
		System.out.println( info );
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.LoggerManager#setLogLevel(short)
	 */
	public void setLogLevel(short level) {
		sLogLevel = level;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.LoggerManager#getLogLevel()
	 */
	public short getLogLevel() {
		return sLogLevel;
	}

	/**
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see cerberus.manager.LoggerManager#flushLog()
	 */
	public void flushLog() {
		assert false : "logger is always flushed, since it prints to system.out";
	}

	/** 
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see cerberus.manager.LoggerManager#isLogFlushed()
	 */
	public boolean isLogFlushed() {
		return true;
	}

}
