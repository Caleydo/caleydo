package org.geneview.core.manager.view;

/**
 * 
 * @author Alexander Lex
 *
 */

public class Pick {
	
	private int iPickingID = 0;
	private EPickingMode ePickingMode = EPickingMode.CLICKED;
	
	public Pick(int iPickingID)
	{
		this.iPickingID = iPickingID;
	}
	
	public Pick(int iPickingID, EPickingMode ePickingMode)
	{
		this.iPickingID = iPickingID;
		this.ePickingMode = ePickingMode;
	}
	
	public int getPickingID()
	{
		return iPickingID;
	}
	
	public EPickingMode getPickingMode()
	{
		return ePickingMode;
	}

}
