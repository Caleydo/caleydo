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
public class CerberusRmiException 
	extends RemoteException {

	final static long serialVersionUID = 7700;
	
	/**
	 * 
	 */
	public CerberusRmiException() {
		super();		
	}

	/**
	 * @param s
	 */
	public CerberusRmiException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public CerberusRmiException(String s, Throwable ex) {
		super(s, ex);
	}

}
