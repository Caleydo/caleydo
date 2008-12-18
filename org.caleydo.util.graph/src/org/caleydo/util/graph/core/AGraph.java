package org.caleydo.util.graph.core;

import java.util.HashMap;

import org.caleydo.util.graph.EGraphProperty;
import org.caleydo.util.graph.IGraph;

/**
 * Base Class for all IGraph implementations handling EGraphProperty and
 * GraphTypeId
 * 
 * @author Michael Kalkusch
 */
public abstract class AGraph
	implements IGraph
{

	private static final long serialVersionUID = 1L;

	/**
	 * initial size of org.caleydo.util.graph.core.AGraph#hashGraphProperties
	 * 
	 * @see org.caleydo.util.graph.core.AGraph#hashGraphProperties
	 */
	private static final int iInitialSizeProperties = 3;

	/**
	 * @see org.caleydo.util.graph.core.AGraph#getId()
	 * @see org.caleydo.util.graph.core.AGraph#setId(int)
	 */
	private int iGraphId = 0;

	/**
	 * HashMap for EGraphProperty.
	 * 
	 * @see org.caleydo.util.graph.core.AGraph#hasGraphProperty(EGraphProperty)
	 * @see org.caleydo.util.graph.core.AGraph#setGraphProperty(EGraphProperty,
	 *      boolean)
	 * @see org.caleydo.util.graph.IGraph#setGraphProperty(EGraphProperty,
	 *      boolean)
	 * @see org.caleydo.util.graph.IGraph#hasGraphProperty(EGraphProperty)
	 */
	private HashMap<EGraphProperty, Boolean> hashGraphProperties;

	/**
	 * 
	 */
	protected AGraph(final int id)
	{
		hashGraphProperties = new HashMap<EGraphProperty, Boolean>(iInitialSizeProperties);

		this.iGraphId = id;
	}

	@Override
	public final int getId()
	{
		return iGraphId;
	}

	@Override
	public final boolean hasGraphProperty(EGraphProperty test)
	{
		return hashGraphProperties.containsKey(test);
	}

	public final void setGraphProperty(final EGraphProperty prop, final boolean value)
	{
		if (value)
		{
			hashGraphProperties.put(prop, new Boolean(value));
			return;
		}
		hashGraphProperties.remove(prop);
	}

	@Override
	public final void setId(int type)
	{
		if (type >= 0)
		{
			iGraphId = type;
			return;
		}

		assert false : "setTypeId( " + type + " ) is invalid; value >= 0";
	}

	/**
	 * Empty method by definition. For details see
	 * org.caleydo.util.graph.IGraphComponent#disposeItem().
	 * 
	 * @see org.caleydo.util.graph.IGraphComponent#disposeItem()
	 */
	public final void disposeItem()
	{
		/**
		 * Graph does not dispose other objects; only IGraphItem need to dispose
		 * objects
		 */
	}

}
