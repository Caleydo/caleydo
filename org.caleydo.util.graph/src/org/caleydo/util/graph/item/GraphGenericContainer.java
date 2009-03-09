package org.caleydo.util.graph.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.caleydo.util.graph.GraphRuntimeException;
import org.caleydo.util.graph.IGraphComponent;

/**
 * General generic container for IGraph and IGraphItem objects. GraphEnum has to be an Enumeration of type
 * EGraphItemProperty or EGraphItemHierarchy. Note: <GraphComponent extends IGraphComponent,..> ensures, that
 * generic "GraphComponent" extends IGraphComponent; this is required for the method disposeItem().
 * 
 * @see org.caleydo.util.graph.EGraphItemProperty
 * @see org.caleydo.util.graph.EGraphItemHierarchy
 * @author Michael Kalkusch
 */
public class GraphGenericContainer<GraphComponent extends IGraphComponent, GraphEnum>
	implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * initial size for type2ArrayList.
	 * 
	 * @see org.caleydo.util.graph.item.GraphGenericContainer#type2ArrayList
	 */
	private static final int iInitialSizeHierarchyArray = 4;

	// private ArrayList <GraphEnum> keyList;

	protected HashMap<GraphEnum, ArrayList<GraphComponent>> type2ArrayList;

	/**
	 * Constructor.
	 * 
	 * @param colGraphEnum
	 *          List of all Enumeration that will be used; Skip type NONE or GRAPH_NONE
	 * @param iInitialSizeGraphComponents
	 *          specify initial size of ArrayList<GraphComponent>
	 */
	public GraphGenericContainer(List<GraphEnum> colGraphEnum, int iInitialSizeGraphComponents) {

		assert colGraphEnum != null : "Can not create a GraphGenericContainer with no GraphEnum elements and empty List";

		/* count number of Enum's to be registered */
		int iCountGraphEnumItems = colGraphEnum.size();
		assert iCountGraphEnumItems > 0 : "can not create a GraphGenericContainer with no GraphEnum elements";

		/* create new HashMap */
		type2ArrayList = new HashMap<GraphEnum, ArrayList<GraphComponent>>(iCountGraphEnumItems);

		/* iterator for all GraphEnum items ... */
		Iterator<GraphEnum> iter = colGraphEnum.iterator();

		/* fill hierarchy HashMap ... */
		while (iter.hasNext()) {
			type2ArrayList.put(iter.next(), new ArrayList<GraphComponent>(iInitialSizeHierarchyArray));
		}

		// keyList = new ArrayList <GraphEnum> (iCountGraphEnumItems);
	}

	/**
	 * Same as
	 * {@link org.caleydo.util.graph.item.GraphGenericContainer#addGraphComponent(IGraphComponent, Object)} but
	 * it will be checked if the element is already added. In this case an assertion will be triggered.
	 */
	public void addGraphComponentChecked(GraphComponent item, final GraphEnum key) {

		/* add to hierarchy... */
		ArrayList<GraphComponent> arrayBuffer = type2ArrayList.get(key);

		if (arrayBuffer == null) {
			throw new GraphRuntimeException("unsupported type " + type2ArrayList.toString());
		}

		if (!arrayBuffer.contains(item)) {
			arrayBuffer.add(item);
		}
		else {
			assert false : "Try to add existing element!";
			throw new GraphRuntimeException("unsupported type " + type2ArrayList.toString());
		}
	}

	/**
	 * @see org.caleydo.util.graph.item.GraphItem#addGraph(org.caleydo.util.graph.IGraph,
	 *      org.caleydo.util.graph.EGraphItemHierarchy)
	 * @see org.caleydo.util.graph.item.GraphItem#addItem(org.caleydo.util.graph.IGraphItem,
	 *      org.caleydo.util.graph.EGraphItemProperty)
	 */
	public void addGraphComponent(GraphComponent item, final GraphEnum key) {

		/* add to hierarchy... */
		ArrayList<GraphComponent> arrayBuffer = type2ArrayList.get(key);

		if (arrayBuffer == null) {
			throw new GraphRuntimeException("unsupported type " + type2ArrayList.toString());
		}

		if (!arrayBuffer.contains(item)) {
			arrayBuffer.add(item);
		}
	}

	/**
	 * @see org.caleydo.util.graph.item.GraphItem#containsGraph(org.caleydo.util.graph.IGraph,
	 *      org.caleydo.util.graph.EGraphItemHierarchy)
	 * @see org.caleydo.util.graph.item.GraphItem#containsItem(org.caleydo.util.graph.IGraphItem,
	 *      org.caleydo.util.graph.EGraphItemProperty)
	 */
	public boolean containsGraphComponent(final GraphComponent item, final GraphEnum key) {

		try {
			return type2ArrayList.get(key).contains(item);
		}
		catch (NullPointerException npe) {
			/** Handle case if type2ArrayList.get(key) returns null */

			throw new GraphRuntimeException(" key: " + key.toString()
				+ " is not registered. Check type and Constructor GraphGenericContainer(..,..); ERROR= "
				+ npe.toString());
		}
	}

	/**
	 * @see org.caleydo.util.graph.item.GraphItem#containsGraph(org.caleydo.util.graph.IGraph,
	 *      org.caleydo.util.graph.EGraphItemHierarchy)
	 * @see org.caleydo.util.graph.item.GraphItem#containsItem(org.caleydo.util.graph.IGraphItem,
	 *      org.caleydo.util.graph.EGraphItemProperty)
	 */
	public boolean containsGraphComponentAtAll(final GraphComponent item) {
		Iterator<ArrayList<GraphComponent>> iter = type2ArrayList.values().iterator();

		while (iter.hasNext()) {
			if (iter.next().contains(item)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @see org.caleydo.util.graph.item.GraphItem#getAllGraphByType(org.caleydo.util.graph.EGraphItemHierarchy)
	 * @see org.caleydo.util.graph.item.GraphItem#getAllItemsByProp(org.caleydo.util.graph.EGraphItemProperty)
	 */
	public List<GraphComponent> getAllGraphComponent(final GraphEnum key) {

		if (key == null) {
			/** special case: return all GraphComponent from all ArrayList's */

			/** Calculate total size .. */
			int iTotalSize = 0;
			Iterator<ArrayList<GraphComponent>> bufferArrayListIter = type2ArrayList.values().iterator();
			while (bufferArrayListIter.hasNext()) {
				iTotalSize += bufferArrayListIter.next().size();
			}

			/** create result ArrayList */
			ArrayList<GraphComponent> resultList = new ArrayList<GraphComponent>(iTotalSize);

			/** fill result array .. */
			bufferArrayListIter = type2ArrayList.values().iterator();
			while (bufferArrayListIter.hasNext()) {
				resultList.addAll(bufferArrayListIter.next());
			}

			return resultList;
		}

		/** regular case */
		List<GraphComponent> buffer = type2ArrayList.get(key);

		return buffer != null ? buffer : new ArrayList<GraphComponent>(0);
	}

	/**
	 * @see org.caleydo.util.graph.item.GraphItem#removeGraph(org.caleydo.util.graph.IGraph,
	 *      org.caleydo.util.graph.EGraphItemHierarchy)
	 * @see org.caleydo.util.graph.item.GraphItem#removeItem(org.caleydo.util.graph.IGraphItem,
	 *      org.caleydo.util.graph.EGraphItemProperty)
	 */
	public boolean removeGraphComponent(final GraphComponent item, GraphEnum key) {

		if (key == null) {
			/** special case: remove GraphComponent from all ArrayList's */

			/** test if item will be removed from any ArrayList .. */
			boolean bRemovedFromAnyList = false;

			/** fill result array .. */
			Iterator<ArrayList<GraphComponent>> bufferArrayListIter = type2ArrayList.values().iterator();
			while (bufferArrayListIter.hasNext()) {
				if (bufferArrayListIter.next().remove(item)) {
					bRemovedFromAnyList = true;
				}
			}

			return bRemovedFromAnyList;
		}

		/** regular case */
		List<GraphComponent> buffer = type2ArrayList.get(key);

		return buffer != null ? buffer.remove(item) : false;
	}

	/**
	 * @see org.caleydo.util.graph.item.GraphItem#disposeItem()
	 */
	public void disposeItem() {
		/** fill result array .. */
		Iterator<ArrayList<GraphComponent>> bufferArrayListIter = type2ArrayList.values().iterator();
		while (bufferArrayListIter.hasNext()) {
			Iterator<GraphComponent> innerIter = bufferArrayListIter.next().iterator();

			while (innerIter.hasNext()) {
				((IGraphComponent) innerIter).disposeItem();
			}
		}
	}

	/**
	 * are there any items stored inside this object?
	 * 
	 * @return TRUE if at least one item is stored
	 */
	public boolean isEmpty() {
		Iterator<ArrayList<GraphComponent>> iter = type2ArrayList.values().iterator();

		while (iter.hasNext()) {
			if (!iter.next().isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
