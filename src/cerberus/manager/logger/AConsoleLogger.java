/**
 * 
 */
package cerberus.manager.logger;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.ISingelton;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AConsoleLogger 
	extends AAbstractManager 
	implements ILoggerManager, IGeneralManager {

	protected LoggerType logLevel = LoggerType.ERROR_ONLY;
	
	protected LoggerType systemLogLevel = LoggerType.VERBOSE;

	protected boolean bIsLogFlushed = false;
	
	/**
	 * @param setGeneralManager
	 */
	public AConsoleLogger(final IGeneralManager setGeneralManager,
			final int iUniqueId_type_offset) {
		super(setGeneralManager,
				IGeneralManager.iUniqueId_TypeOffset_Logger,
				ManagerType.LOGGER );
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#setLogLevel(short)
	 */
	public final void setLogLevel(LoggerType level) {
		logLevel = level;
		logMsg("Set Logger level to [" + level + "]", LoggerType.VERBOSE );
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.ILoggerManager#getLogLevel()
	 */
	public final LoggerType getLogLevel() {
		return logLevel;
	}

	/** 
	 * Since the logger prints to system.out it is always flushed.
	 * 
	 * @see cerberus.manager.ILoggerManager#isLogFlushed()
	 */
	public final boolean isLogFlushed() {
		return bIsLogFlushed;
	}

	/**
	 * Do nothing, since all messages are loged any way.
	 * 
	 * @see cerberus.manager.ILoggerManager#setSystemLogLevel(short)
	 */
	public final void setSystemLogLevel(LoggerType systemLogLevel)
	{
		this.systemLogLevel = systemLogLevel;
		logMsg("Set Logger systemLogLevel to [" + systemLogLevel + "]", LoggerType.VERBOSE );
	}

	/**
	 * Do nothing, since all messages are loged any way.
	 * 
	 * @see cerberus.manager.ILoggerManager#getSystemLogLevel()
	 */
	public final LoggerType getSystemLogLevel()
	{
		return this.systemLogLevel;
	}
	
	/**
	 * @see cerberus.manager.IGeneralManager#hasItem(int)
	 */
	public final boolean hasItem(final int iItemId) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode hasItem()");
	}

	/**
	 * @see cerberus.manager.IGeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode getItem()");
	}
	
	/**
	 * @see cerberus.manager.IGeneralManager#size()
	 */
	public int size() {
		return 0;
	}
	

	/**
	 * @see cerberus.manager.IGeneralManager#registerItem(java.lang.Object, int, cerberus.manager.type.ManagerObjectType)
	 */
	public final boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type )
	{
		throw new CerberusRuntimeException("LOGGER: does not support this methode registerItem()");
	}
	
	
	
	/**
	 * @see cerberus.manager.IGeneralManager#unregisterItem(int, cerberus.manager.type.ManagerObjectType)
	 */
	public final boolean unregisterItem( final int iItemId, final ManagerObjectType type  ) {
		throw new CerberusRuntimeException("LOGGER: does not support this methode unregisterItem()");
	}

}
