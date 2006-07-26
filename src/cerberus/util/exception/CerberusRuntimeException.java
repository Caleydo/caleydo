/*
 * Created on Jul 19, 2003
 *
 */
package cerberus.util.exception;

import java.lang.RuntimeException;

/**
 * Base class for all RuntimeException.
 *  
 * @author Michael Kalkusch
 *
 */
public class CerberusRuntimeException 
	extends RuntimeException {

	final static long serialVersionUID = 7000;
	
	protected final CerberusExceptionType type;
	
	/**
	 * 
	 */
	public CerberusRuntimeException() {
		super();	
		
		type = CerberusExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 */
	public CerberusRuntimeException(String s) {
		super(s);
		
		type = CerberusExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 */
	public CerberusRuntimeException(String s, final CerberusExceptionType type) {
		super(s);
		this.type = type;
	}
	
	
	/**
	 * @param s
	 * @param ex
	 */
	public CerberusRuntimeException(String s, Throwable ex) {
		super(s, ex);
		
		type = CerberusExceptionType.RUNTIME;
	}
	
	
	/**
	 * @param s
	 * @param ex
	 */
	public CerberusRuntimeException(String s, Throwable ex, final CerberusExceptionType type) {
		super(s, ex);
		
		this.type = type;
	}

}
