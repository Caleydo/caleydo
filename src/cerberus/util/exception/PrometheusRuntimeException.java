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
public class PrometheusRuntimeException 
	extends RuntimeException {

	final static long serialVersionUID = 7000;
	
	/**
	 * 
	 */
	public PrometheusRuntimeException() {
		super();		
	}

	/**
	 * @param s
	 */
	public PrometheusRuntimeException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusRuntimeException(String s, Throwable ex) {
		super(s, ex);
	}

}
