/**
 * 
 */
package org.caleydo.core.manager.logger;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager;
//import org.caleydo.core.manager.ILoggerManager.LoggerType;
//import org.caleydo.core.manager.ISingelton;
import org.caleydo.core.manager.base.AManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * 
 * @see org.caleydo.core.manager.ILoggerManager
 * @see org.caleydo.core.manager.IGeneralManager
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AConsoleLogger 
	extends AManager 
	implements ILoggerManager {

	protected LoggerType logLevel = LoggerType.ERROR;
	
	protected LoggerType systemLogLevel = LoggerType.VERBOSE;

	protected boolean bIsLogFlushed = false;
	
	/**
	 * @param setGeneralManager
	 */
	protected AConsoleLogger(final IGeneralManager setGeneralManager,
			final int iUniqueId_type_offset) {
		super(setGeneralManager,
				IGeneralManager.iUniqueId_TypeOffset_Logger,
				ManagerType.LOGGER );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.ILoggerManager#setLogLevel(short)
	 */
	public final void setLogLevel(LoggerType level) {
		logLevel = level;
		logMsg("Set Logger level to [" + level + "]", LoggerType.VERBOSE );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.ILoggerManager#getLogLevel()
	 */
	public final LoggerType getLogLevel() {
		return logLevel;
	}

	/** 
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see org.caleydo.core.manager.ILoggerManager#isLogFlushed()
	 */
	public final boolean isLogFlushed() {
		return bIsLogFlushed;
	}

	/**
	 * Do nothing, since all messages are loged any way.
	 * 
	 * @see org.caleydo.core.manager.ILoggerManager#setSystemLogLevel(short)
	 */
	public final void setSystemLogLevel(LoggerType systemLogLevel)
	{
		this.systemLogLevel = systemLogLevel;
		logMsg("Set logger systemLogLevel to [" + 
				systemLogLevel.name() + "] ==> [" + 
				systemLogLevel + "] (up to this level messages will be visible)", LoggerType.VERBOSE );
	}

	/**
	 * Do nothing, since all messages are loged any way.
	 * 
	 * @see org.caleydo.core.manager.ILoggerManager#getSystemLogLevel()
	 */
	public final LoggerType getSystemLogLevel()
	{
		return this.systemLogLevel;
	}
	
	/**
	 * @see org.caleydo.core.manager.IGeneralManager#hasItem(int)
	 */
	public final boolean hasItem(final int iItemId) {
		throw new CaleydoRuntimeException("LOGGER: does not support this method hasItem()");
	}

	/**
	 * @see org.caleydo.core.manager.IGeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		throw new CaleydoRuntimeException("LOGGER: does not support this method getItem()");
	}
	
	/**
	 * @see org.caleydo.core.manager.IGeneralManager#size()
	 */
	public int size() {
		return 0;
	}
	

	/**
	 * @see org.caleydo.core.manager.IGeneralManager#registerItem(java.lang.Object, int, org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public final boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type )
	{
		throw new CaleydoRuntimeException("LOGGER: does not support this method registerItem()");
	}
	
	
	
	/**
	 * @see org.caleydo.core.manager.IGeneralManager#unregisterItem(int, org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public final boolean unregisterItem( final int iItemId, final ManagerObjectType type  ) {
		throw new CaleydoRuntimeException("LOGGER: does not support this method unregisterItem()");
	}

}
