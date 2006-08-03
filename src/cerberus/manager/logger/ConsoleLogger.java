/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.GeneralManager;
import cerberus.manager.LoggerManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author java
 *
 */
public class ConsoleLogger extends AbstractManagerImpl implements LoggerManager {

	protected short sLogLevel = 0;
	
	/**
	 * @param setGeneralManager
	 */
	public ConsoleLogger(GeneralManager setGeneralManager) {
		super(setGeneralManager, 
				GeneralManager.iUniqueId_TypeOffset_Logger );
	}

	protected void logMessage( final String msg ) {	
		if ( sLogLevel > 0 ) {
			System.err.println( msg );
			return;
		}
		System.out.println( msg );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.LoggerManager#logMsg(java.lang.String)
	 */
	public void logMsg(String info) {
		logMessage( info );
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.LoggerManager#logMsg(java.lang.String, short)
	 */
	public void logMsg(String info, short logLevel) {
		sLogLevel = logLevel;
		logMessage( info );
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
	
	/**
	 * @see cerberus.manager.GeneralManager#hasItem(int)
	 */
	public boolean hasItem(final int iItemId) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode hasItem()");
	}

	/**
	 * @see cerberus.manager.GeneralManager#getItem(int)
	 */
	public Object getItem( final int iItemId) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode getItem()");
	}
	
	/**
	 * @see cerberus.manager.GeneralManager#size()
	 */
	public int size() {
		return 0;
	}

	/**
	 * @see cerberus.manager.GeneralManager#getManagerType()
	 */
	public ManagerObjectType getManagerType() {
		return ManagerObjectType.LOGGER;
	}
	

	/**
	 * @see cerberus.manager.GeneralManager#registerItem(java.lang.Object, int, cerberus.manager.type.ManagerObjectType)
	 */
	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type )
	{
		throw new CerberusRuntimeException("LOGGER: does not support this methode registerItem()");
	}
	
	
	
	/**
	 * @see cerberus.manager.GeneralManager#unregisterItem(int, cerberus.manager.type.ManagerObjectType)
	 */
	public boolean unregisterItem( final int iItemId, final ManagerObjectType type  ) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode unregisterItem()");
	}

}
