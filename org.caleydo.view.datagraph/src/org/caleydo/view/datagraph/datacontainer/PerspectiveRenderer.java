package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;
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
