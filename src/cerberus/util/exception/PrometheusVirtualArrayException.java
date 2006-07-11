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
 * @see VirtualArray
 * @see SelectionInterface
 * 
 * @author Michael Kalkusch
 *
 */
public class PrometheusVirtualArrayException 
	extends PrometheusRuntimeException {

	final static long serialVersionUID = 7100;
	
	/**
	 * 
	 */
	public PrometheusVirtualArrayException() {
		super();		
	}

	/**
	 * @param s
	 */
	public PrometheusVirtualArrayException(String s) {
		super(s);
	}

	/**
	 * @param s
	 * @param ex
	 */
	public PrometheusVirtualArrayException(String s, Throwable ex) {
		super(s, ex);
	}

}
