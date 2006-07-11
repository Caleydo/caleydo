/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.util.exception;

/**
 * Exceptions from Memento objects. "Memento" Desing Pattern.
 * 
 * @see GeneralMemento
 * @author Michael Kalkusch
 *
 */
public class PrometheusMementoException extends PrometheusRuntimeException {

	final static long serialVersionUID = 7200;
	
	/**
	 * 
	 */
	public PrometheusMementoException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 */
	public PrometheusMementoException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusMementoException(String s, Throwable ex) {
		super(s, ex);
		// TODO Auto-generated constructor stub
	}

}
