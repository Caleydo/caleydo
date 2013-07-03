/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.view.dvi.GLDataViewIntegrator;

/**
 * Event that specifies whether the connection between data nodes shall be
 * displayed per default in {@link GLDataViewIntegrator}.
 * 
 * @author Christian
 * 
 */
public class ShowDataConnectionsEvent
	extends AEvent
{
	private boolean showDataConnections;

	public ShowDataConnectionsEvent(boolean showDataConnections)
	{
		this.setShowDataConnections(showDataConnections);
	}

	@Override
	public boolean checkIntegrity()
	{
		return true;
	}

	public boolean isShowDataConnections()
	{
		return showDataConnections;
	}

	public void setShowDataConnections(boolean showDataConnections)
	{
		this.showDataConnections = showDataConnections;
	}

}
