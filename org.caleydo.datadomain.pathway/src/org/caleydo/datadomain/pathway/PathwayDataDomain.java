package org.caleydo.datadomain.pathway;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.manager.GeneticIDMappingHelper;
import org.caleydo.datadomain.pathway.manager.PathwayDatabase;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

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

	IDMappingManager geneIDMappingManager;
	GeneticIDMappingHelper mappingHelper;

	IDType primaryIDType;

	//private PathwayDatabaseType pathwayDatabaseType;

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE
				+ DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER
				+ extensionID++);

		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

		this.loadDataParameters = new LoadDataParameters();
	}

	@Override
	public void init() {

		super.init();
		// IDMappingLoader.get().loadMappingFile(fileName);

		primaryIDType = IDType.getIDType("PATHWAY_VERTEX");

		mappingHelper = new GeneticIDMappingHelper(IDMappingManagerRegistry
				.get().getIDMappingManager(IDCategory.getIDCategory("GENE")));

		addIDCategory(IDCategory.getIDCategory("GENE"));
		
		String pathwayDataSources = GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES);
		
		loadDataParameters.setLabel(pathwayDataSources);
		
		if (pathwayDataSources.contains(PathwayDatabaseType.BIOCARTA.getName())) {

			//pathwayDataDomain.setPathwayDatabaseType(PathwayDatabaseType.BIOCARTA);

			PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(
					PathwayDatabaseType.BIOCARTA, "data/html/", "data/images/",
					"data/html");

			PathwayManager.get().loadPathwaysByType(pathwayDatabase);
		}

		if (pathwayDataSources.contains(PathwayDatabaseType.KEGG.getName())) {
			
			//pathwayDataDomain.setPathwayDatabaseType(PathwayDatabaseType.KEGG);

			PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(
					PathwayDatabaseType.KEGG, "data/xml/", "data/images/", "");

			PathwayManager.get().loadPathwaysByType(pathwayDatabase);
		}

		PathwayManager.get().notifyPathwayLoadingFinished(true);
	}


	
	// @Override
	// protected void initIDMappings() {
	// // Load IDs needed in this datadomain
	// IDMappingLoader.get().loadMappingFile(fileName);
	// }

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

	public IDMappingManager getGeneIDMappingManager() {
		return geneIDMappingManager;
	}

	/**
	 * @return the mappingHelper, see {@link #mappingHelper}
	 */
	public GeneticIDMappingHelper getMappingHelper() {
		return mappingHelper;
	}

	@Override
	public int getDataAmount() {
		// TODO Calculate properly
		return 0;
	}

//	public void setPathwayDatabaseType(PathwayDatabaseType pathwayDatabaseType) {
//		//this.pathwayDatabaseType = pathwayDatabaseType;
//		loadDataParameters.setLabel(pathwayDatabaseType.getName());
//	}

	//
	// @Override
	// public List<ADimensionGroupData> getDimensionGroups() {
	// return dimensionGroups;
	// }
	//
	// @Override
	// public void setDimensionGroups(List<ADimensionGroupData> dimensionGroups)
	// {
	// this.dimensionGroups = dimensionGroups;
	// DimensionGroupsChangedEvent event = new
	// DimensionGroupsChangedEvent(this);
	// event.setSender(this);
	// GeneralManager.get().getEventPublisher().triggerEvent(event);
	// }
	//
	// @Override
	// public void addDimensionGroup(ADimensionGroupData dimensionGroup) {
	// dimensionGroups.add(dimensionGroup);
	// DimensionGroupsChangedEvent event = new
	// DimensionGroupsChangedEvent(this);
	// event.setSender(this);
	// GeneralManager.get().getEventPublisher().triggerEvent(event);
	// }
}
