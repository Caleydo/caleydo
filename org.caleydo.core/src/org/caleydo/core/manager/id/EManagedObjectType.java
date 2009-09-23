package org.caleydo.core.manager.id;

/**
 * Types of managed objects
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public enum EManagedObjectType {
	STORAGE(10),
	STORAGE_NUMERICAL(11),
	STORAGE_NOMINAL(12),

	VIRTUAL_ARRAY(13),
	SELECTION(14),

	SET(15),

	VIEW(20),
	VIEW_GL_CANVAS(21),
	VIEW_SWT_GLYPH_DATAEXPORT(24),
	VIEW_SWT_GLYPH_MAPPINGCONFIGURATION(25),
	VIEW_SWT_BROWSER_GENERAL(26),
	VIEW_SWT_BROWSER_GENOME(23),
	VIEW_SWT_TABULAR_DATA_VIEWER(99),
	VIEW_SWT_JOGL_CONTAINER(99),
	VIEW_SWT_COLLAB(99),

	GL_CANVAS(28),
	GL_EVENT_LISTENER(29),
	GL_PATHWAY(30),
	GL_PARALLEL_COORDINATES(31),
	GL_HEAT_MAP(32),
	GL_GLYPH(33),
	GL_REMOTE_RENDERING(34),
	GL_GLYPH_SLIDER(35),
	GL_HIER_HEAT_MAP(36),
	GL_CELL_LOCALIZATION(37),
	GL_SELECTION_PANEL(38),
	GL_RADIAL_HIERARCHY(39),
	GL_HYPERBOLIC(40),
	GL_HISTOGRAM(41),
	GL_DENDOGRAM(42),
	GL_DATA_FLIPPER(43),
	GL_TISSUE(44),

	GL_CONTEXT_MENUE(49),

	GUI_SWT_WINDOW(50),
	GUI_SWT_NATIVE_WIDGET(51),
	GUI_SWT_EMBEDDED_JOGL_WIDGET(52),
	GUI_SWT_EMBEDDED_JGRAPH_WIDGET(53),

	CMD_QUEUE(54),
	CMD_QUEUE_RUN(55),
	COMMAND(56),

	MEMENTO(57),

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

	/**
	 * Type for ids used to connect different elements that have different ids.
	 */
	CONNECTION(85),

	HIERARCHYGRAPH(90);

	private int iIdPrefix;

	/**
	 * Constructor.
	 */
	private EManagedObjectType(final int iIdPrefix) {
		this.iIdPrefix = iIdPrefix;
	}

	public int getIdPrefix() {
		return iIdPrefix;
	}
}
