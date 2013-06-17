package org.caleydo.view.stratomex.dualFrame;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.column.BrickColumn;

public class ManualClusterCreationManager {
	
	private GLBrick sourceBrick;
	
	private BrickColumn sourceBrickColumn;
	
	private ASingleTablePerspectiveElementView scatterPlotView;
	
	private GLStratomex parentStratomeX;
	
	private EventBasedSelectionManager selectionManager;
	
	private ArrayList<VirtualArray> stratificationList;
	
	public ManualClusterCreationManager(GLStratomex stratomex)
	{
		this.parentStratomeX = stratomex;
		stratificationList = new ArrayList<VirtualArray>();
	}
	
	public void InitiateClusterCreation(GLBrick _sourceBrick)
	{
		this.sourceBrick = _sourceBrick;
		this.sourceBrickColumn = sourceBrick.getBrickColumn();
		stratificationList.clear();
		
	}
	
	public void CreateManualClustering()
	{
		
		this.sourceBrick = this.sourceBrickColumn.getSegmentBricks().get(0);
		
		// This is the VA from the source brick to be split
		VirtualArray brickDataVA = sourceBrick.getBrickColumn().getTablePerspective().getRecordPerspective().getVirtualArray();
		
		// This is the VA that keeps the selected ones
		VirtualArray selectedRecordVA = new VirtualArray(brickDataVA.getIdType());
		
		// Check if the ID types of the brick and primary type of StratomeX are the same
		// and convert accordingly
		boolean idNeedsConverting = false;
		if (!brickDataVA.getIdType().equals(parentStratomeX.getRecordSelectionManager().getIDType())) {
			idNeedsConverting = true;
			// sharedRecordVA =
			// sourceBrick.getDataDomain().convertForeignRecordPerspective(foreignPerspective)
		}
		
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				brickDataVA.getIdType().getIDCategory());
		
		// No selection, do nothing
		// Here build the selected records list
		if (!(parentStratomeX.getRecordSelectionManager().getNumberOfElements(SelectionType.SELECTION) == 0))
		{
			for (int recordID: brickDataVA)
			{
				if (idNeedsConverting)
				{
					int mappedRecordID = idMappingManager.getID(selectedRecordVA.getIdType(), parentStratomeX.getRecordSelectionManager().getIDType(), recordID);
					if(parentStratomeX.getRecordSelectionManager().checkStatus(SelectionType.SELECTION, mappedRecordID))
					{
						selectedRecordVA.append(recordID);
					}
				}
				else
				{
					if(parentStratomeX.getRecordSelectionManager().checkStatus(SelectionType.SELECTION, recordID))
					{
						selectedRecordVA.append(recordID);
					}
				}
			}		
			
			selectedRecordVA = removeDuplicateEntries(selectedRecordVA);
			
			stratificationList.add(selectedRecordVA);
			
			parentStratomeX.createBrick(selectedRecordVA, sourceBrick);
		}
	}
	
	public VirtualArray removeDuplicateEntries(VirtualArray selectedRecordVA)
	{
		VirtualArray result = new VirtualArray(selectedRecordVA.getIdType());
		for (int recordID: selectedRecordVA)
		{
			boolean alreadyClustered = false;
			for (VirtualArray currentGroup: this.stratificationList)
			{
				if (currentGroup.getIDs().contains(recordID))
				{
					alreadyClustered = true;
				}
			}
			
			if(!alreadyClustered)
			{
				result.append(recordID);
			}
		}
		
		
		return result;
	}

}
