package cerberus.view.gui.swt.pathway.jgraph;

import java.awt.Frame;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.view.gui.swt.pathway.PathwayViewInter;
import cerberus.view.gui.swt.SWTEmbeddedGraphWidget;

/**
 * In this class the real drawing of the Pathway happens.
 * For the drawing the JGraph package is used.
 * We can decide here if we want to draw in a new widget
 * or if we want to draw in an existing one.
 * 
 * @author Marc Streit
 *
 */
public class PathwayViewRep implements PathwayViewInter
{
	protected final int iNewId;
	protected GeneralManager refGeneralManager;
	protected Frame refEmbeddedFrame;
	
	public PathwayViewRep(int iNewId, GeneralManager refGeneralManager)
	{
		this.iNewId = iNewId;
		this.refGeneralManager = refGeneralManager;
	}

	public void drawGraph()
	{
	}
	
	protected void retrieveNewWidget()
	{
		SWTEmbeddedGraphWidget refSWTEmbeddedGraphWidget = 
			(SWTEmbeddedGraphWidget)refGeneralManager.getSingelton()
		.getSWTGUIManager().createWidget(ManagerObjectType.GUI_SWT_EMBEDDED_JGRAPH_WIDGET);

		refEmbeddedFrame = refSWTEmbeddedGraphWidget.getEmbeddedFrame();
	}
	
	protected void retrieveExistingWidget()
	{
		
	}
	
}
