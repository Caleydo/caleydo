package org.caleydo.datadomain.tissue;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.IDimensionGroupData;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * TODO The use case for tissue input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class TissueDataDomain extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.tissue";

	protected Set<IDimensionGroupData> dimensionGroups;
	
	/**
	 * Constructor.
	 */
	public TissueDataDomain() {
		
		super(DATA_DOMAIN_TYPE);
		
		icon = EIconTextures.DATA_DOMAIN_TISSUE;
		
		dimensionGroups = new HashSet<IDimensionGroupData>();

		// possibleIDCategories.put(EIDCategory.GENE, null);
	}
	
	@Override
	protected void initIDMappings() {
		// nothing to do ATM
	}

	@Override
	public void registerEventListeners() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterEventListeners() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Set<IDimensionGroupData> getDimensionGroups() {
		return dimensionGroups;
	}
	
	@Override
	public void setDimensionGroups(Set<IDimensionGroupData> dimensionGroups) {
		this.dimensionGroups = dimensionGroups;
	}
	
	@Override
	public void addDimensionGroup(IDimensionGroupData dimensionGroup) {
		dimensionGroups.add(dimensionGroup);
	}
}
