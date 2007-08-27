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
public class GeneViewRmiException 
	extends RemoteException {

	final static long serialVersionUID = 7700;
	
	/**
	 * 
	 */
	public GeneViewRmiException() {
		super();		
	}

	/**
	 * @param s
	 */
	public GeneViewRmiException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public GeneViewRmiException(String s, Throwable ex) {
		super(s, ex);
	}

}
