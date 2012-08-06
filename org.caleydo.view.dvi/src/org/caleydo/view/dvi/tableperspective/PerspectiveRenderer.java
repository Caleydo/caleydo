/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
	public void render(GL2 gl) {
		//This specific renderer is used not in a regular layout.
		renderContent(gl);
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
