package org.caleydo.datadomain.pathway;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.mapping.IDMappingLoader;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.datadomain.pathway.rcp.PathwayLoadingProgressIndicatorAction;

/**
 * TODO The use case for pathway input data.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class PathwayDataDomain extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.pathway";

	IDType primaryIDType;
	
	protected List<ADimensionGroupData> dimensionGroups;

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {

		super(DATA_DOMAIN_TYPE);
		
		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

		PathwayManager.get().triggerParsingPathwayDatabases();

		// Trigger pathway loading
		new PathwayLoadingProgressIndicatorAction().run(null);

		primaryIDType = IDType.getIDType("PATHWAY_VERTEX");
		
		dimensionGroups = new ArrayList<ADimensionGroupData>();
	}

	@Override
	protected void initIDMappings() {
		// Load IDs needed in this datadomain
		IDMappingLoader.get().loadMappingFile(fileName);
	}
	
	public IDType getPrimaryIDType() {
		return primaryIDType;
	}

	public IDType getDavidIDType() {
		return IDType.getIDType("DAVID");
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
	}
	
	@Override
	public void addDimensionGroup(ADimensionGroupData dimensionGroup) {
		dimensionGroups.add(dimensionGroup);
	}
}
