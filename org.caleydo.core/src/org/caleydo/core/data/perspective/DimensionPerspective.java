package org.caleydo.core.data.perspective;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.DimensionFilterManager;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;

/**
 * Implementation of {@link DataPerspective} for dimensions.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class DimensionPerspective
	extends
	DataPerspective<DimensionVirtualArray, DimensionGroupList, DimensionVADelta, DimensionFilterManager> {

	private static int dimensionDataRunningNumber;

	public DimensionPerspective() {
	}

	public DimensionPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		perspectiveID = "DimensionPerspective_" + dimensionDataRunningNumber;
		dimensionDataRunningNumber++;
		filterManager = new DimensionFilterManager(dataDomain, this);
		idType = dataDomain.getDimensionIDType();
	}

	@Override
	protected DimensionGroupList createGroupList() {
		return new DimensionGroupList();
	}

	@Override
	protected DimensionVirtualArray newConcreteVirtualArray(List<Integer> indexList) {
		return new DimensionVirtualArray(perspectiveID, indexList);
	}

	@Override
	protected String getLabel(Integer id) {
		return dataDomain.getDimensionLabel(id);
	}
}
