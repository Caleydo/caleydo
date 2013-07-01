/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class PerspectiveRenderer
	extends ADraggableColorRenderer
{

	private String perspectiveID;
	private boolean isRecordPerspective;
	private ATableBasedDataDomain dataDomain;
	private Point2D position;

	public PerspectiveRenderer(float[] color, float[] borderColor, int borderWidth,
			AGLView view, ATableBasedDataDomain dataDomain, String perspectiveID,
			boolean isRecordPerspective)
	{
		super(color, borderColor, borderWidth, view);
		this.setDataDomain(dataDomain);
		this.setRecordPerspective(isRecordPerspective);
		this.setPerspectiveID(perspectiveID);
	}
	

	@Override
	public void renderContent(GL2 gl) {
		//This specific renderer is used not in a regular layout.
		super.renderContent(gl);
	}

	public String getPerspectiveID()
	{
		return perspectiveID;
	}

	public void setPerspectiveID(String perspectiveID)
	{
		this.perspectiveID = perspectiveID;
	}

	public boolean isRecordPerspective()
	{
		return isRecordPerspective;
	}

	public void setRecordPerspective(boolean isRecordPerspective)
	{
		this.isRecordPerspective = isRecordPerspective;
	}

	public ATableBasedDataDomain getDataDomain()
	{
		return dataDomain;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain)
	{
		this.dataDomain = dataDomain;
	}

	@Override
	public Point2D getPosition()
	{
		return position;
	}

	public void setPosition(Point2D position)
	{
		this.position = position;
	}

}
