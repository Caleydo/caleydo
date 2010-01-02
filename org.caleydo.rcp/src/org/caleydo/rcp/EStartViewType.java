package org.caleydo.rcp;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.SerializedGlyphView;
import org.caleydo.core.view.opengl.canvas.histogram.SerializedHistogramView;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.caleydo.core.view.opengl.canvas.remote.SerializedRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.SerializedDataFlipperView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedDendogramHorizontalView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedDendogramVerticalView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedHeatMapView;
import org.caleydo.core.view.opengl.canvas.storagebased.parallelcoordinates.SerializedParallelCoordinatesView;
import org.caleydo.core.view.swt.browser.SerializedHTMLBrowserView;
import org.caleydo.core.view.swt.tabular.SerializedTabularDataView;

/**
 * Enum for triggering view loading in RCP over the command line.
 * 
 * @author Marc Streit
 * @author Werner Puff
 */
public enum EStartViewType {

	glyphview(SerializedGlyphView.class),
	parcoords(SerializedParallelCoordinatesView.class),
	heatmap(SerializedHeatMapView.class),
	remote(SerializedRemoteRenderingView.class),
	browser(SerializedHTMLBrowserView.class),
	tabular(SerializedTabularDataView.class),
	radial(SerializedRadialHierarchyView.class),
	histogram(SerializedHistogramView.class),
	dendrogram_horizontal(SerializedDendogramHorizontalView.class),
	dendrogram_vertical(SerializedDendogramVerticalView.class),
	//scatterplot(SerializedScatterplotView.class),
	dataflipper(SerializedDataFlipperView.class);

	private Class<? extends ASerializedView> serializedViewClass;

	/**
	 * Constructor
	 * 
	 * @param serializedViewClass
	 *            class related to the command-line argument
	 */
	private EStartViewType(Class<? extends ASerializedView> serializedViewClass) {
		this.serializedViewClass = serializedViewClass;
	}

	public Class<? extends ASerializedView> getSerializedViewClass() {
		return serializedViewClass;
	}
}
