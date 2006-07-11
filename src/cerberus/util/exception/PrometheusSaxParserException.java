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
 * Exception for "Observer" Desing Pattern
 * 
 * @see prometheus.app.mediator.MediatorItem
 * @author Michael Kalkusch
 *
 */
public class PrometheusSaxParserException
	extends PrometheusRuntimeException {

	final static long serialVersionUID = 7200;
	
	/**
	 * 
	 */
	public PrometheusSaxParserException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 */
	public PrometheusSaxParserException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusSaxParserException(String s, Throwable ex) {
		super(s, ex);
		// TODO Auto-generated constructor stub
	}

}
