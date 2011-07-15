package org.caleydo.datadomain.tissue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.event.data.DimensionGroupsChangedEvent;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * The data domain for tissue input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class TissueDataDomain extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.tissue";

	protected List<ADimensionGroupData> dimensionGroups;
	
	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;

	/**
	 * Constructor.
	 */
	public TissueDataDomain() {
		
		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + ":" + extensionID++);
		
		icon = EIconTextures.DATA_DOMAIN_TISSUE;
		
		dimensionGroups = new ArrayList<ADimensionGroupData>();

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
	public List<ADimensionGroupData> getDimensionGroups() {
		return dimensionGroups;
	}
	
	@Override
	public void setDimensionGroups(List<ADimensionGroupData> dimensionGroups) {
		this.dimensionGroups = dimensionGroups;
		DimensionGroupsChangedEvent event = new DimensionGroupsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}
	
	@Override
	public void addDimensionGroup(ADimensionGroupData dimensionGroup) {
		dimensionGroups.add(dimensionGroup);
		DimensionGroupsChangedEvent event = new DimensionGroupsChangedEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}
}
