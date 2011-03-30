package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.collection.set.statistics.IDBasedBinning;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.mapping.IDMappingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;

public class BinGroup extends DimensionGroup {

	IDType contentIDType;
	IDType binnigIDType;

	IDBasedBinning binning;
	IDMappingManager mappingManager;


	public BinGroup(GLCaleydoCanvas canvas, ViewFrustum viewFrustum) {
		super(canvas, viewFrustum);

		mappingManager = GeneralManager.get().getIDMappingManager();
	}

	public void setIDTypes(IDType contentIDType, IDType binnigIDType) {
		this.contentIDType = contentIDType;
		this.binnigIDType = binnigIDType;
	}

	@Override
	public void initialize() {

		super.initialize();
		ContentVirtualArray originalContentVA = set.getContentData(Set.CONTENT).getContentVA();
		binning = new IDBasedBinning();
		HashMap<String, ArrayList<Integer>> bin = binning.getBinning(contentIDType,
				binnigIDType, originalContentVA);

		ContentVirtualArray newContentVA = new ContentVirtualArray();
		ContentGroupList groupList = new ContentGroupList();

		for (ArrayList<Integer> group : bin.values()) {
			for (Integer id : group)
				newContentVA.append(id);

			groupList.append(new Group(group.size()));

		}
		newContentVA.setGroupList(groupList);
		set.setContentVA(Set.CONTENT, newContentVA);
//		replaceContentVA(set.getID(), dataDomain.getDataDomainType(), Set.CONTENT);
//		ReplaceContentVAInUseCaseEvent event = new ReplaceContentVAInUseCaseEvent(set,
//				dataDomain.getDataDomainType(), Set.CONTENT, newContentVA);
		// event.setVirtualArray(contentVirtualArray);
		// event.setDataDomainType(dataDomain.getDataDomainType());
		// event.setVAType(Set.CONTENT);

//		GeneralManager.get().getEventPublisher().triggerEvent(event);

		// replaceContentVA(set.getID(),
		// set.getDataDomain().getDataDomainType(),
		// Set.CONTENT);

	}

	// @Override
	// public void replaceContentVA(int setID, String dataDomainType, String
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
