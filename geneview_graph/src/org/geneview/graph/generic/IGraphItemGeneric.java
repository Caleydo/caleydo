package org.geneview.graph.generic;

import java.util.Collection;

import org.geneview.graph.generic.EGraphItemHierarchy;

public interface IGraphItemGeneric <GraphParent, GraphItem, GraphData> {

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	public void addGraphItemByType(GraphItem item, EGraphItemHierarchy type);

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	public boolean removeGraphItemByType(GraphItem item,
			EGraphItemHierarchy type);

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	public Collection<GraphItem> getAllGraphItemByType(GraphItem item,
			EGraphItemHierarchy type);

	public boolean containsGraphItemByType(GraphItem item,
			EGraphItemHierarchy type);

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	public boolean addGraphDataById(GraphData data, int id);

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	public void setGraphDataById(GraphData data, int id);

	public GraphData getGraphDataById(int id);

	public GraphData removeGraphDataById(int id);

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#getAllData()
	 */
	public Collection<GraphData> getAllGraphDataCopy();

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#removeAllData()
	 */
	public void removeAllData();

	public void removeAllGraphItem();

	public boolean addParentGraph(GraphParent parent);

	public void setParentGraph(GraphParent parent);

	public boolean removeParentGraph(GraphParent parent);

	public boolean containsParentGraph(GraphParent parent);

}