package org.caleydo.util.graph;

import java.util.List;

import org.caleydo.util.graph.item.IGraphDataHandler;

/**
 * Interface for all graph items. Could be nodes or edges.
 * 
 * @see org.caleydo.util.graph.EGraphItemHierarchy
 * @see org.caleydo.util.graph.EGraphItemProperty
 * @see org.caleydo.util.graph.EGraphItemKind
 * @see org.caleydo.util.graph.IGraph
 * @author Michael Kalkusch
 */
public interface IGraphItem
	extends IGraphDataHandler, IGraphItemHierarchy, IGraphComponent {

	/* ---------------- */

	/**
	 * @see org.caleydo.util.graph.IGraphItem#setGraphKind(EGraphItemKind)
	 * @return type of this GraphItem
	 */
	public EGraphItemKind getGraphKind();

	/**
	 * Set the type of this GraphItem.
	 * 
	 * @see org.caleydo.util.graph.IGraphItem#getGraphKind()
	 * @param kind
	 *            type for this GraphItem
	 */
	public void setGraphKind(EGraphItemKind kind);

	/* ---------------- */

	/**
	 * Get a List of IGraphItem with respect to their EGraphItemProperty. Note, if prop ==
	 * EGraphItemProperty.NONE or null all IGraphItem's are returned.
	 * 
	 * @param prop
	 *            specify, which IGraphItem's should be returned; if prop == EGraphItemProperty.NONE or null
	 *            all IGraphItem's are returned.
	 * @return collection of IGraphItems matching prop
	 */
	public List<IGraphItem> getAllItemsByProp(EGraphItemProperty prop);

	/**
	 * Adds a new IGraphItem with prop. Note, if prop == EGraphItemProperty.NONE or null a
	 * GraphRuntimeException is thrown
	 * 
	 * @see org.caleydo.util.graph.IGraphItem#addItemDoubleLinked(IGraphItem, EGraphItemProperty)
	 * @param item
	 *            new IGraphItem to be added
	 * @param prop
	 *            property linked to the added item
	 */
	public void addItem(IGraphItem item, EGraphItemProperty prop) throws GraphRuntimeException;

	/**
	 * Adds a new IGraphItem similar to addItem(IGraphItem, EGraphItemProperty) and also adds this item as
	 * reverse link to the new item. Revert entry is done for prop==(EGraphItemProperty.INCOMING,
	 * EGraphItemProperty.OUTGOING, EGraphItemProperty.ALIAS_PARENT,EGraphItemProperty.ALIAS_CHILD ) Note:
	 * EGraphItemProperty.NONE causes a GraphRuntimeException.
	 * 
	 * @see org.caleydo.util.graph.IGraphItem#addItem(IGraphItem, EGraphItemProperty)
	 * @param item
	 *            new IGraphItem to be added and to be linked reverse to this item;
	 * @param prop
	 *            property linked to the added item
	 * @throws GraphRuntimeException
	 */
	public void addItemDoubleLinked(IGraphItem item, EGraphItemProperty prop) throws GraphRuntimeException;

	/**
	 * Note, if prop == EGraphItemProperty.NONE or null IGraphItem is removed from any of the lists
	 * "INCOMING,OUTGOING" and "ALIAS"
	 * 
	 * @param item
	 *            IGraphItem to be removed
	 * @param prop
	 *            specify from which internal structure item should be removed
	 * @return TRUE if removal was successful, FLASE indicates that item was not registered
	 */
	public boolean removeItem(IGraphItem item, EGraphItemProperty prop);

	/**
	 * Test if IGraphItem item is contained depending on EGraphItemProperty. Note, if prop ==
	 * EGraphItemProperty.NONE or null all types are matched.
	 * 
	 * @param item
	 *            object to be tested
	 * @param prop
	 *            specify context; if prop == EGraphItemProperty.NONE or null all types are matched
	 * @return TURE indicates item is contained
	 */
	public boolean containsItem(IGraphItem item, EGraphItemProperty prop);

}
