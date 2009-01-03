package org.caleydo.util.graph.core;

import java.util.Iterator;
import java.util.List;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.GraphRuntimeException;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.util.graph.item.GraphGenericContainer;

/**
 * Generic graph.
 * 
 * @author Michael Kalkusch
 */
public class Graph
	extends AGraph
{
	private static final int initialSizeItems = 10;

	private static final int initialSizeGraph = 3;

	protected GraphGenericContainer<IGraphItem, EGraphItemKind> items;

	protected GraphGenericContainer<IGraph, EGraphItemHierarchy> graphs;

	/**
	 * Constructor.
	 */
	public Graph(final int id)
	{

		super(id);

		/** create container for items */
		items = new GraphGenericContainer<IGraphItem, EGraphItemKind>(EGraphItemKind
				.getActiveItems(), Graph.initialSizeItems);

		/** create container for graphs */
		graphs = new GraphGenericContainer<IGraph, EGraphItemHierarchy>(EGraphItemHierarchy
				.getActiveItems(), Graph.initialSizeGraph);
	}

	@Override
	public boolean addGraph(IGraph graph, EGraphItemHierarchy type)
			throws GraphRuntimeException
	{

		graphs.addGraphComponent(graph, type);

		return true;
	}

	@Override
	public boolean addItem(IGraphItem item)
	{
		items.addGraphComponent(item, item.getGraphKind());

		return true;
	}

	@Override
	public void clearGraph()
	{

		/** items .. */
		Iterator<IGraphItem> iter = items.getAllGraphComponent(EGraphItemKind.NODE).iterator();

		while (iter.hasNext())
		{
			iter.next().removeGraph(this, null);
		}

		iter = items.getAllGraphComponent(EGraphItemKind.EDGE).iterator();

		while (iter.hasNext())
		{
			iter.next().removeGraph(this, null);
		}

		/** graphs .. */
		Iterator<IGraph> iterGraph = graphs.getAllGraphComponent(null).iterator();

		while (iterGraph.hasNext())
		{
			iterGraph.next().removeGraph(this, null);
		}
	}

	@Override
	public boolean containsItem(IGraphItem item)
	{
		return items.getAllGraphComponent(item.getGraphKind()).contains(item);
	}

	@Override
	public final boolean isEmpty()
	{
		if ((graphs.isEmpty()) && (items.isEmpty()))
		{
			return true;
		}
		return false;
	}

	@Override
	public void removeAllByKind(EGraphItemKind kind)
	{

		switch (kind)
		{
			case EDGE:
				break;

			case NODE:
				break;

			default:
				throw new GraphRuntimeException("unsupported type= " + kind.toString());
		}

		items.getAllGraphComponent(kind).clear();
	}

	@Override
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type)
	{
		return graphs.removeGraphComponent(graph, type);
	}

	@Override
	public boolean removeItem(IGraphItem item)
	{
		return items.removeGraphComponent(item, item.getGraphKind());
	}

	@Override
	public boolean updateItem(IGraphItem item)
	{
		EGraphItemKind itemNewKind = item.getGraphKind();
		if (items.containsGraphComponent(item, itemNewKind))
		{
			/** same kind as already registered. no update required */
			return true;

			/** do not use else but use early "return" and avoid "else" */
		}

		/** update is required! */

		/** add item to new list .. */
		items.addGraphComponent(item, itemNewKind);

		/** remove item from old list .. */
		if (itemNewKind.equals(EGraphItemKind.EDGE))
		{
			/** new kind is EDGE thus old kind was NODE */
			return items.removeGraphComponent(item, EGraphItemKind.NODE);

			/** do not use else but use early "return" and avoid "else" */
		}

		/** new kind is NODE thus old kind was EDGE */
		return items.removeGraphComponent(item, EGraphItemKind.EDGE);
	}

	@Override
	public List<IGraphItem> getAllItemsByKind(EGraphItemKind kind)
	{
		return items.getAllGraphComponent(kind);
	}

	@Override
	public List<IGraph> getAllGraphByType(EGraphItemHierarchy type)
	{
		return graphs.getAllGraphComponent(type);
	}
}
