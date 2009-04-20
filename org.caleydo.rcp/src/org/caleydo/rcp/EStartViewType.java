package org.caleydo.rcp;

import org.caleydo.rcp.views.opengl.GLDendrogramView;
import org.caleydo.rcp.views.opengl.GLGlyphView;
import org.caleydo.rcp.views.opengl.GLHierarchicalHeatMapView;
import org.caleydo.rcp.views.opengl.GLHistogramView;
import org.caleydo.rcp.views.opengl.GLHyperbolicView;
import org.caleydo.rcp.views.opengl.GLParCoordsView;
import org.caleydo.rcp.views.opengl.GLRadialHierarchyView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.caleydo.rcp.views.swt.HTMLBrowserView;
import org.caleydo.rcp.views.swt.TabularDataView;

/**
 * Enum for triggering view loading in RCP over the command line.
 * 
 * @author Marc Streit
 */
public enum EStartViewType {

	GLYPHVIEW("glyphview", GLGlyphView.ID), PARALLEL_COORDINATES("parcoords", GLParCoordsView.ID), HEATMAP(
		"heatmap", GLHierarchicalHeatMapView.ID), REMOTE("remote", GLRemoteRenderingView.ID), BROWSER("browser",
		HTMLBrowserView.ID), TABULAR("tabular", TabularDataView.ID), RADIAL_HIERARCHY("radial", GLRadialHierarchyView.ID), 
		HYPERBOLIC("hyperbolic", GLHyperbolicView.ID), HISTOGRAM("histogram", GLHistogramView.ID), DENDROGRAM("dendrogram", GLDendrogramView.ID);;

	private String sCommandLineArgument;
	private String sRCPViewID;

	/**
	 * Constructor
	 * 
	 * @param sTriggerCommand
	 */
	private EStartViewType(String sTriggerCommand, String sRCPViewID) {
		this.sCommandLineArgument = sTriggerCommand;
		this.sRCPViewID = sRCPViewID;
	}

	public String getCommandLineArgument() {
		return sCommandLineArgument;
	}

	public String getRCPViewID() {
		return sRCPViewID;
	}
}
