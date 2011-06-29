package org.caleydo.view.visbricks.brick.data;

import java.util.HashMap;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.data.virtualarray.SetBasedDimensionGroupData;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;

public class BrickDimensionGroupDataCreator {

	private HashMap<Class<? extends ADimensionGroupData>, Class<? extends IBrickDimensionGroupData>> map;

	public BrickDimensionGroupDataCreator() {
		map = new HashMap<Class<? extends ADimensionGroupData>, Class<? extends IBrickDimensionGroupData>>();
		map.put(PathwayDimensionGroupData.class,
				PathwayBrickDimensionGroupData.class);
		map.put(SetBasedDimensionGroupData.class,
				SetBasedBrickDimensionGroupData.class);
	}

	public IBrickDimensionGroupData createBrickDimensionGroupData(
			ADimensionGroupData dimensionGroupData) {
		Class<? extends IBrickDimensionGroupData> c = map
				.get(dimensionGroupData.getClass());
		try {
			return c.getConstructor(dimensionGroupData.getClass()).newInstance(
					dimensionGroupData.getClass().cast(dimensionGroupData));

		} catch (Exception e) {

		}

		return null;
	}

}
