/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author java
 *
 */
public class ConsoleLogger 
extends AAbstractManager 
implements ILoggerManager {

	private LoggerType systemLogLevel = LoggerType.ERROR_ONLY;
	
	protected LoggerType logLevel = LoggerType.VERBOSE;
	
	/**
	 * @param setGeneralManager
	 */
	public ConsoleLogger(IGeneralManager setGeneralManager) {
		super(setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Logger );
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

	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#setLogLevel(short)
	 */
	public void setLogLevel( final LoggerType level) {
		this.logLevel = level;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#getLogLevel()
	 */
	public LoggerType getLogLevel() {
		return logLevel;
	}
	
	/**
	 * 
	 */
	public void setSystemLogLevel( final LoggerType systemLogLevel) {		
		this.systemLogLevel= systemLogLevel;
	}
	
	public LoggerType getSystemLogLevel() {		
		return this.systemLogLevel;
	}

	/**
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see cerberus.manager.ILoggerManager#flushLog()
	 */
	public void flushLog() {
		assert false : "logger is always flushed, since it prints to system.out";
	}

	/** 
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see cerberus.manager.ILoggerManager#isLogFlushed()
	 */
	public boolean isLogFlushed() {
		return true;
	}
	
	/**
	 * @see cerberus.manager.IGeneralManager#hasItem(int)
	 */
	public boolean hasItem( final int iItemId ) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode hasItem()");
	}

	/**
	 * @see cerberus.manager.IGeneralManager#getItem(int)
	 */
	public Object getItem( final int iItemId ) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode getItem()");
	}
	
	/**
	 * @see cerberus.manager.IGeneralManager#size()
	 */
	public int size() {
		return 0;
	}

	/**
	 * @see cerberus.manager.IGeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {
		return ManagerObjectType.LOGGER;
	}
	

	/**
	 * @see cerberus.manager.IGeneralManager#registerItem(java.lang.Object, int, cerberus.manager.type.ManagerObjectType)
	 */
	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type )
	{
		throw new CerberusRuntimeException("LOGGER: does not support this methode registerItem()");
	}
	
	
	
	/**
	 * @see cerberus.manager.IGeneralManager#unregisterItem(int, cerberus.manager.type.ManagerObjectType)
	 */
	public boolean unregisterItem( final int iItemId, 
			final ManagerObjectType type  ) {
		
		throw new CerberusRuntimeException("LOGGER: does not support this methode unregisterItem()");
	}

}
