package org.caleydo.rcp;

import org.caleydo.rcp.views.GLHeatMapView;
import org.caleydo.rcp.views.GLParCoordsView;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.caleydo.rcp.views.browser.HTMLBrowserView;

/**
 * Enum for triggering view loading in RCP over the command line.
 * 
 * @author Marc Streit
 *
 */
public enum EStartViewsMode
{
	PARALLEL_COORDINATES("parcoords", GLParCoordsView.ID),
	HEATMAP("heatmap", GLHeatMapView.ID),
	REMOTE("remote", GLRemoteRenderingView.ID),
	BROWSER("browser", HTMLBrowserView.ID);
	
	private String sCommandLineArgument;
	private String sRCPViewID;
	
	/**
	 * Constructor
	 * @param sTriggerCommand
	 */
	private EStartViewsMode(String sTriggerCommand,
			String sRCPViewID)
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
