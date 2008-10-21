package org.caleydo.rcp;

import org.caleydo.rcp.views.GLGlyphView;
import org.caleydo.rcp.views.GLHeatMapView;
import org.caleydo.rcp.views.GLParCoordsView;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.caleydo.rcp.views.HTMLBrowserView;

/**
 * Enum for triggering view loading in RCP over the command line.
 * 
 * @author Marc Streit
 */
public enum EStartViewType
{
	PARALLEL_COORDINATES("parcoords", GLParCoordsView.ID), GLYPHVIEW("glyphview",
			GLGlyphView.ID), HEATMAP("heatmap", GLHeatMapView.ID), REMOTE("remote",
			GLRemoteRenderingView.ID), BROWSER("browser", HTMLBrowserView.ID);

	private String sCommandLineArgument;
	private String sRCPViewID;

	/**
	 * Constructor
	 * 
	 * @param sTriggerCommand
	 */
	private EStartViewType(String sTriggerCommand, String sRCPViewID)
	{
		this.sCommandLineArgument = sTriggerCommand;
		this.sRCPViewID = sRCPViewID;
	}

	public String getCommandLineArgument()
	{
		return sCommandLineArgument;
	}

	public String getRCPViewID()
	{
		return sRCPViewID;
	}
}
