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
 * Exception for "Command" Desing Pattern
 * 
 * @see prometheus.command.CommandInterface
 * @author Michael Kalkusch
 *
 */
public class PrometheusCommandException extends PrometheusRuntimeException {

	final static long serialVersionUID = 7200;
	
	/**
	 * 
	 */
	public PrometheusCommandException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 */
	public PrometheusCommandException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusCommandException(String s, Throwable ex) {
		super(s, ex);
		// TODO Auto-generated constructor stub
	}

}
