package cerberus.view.gui.swt.pathway.jgraph;

import cerberus.view.gui.swt.pathway.PathwayViewInter;

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
	final int iNewId;
	
	public PathwayViewRep(int iNewId)
	{
		this.iNewId = iNewId;
	}

	public void drawGraph()
	{
		
	}
	
	private void retrieveNewWidget()
	{
		//GUIManager
	}
	
	private void retrieveExistingWidget()
	{
		
	}
	
}
