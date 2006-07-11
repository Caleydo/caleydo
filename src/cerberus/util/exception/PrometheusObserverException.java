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
public class PrometheusObserverException
	extends PrometheusRuntimeException {

	final static long serialVersionUID = 7300;
	
	/**
	 * 
	 */
	public PrometheusObserverException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 */
	public PrometheusObserverException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusObserverException(String s, Throwable ex) {
		super(s, ex);
		// TODO Auto-generated constructor stub
	}

}
