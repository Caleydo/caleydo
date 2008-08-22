/**
 * 
 */
package org.caleydo.util.graph.algorithm;

import java.util.Iterator;
import java.util.List;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

/**
 * Search for adjacencies in a graph starting with an item.
 * 
 * @author Michael Kalkusch
 */
public class GraphVisitorGetLinkedGraphItems
	extends AGraphVisitorSearch
	implements IGraphVisitorSearch
{

	/**
	 * 
	 */
	public GraphVisitorGetLinkedGraphItems(final IGraphItem itemSource, final int iSearchDepth)
	{
		super(itemSource, iSearchDepth);
	}

	protected List<IGraphItem> getSearchResultFromGraphItem(IGraphItem item)
	{
		List<IGraphItem> buffer = item.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);

		List<IGraphItem> resultBuffer = null;

		Iterator<IGraphItem> iter = buffer.iterator();

		while (iter.hasNext())
		{
			List<IGraphItem> listAllChildren_fromParent = iter.next().getAllItemsByProp(
					EGraphItemProperty.ALIAS_CHILD);

			if (!listAllChildren_fromParent.isEmpty())
			{
				if (resultBuffer == null)
				{
					resultBuffer = listAllChildren_fromParent;
				}
				else
				{
					resultBuffer.addAll(listAllChildren_fromParent);
				}
			}
		}

		return resultBuffer;
	}

	@Override
	public final boolean init()
	{
		return false;
	}

	@Override
	public final void search()
	{
		/** algorithm is done inside method getSearchResult() */
	}

	@Override
	public final void wipeTemporalDataFromGraph()
	{
		/** not temporal data was created inside the graphs. */
	}

	@Override
	public List<IGraphItem> getSearchResult()
	{

		if (this.iSearchDepth == 1)
		{
			return getSearchResultFromGraphItem(this.itemSource);
		}

		assert false : "not implemented yet";

		return null;
	}

	@Override
	public List<List<IGraphItem>> getSearchResultDepthOrdered()
	{

		assert false : "not implemented yet";

		return null;
	}

}
