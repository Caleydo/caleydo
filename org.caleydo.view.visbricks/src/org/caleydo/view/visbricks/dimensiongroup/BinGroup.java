package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.table.statistics.IDBasedBinning;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.eclipse.swt.widgets.Composite;

public class BinGroup extends DimensionGroup {

	IDType recordIDType;
	IDType binnigIDType;

	IDMappingManager mappingManager;

	IDBasedBinning binning;

	
	public BinGroup(GLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum);

		mappingManager = GeneralManager.get().getIDMappingManager();
	}
	


	public void tableIDTypes(IDType recordIDType, IDType binnigIDType) {
		this.recordIDType = recordIDType;
		this.binnigIDType = binnigIDType;
	}

	@Override
	public void initialize() {

		super.initialize();
//		ContentVirtualArray originalRecordVA = table.getContentData(Set.CONTENT).getRecordVA();
//		binning = new IDBasedBinning();
//		HashMap<String, ArrayList<Integer>> bin = binning.getBinning(recordIDType,
//				binnigIDType, originalRecordVA);
//
//		ContentVirtualArray newRecordVA = new ContentVirtualArray();
//		ContentGroupList groupList = new ContentGroupList();
//
//		for (ArrayList<Integer> group : bin.values()) {
//			for (Integer id : group)
//				newRecordVA.append(id);
//
//			groupList.append(new Group(group.size()));
//
//		}
//		newRecordVA.setGroupList(groupList);
//		table.setRecordVA(Set.CONTENT, newRecordVA);
		
		
//		replaceRecordVA(table.getID(), dataDomain.getDataDomainType(), Set.CONTENT);
//		ReplaceRecordVAInUseCaseEvent event = new ReplaceRecordVAInUseCaseEvent(set,
//				dataDomain.getDataDomainType(), Set.CONTENT, newRecordVA);
		// event.setVirtualArray(contentVirtualArray);
		// event.setDataDomainType(dataDomain.getDataDomainType());
		// event.setVAType(Set.CONTENT);

//		GeneralManager.get().getEventPublisher().triggerEvent(event);

		// replaceRecordVA(table.getID(),
		// table.getDataDomain().getDataDomainType(),
		// Set.CONTENT);

	}

	// @Override
	// public void replaceRecordVA(int tableID, String dataDomainType, String
	// vaType) {
	//
	// topCol.clear();
	// topBricks.clear();
	// bottomCol.clear();
	// bottomBricks.clear();
	// createSubBricks(contentVirtualArray);
	// detailRow.updateSubLayout();
	// // groupColumn.updateSubLayout();
	// visBricks.updateConnectionLinesBetweenDimensionGroups();
	//
	// }

}
