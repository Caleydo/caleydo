package org.caleydo.rcp;

import org.caleydo.core.serialize.ASerializedView;

/**
 * Enum for triggering view loading in RCP over the command line.
 * 
 * @author Marc Streit
 * @author Werner Puff
 */
public enum EStartViewType {

	glyphview("org.caleydo.view.glyph.SerializedGlyphView"),
	parcoords("org.caleydo.view.parcoords.SerializedParallelCoordinatesView"),
	heatmap("org.caleydo.view.heatmap.SerializedHeatMapView"),
	remote("org.caleydo.view.remote.SerializedRemoteRenderingView"),
	browser("org.caleydo.view.browser.SerializedHTMLBrowserView"),
	tabular("org.caleydo.view.tabular.SerializedTabularDataView"),
	radial("org.caleydo.view.radial.SerializedRadialHierarchyView"),
	histogram("org.caleydo.view.histogram.SerializedHistogramView"),
	dendrogram_horizontal("org.caleydo.view.heatmap.SerializedDendogramHorizontalView"),
	dendrogram_vertical("org.caleydo.view.heatmap.SerializedDendogramVerticalView"),
	scatterplot("org.caleydo.view.scatterplot.SerializedScatterplotView"),
	dataflipper("org.caleydo.view.dataflipper.SerializedDataFlipperView");

	private String serializedViewClassName;

	/**
	 * Constructor
	 * 
	 * @param serializedViewClass
	 *            class related to the command-line argument
	 */
	private EStartViewType(String serializedViewClassName) {
		this.serializedViewClassName = serializedViewClassName;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends ASerializedView> getSerializedViewClass() {
		try {
			return (Class<? extends ASerializedView>) Class.forName(serializedViewClassName);
		}
		catch (ClassNotFoundException e) {
			//TODO print error message that view cannot be loaded
			e.printStackTrace();
		}
		
		return null;
	}
}
