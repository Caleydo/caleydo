/**
 * 
 */
package org.geneview.core.manager.logger;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager;
//import org.geneview.core.manager.ILoggerManager.LoggerType;
//import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.base.AAbstractManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * 
 * @see cerberus.manager.ILoggerManager
 * @see cerberus.manager.IGeneralManager
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AConsoleLogger 
	extends AAbstractManager 
	implements ILoggerManager {

	protected LoggerType logLevel = LoggerType.ERROR_ONLY;
	
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
		logMsg("Set logger systemLogLevel to [" + 
				systemLogLevel.name() + "] ==> [" + 
				systemLogLevel + "] (up to this level messages will be visible)", LoggerType.VERBOSE );
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
		throw new GeneViewRuntimeException("LOGGER: does not support this method hasItem()");
	}

	/**
	 * @see cerberus.manager.IGeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		throw new GeneViewRuntimeException("LOGGER: does not support this method getItem()");
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
		throw new GeneViewRuntimeException("LOGGER: does not support this method registerItem()");
	}
	
	
	
	/**
	 * @see cerberus.manager.IGeneralManager#unregisterItem(int, cerberus.manager.type.ManagerObjectType)
	 */
	public final boolean unregisterItem( final int iItemId, final ManagerObjectType type  ) {
		throw new GeneViewRuntimeException("LOGGER: does not support this method unregisterItem()");
	}

}
