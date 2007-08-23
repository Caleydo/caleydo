/**
 * 
 */
package org.geneview.graph.core;

import java.util.HashMap;

import org.geneview.graph.EGraphProperty;
import org.geneview.graph.IGraph;

/**
 * Base Class for all IGraph implementations handling EGraphProperty and GraphTypeId
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGraph implements IGraph {

	/**
	 * initial size of org.geneview.graph.core.AGraph#hashGraphProperties 
	 * 
	 * @see org.geneview.graph.core.AGraph#hashGraphProperties
	 */
	private static final int iInitialSizeProperties = 3;
	
	/**
	 * @see org.geneview.graph.core.AGraph#getId()
	 * @see org.geneview.graph.core.AGraph#setId(int)
	 */
	private int iGraphId = 0;
	
	/**
	 * HashMap for EGraphProperty.
	 * 
	 * @see org.geneview.graph.core.AGraph#hasGraphProperty(EGraphProperty)
	 * @see org.geneview.graph.core.AGraph#setGraphProperty(EGraphProperty, boolean)
	 * @see org.geneview.graph.IGraph#setGraphProperty(EGraphProperty, boolean)
	 * @see org.geneview.graph.IGraph#hasGraphProperty(EGraphProperty)
	 */
	private HashMap <EGraphProperty,Boolean> hashGraphProperties;
	
	/**
	 * 
	 */
	protected AGraph() {
		hashGraphProperties = new HashMap <EGraphProperty,Boolean> (iInitialSizeProperties);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#getTypeId()
	 */
	public final int getId() {
		return iGraphId;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#hasGraphProperty(org.geneview.graph.EGraphProperty)
	 */
	public final boolean hasGraphProperty(EGraphProperty test) {
		return hashGraphProperties.containsKey(test);
	}
	
	public final void setGraphProperty(final EGraphProperty prop, final boolean value) {
		if ( value ) {
			hashGraphProperties.put(prop, new Boolean(value));
			return;
		}
		hashGraphProperties.remove(prop);
	}


	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#setTypeId(int)
	 */
	public final void setId(int type) {
		if (type >= 0) {
			iGraphId = type;
			return;
		}
		
		assert false : "setTypeId( " + type + " ) is invalid; value >= 0";
	}


	/**
	 * Empty method by definition.
	 * For details see org.geneview.graph.IGraphComponent#disposeItem().
	 * 
	 * @see org.geneview.graph.IGraphComponent#disposeItem()
	 */
	public final void disposeItem() {
		/** Graph does not dispose other objects; only IGraphItem need to dispose objects */
	}

}
