/*
 * Created on Jul 19, 2003
 *
 */
package org.geneview.core.util.exception;

import java.lang.RuntimeException;

/**
 * Base class for all RuntimeException.
 *  
 * @author Michael Kalkusch
 *
 */
public class GeneViewRuntimeException 
	extends RuntimeException {

	final static long serialVersionUID = 7000;
	
	protected final GeneViewRuntimeExceptionType type;
	
	/**
	 * 
	 */
	public GeneViewRuntimeException() {
		super();	
		
		type = GeneViewRuntimeExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 */
	public GeneViewRuntimeException(String s) {
		super(s);
		
		type = GeneViewRuntimeExceptionType.RUNTIME;
	}

	/**
	 * @param s
	 */
	public GeneViewRuntimeException(String s, final GeneViewRuntimeExceptionType type) {
		super(s);
		this.type = type;
	}
	
	
	/**
	 * @param s
	 * @param ex
	 */
	public GeneViewRuntimeException(String s, Throwable ex) {
		super(s, ex);
		
		type = GeneViewRuntimeExceptionType.RUNTIME;
	}
	
	
	/**
	 * @param s
	 * @param ex
	 */
	public GeneViewRuntimeException(String s, Throwable ex, final GeneViewRuntimeExceptionType type) {
		super(s, ex);
		
		this.type = type;
	}

}
