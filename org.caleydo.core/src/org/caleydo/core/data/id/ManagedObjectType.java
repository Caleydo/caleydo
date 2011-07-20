package org.caleydo.core.data.id;

/**
 * Types of managed objects
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum ManagedObjectType {
	STORAGE(10),
	STORAGE_NUMERICAL(11),
	STORAGE_NOMINAL(12),

	VIRTUAL_ARRAY(13),
	SELECTION(14),
	SET(15),
	GROUP(16),

	VIEW(20),
	VIEW_GL_CANVAS(21),
	VIEW_SWT_BROWSER_GENERAL(26),
	VIEW_SWT_BROWSER_GENOME(23),
	VIEW_SWT_TABULAR_DATA_VIEWER(99),
	VIEW_SWT_JOGL_CONTAINER(99),
	VIEW_SWT_COLLAB(99),

	GL_CANVAS(28),
	GL_VIEW(29),

	GL_CONTEXT_MENUE(49),

	GUI_SWT_WINDOW(50),
	GUI_SWT_NATIVE_WIDGET(51),
	GUI_SWT_EMBEDDED_JOGL_WIDGET(52),

	CMD_QUEUE(54),
	COMMAND(56),

	PATHWAY(58),
	PATHWAY_ELEMENT(59),
	PATHWAY_VERTEX(60),
	PATHWAY_VERTEX_REP(61),
	PATHWAY_EDGE(62),
	PATHWAY_EDGE_REP(63),

	EVENT_PUBLISHER(64),
	EVENT_MEDIATOR(65),

	REMOTE_LEVEL_ELEMENT(66),

	GRAPH(67),
	GRAPH_ITEM(68),

	DIMENSION_GROUP(69),
	DIMENSION_GROUP_SPACER(70),

	/**
	 * Type for ids used to connect different elements that have different ids.
	 */
	CONNECTION(85),

	HIERARCHYGRAPH(90);

	private int iIdPrefix;

	/**
	 * Constructor.
	 */
	private ManagedObjectType(final int iIdPrefix) {
		this.iIdPrefix = iIdPrefix;
	}

	public int getIdPrefix() {
		return iIdPrefix;
	}
}
