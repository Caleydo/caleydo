/*
 * Created on Jul 19, 2003
 *
 */
package org.caleydo.core.util.exception;

import java.rmi.RemoteException;

/**
 * Exception from RMI calls.
 * 
 * @author Michael Kalkusch
 *
 */
public class CaleydoRmiException 
	extends RemoteException {

	final static long serialVersionUID = 7700;
	
	/**
	 * 
	 */
	public CaleydoRmiException() {
		super();		
	}

	/**
	 * @param s
	 */
	public CaleydoRmiException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public CaleydoRmiException(String s, Throwable ex) {
		super(s, ex);
	}

}
