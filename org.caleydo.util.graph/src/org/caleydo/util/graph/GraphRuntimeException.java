/**
 * 
 */
package org.caleydo.util.graph;

/**
 * RuntimeException for graphs.
 * 
 * @see org.caleydo.util.graph.IGraph
 * @author Michael Kalkusch
 */
public final class GraphRuntimeException
	extends RuntimeException {

	/**
	 * id for serialization.
	 */
	private static final long serialVersionUID = 5290845775175543483L;

	/**
	 * @param message
	 */
	public GraphRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GraphRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GraphRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
