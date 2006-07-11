/*
 * Created on Jul 19, 2003
 *
 */
package cerberus.util.exception;

//import java.lang.RuntimeException;
import cerberus.util.exception.PrometheusRuntimeException;

/**
 * Errors while proecessing data.
 * 
 * extends import java.lang.RuntimeException;
 * 
 * @author Michael Kalkusch
 *
 */
public class PrometheusDataHandlingException 
	extends PrometheusRuntimeException {

	final static long serialVersionUID = 7000;
	
	/**
	 * 
	 */
	public PrometheusDataHandlingException() {
		super();		
	}

	/**
	 * @param s
	 */
	public PrometheusDataHandlingException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusDataHandlingException(String s, Throwable ex) {
		super(s, ex);
	}

}
