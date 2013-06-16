package org.caleydo.view.stratomex.dualFrame;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;

public class ManualClusterCreationManager {
	
	private GLBrick sourceBrick;
	
	private ASingleTablePerspectiveElementView scatterPlotView;
	
	private GLStratomex parentStratomeX;
	
	public ManualClusterCreationManager(GLStratomex stratomex)
	{
		this.parentStratomeX = stratomex;
	}
	
	public void InitiateClusterCreation(GLBrick _sourceBrick)
	{
		this.sourceBrick = _sourceBrick;
		
	}
	
	public void CreateManualClustering(VirtualArray selectedRecordVA)
	{
		parentStratomeX.createBrick(selectedRecordVA, sourceBrick);
	}

}
