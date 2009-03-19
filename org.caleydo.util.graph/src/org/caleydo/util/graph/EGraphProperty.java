/**
 * 
 */
package org.caleydo.util.graph;

/**
 * Define graph properties like HAS_CYCLES, HAS_NO_CYCLES, IS_TREE, HAS_SUB_GRAPH
 * 
 * @author Michael Kalkusch
 * @see org.caleydo.util.graph.EGraphItemHierarchy
 * @see org.caleydo.util.graph.EGraphItemProperty
 * @see org.caleydo.util.graph.EGraphItemKind
 */
public enum EGraphProperty {

	HAS_NO_CYCLES(),
	IS_TREE(),
	HAS_SUB_GRAPH();

	private EGraphProperty() {
		/** no local variables necessary yet! */
	}

	/**
	 * Test if a graph is cyclic.
	 * 
	 * @param prop
	 *            property to be tested
	 * @return TRUE if cycles exist, FLASE if the graph is acyclic
	 */
	public static final boolean isCyclic(EGraphProperty prop) {
		return prop == EGraphProperty.HAS_NO_CYCLES ? false : true;
	}
}
