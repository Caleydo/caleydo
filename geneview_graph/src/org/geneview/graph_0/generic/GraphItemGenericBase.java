/**
 * 
 */
package org.geneview.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.geneview.graph.IGraph;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraphData;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.generic.IGraphContainerGeneric;

/**
 * @author java
 *
 */
public class GraphItemGenericBase <Parent,Item,Data>  implements IGraphContainerGeneric <Parent,Item,Data> {

	
	
	
	protected Collection<Item> listIncoming;
	protected Collection<Item> listOutgoing;

	public GraphItemGenericBase( final IGraph parentGraph ) {
		this(parentGraph, 5, 3, 3);
	}
	
	/**
	 * @param dataSize
	 * @param nodeSize
	 * @param edgeSize
	 */
	public GraphItemGenericBase(  final IGraph parentGraph, 
			int iDataSize, 
			int iNodeSize, 
			int iEdgeSize) {
		
		listData = new ArrayList <Data> (iDataSize);
		
		listIncoming = new ArrayList <Item> (iEdgeSize);
		listOutgoing = new ArrayList <Item> (iNodeSize);
		
		hashId2GraphData = new HashMap <Integer,Data> (iDataSize);
	}

	
	

	
	
//	public Collection<IGraph> getParentGraphs() {
//		Iterator <IGraph> iter = listParentGraph.iterator();		
//		Collection<IGraph> container = 
//			new ArrayList <IGraph> (listParentGraph.size());
//		
//		while (iter.hasNext()) {
//			container.add( iter.next() );
//		}
//		
//		return container;
//	}
	

}
