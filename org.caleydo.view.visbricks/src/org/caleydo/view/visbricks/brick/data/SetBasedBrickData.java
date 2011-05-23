package org.caleydo.view.visbricks.brick.data;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.StorageData;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.view.visbricks.brick.GLBrick;

public class SetBasedBrickData implements IBrickData {

	private ASetBasedDataDomain dataDomain;
	private ContentVirtualArray contentVA;
	private Group group;
	private ISet set;
	private SetBasedDimensionGroupData dimensionGroupData;
	private double averageValue;

	public SetBasedBrickData(ASetBasedDataDomain dataDomain, ISet set,
			ContentVirtualArray contentVA, Group group,
			SetBasedDimensionGroupData dimensionGroupData) {
		this.set = set;
		this.dataDomain = dataDomain;
		this.contentVA = contentVA;
		this.group = group;
		this.dimensionGroupData = dimensionGroupData;
		calculateAverageValue();
	}

	@Override
	public IDataDomain getDataDomain() {
		// TODO Auto-generated method stub
		return dataDomain;
	}

	@Override
	public ContentVirtualArray getContentVA() {
		// TODO Auto-generated method stub
		return contentVA;
	}

	@Override
	public Group getGroup() {
		// TODO Auto-generated method stub
		return group;
	}

	@Override
	public void setBrickData(GLBrick brick) {
		brick.setDataDomain(dataDomain);
		brick.setContentVA(group, contentVA);
	}

	private void calculateAverageValue() {
		int count = 0;
		// if (contentVA == null)
		// throw new IllegalStateException("contentVA was null");
		for (Integer contenID : contentVA) {
			StorageData storageData = set.getStorageData(Set.STORAGE);
			if (storageData == null) {
				averageValue = 0;
				return;
			}

			StorageVirtualArray storageVA = storageData.getStorageVA();

			if (storageVA == null) {
				averageValue = 0;
				return;
			}
			for (Integer storageID : storageVA) {
				float value = set.get(storageID).getFloat(
						EDataRepresentation.NORMALIZED, contenID);
				if (!Float.isNaN(value)) {
					averageValue += value;
					count++;
				}
			}
		}
		averageValue /= count;
	}

	public double getAverageValue() {
		return averageValue;
	}

	@Override
	public String getLabel() {
		return "Group " + group.getGroupID() + " in " + set.getLabel();
	}

}
