package org.caleydo.core.data.perspective;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.filter.DimensionFilterManager;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;

/**
 * Implementation of {@link ADataPerspective} for dimensions.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
public class DimensionPerspective
	extends
	ADataPerspective<DimensionVirtualArray, DimensionGroupList, DimensionVADelta, DimensionFilterManager> {

	
	public DimensionPerspective() {
	}

	public DimensionPerspective(ATableBasedDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	protected void init() {
		// if this perspective is de-serialized the perspectiveID is already set.
		if (perspectiveID == null)
			perspectiveID = "DimensionPerspective_" + UUID.randomUUID();
		filterManager = new DimensionFilterManager(dataDomain, this);
		idType = dataDomain.getDimensionIDType();
	}

	@Override
	protected DimensionGroupList createGroupList() {
		return new DimensionGroupList();
	}

	@Override
	protected void createFilterManager() {
		filterManager = new DimensionFilterManager(dataDomain, this);
	}

	@Override
	protected DimensionVirtualArray newConcreteVirtualArray(List<Integer> indexList) {
		return new DimensionVirtualArray(perspectiveID, indexList);
	}

	@Override
	protected String getElementLabel(Integer id) {
		return dataDomain.getDimensionLabel(id);
	}

	@Override
	protected List<Integer> getIDList() {
		return dataDomain.getTable().getColumnIDList();
	}
}
