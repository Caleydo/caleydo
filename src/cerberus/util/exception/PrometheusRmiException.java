/*
 * Created on Jul 19, 2003
 *
 */
package cerberus.util.exception;

import java.rmi.RemoteException;

/**
 * Exception from RMI calls.
 * 
 * @author Michael Kalkusch
 *
 */
public class PrometheusRmiException 
	extends RemoteException {

	final static long serialVersionUID = 7700;
	
	/**
	 * 
	 */
	public PrometheusRmiException() {
		super();		
	}

	/**
	 * @param s
	 */
	public PrometheusRmiException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusRmiException(String s, Throwable ex) {
		super(s, ex);
	}

}
