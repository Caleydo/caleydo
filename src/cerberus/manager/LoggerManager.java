/**
 * 
 */
package cerberus.manager;

/**
 * @author java
 *
 */
public interface LoggerManager {

	public void logMsg( String info );
	
	public void logMsg( String info, short logLevel );
	
	public void setLogLevel( short level );
	
	public short getLogLevel();
	
	public void flushLog();
	
	public boolean isLogFlushed();
}
