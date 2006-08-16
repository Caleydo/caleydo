/**
 * 
 */
package cerberus.manager;

import cerberus.manager.IGeneralManager;

/**
 * @author java
 *
 */
public interface ILoggerManager 
extends IGeneralManager 
{

	public void logMsg( String info );
	
	public void logMsg( String info, short logLevel );
	
	public void setLogLevel( short level );
	
	public short getLogLevel();
	
	public void flushLog();
	
	public boolean isLogFlushed();
}
