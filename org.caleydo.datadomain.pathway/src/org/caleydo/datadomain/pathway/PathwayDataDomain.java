/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
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
public class PathwayDataDomain
	extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.pathway";

	IDMappingManager geneIDMappingManager;

	IDType primaryIDType;

	/**
	 * {@link PathwayDataContainer}s of this datadomain.
	 */
	private List<PathwayDataContainer> dataContainers = new ArrayList<PathwayDataContainer>();

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;

	/**
	 * ID category for metabolites.
	 */
	protected IDCategory metaboliteIDCategory;

	/**
	 * IDType for metabolites.
	 */
	protected IDType metaboliteIDType;

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE
				+ DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);

		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

		this.dataSetDescription = new DataSetDescription();
	}

	@Override
	public void init() {

		super.init();

		primaryIDType = IDType.getIDType("PATHWAY_VERTEX");

		geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				IDCategory.getIDCategory("GENE"));

		addIDCategory(IDCategory.getIDCategory("GENE"));

		metaboliteIDCategory = IDCategory.registerCategory("METABOLITE");
		metaboliteIDType = IDType.registerType("METABOLITE", metaboliteIDCategory,
				EColumnType.INT);

		addIDCategory(metaboliteIDCategory);

	}

	@Override
	public void run() {

		PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(
				PathwayDatabaseType.BIOCARTA, "data/html/", "data/images/", "data/html");

		PathwayManager.get().loadPathwaysByType(pathwayDatabase);

		pathwayDatabase = PathwayManager.get().createPathwayDatabase(PathwayDatabaseType.KEGG,
				"data/xml/", "data/images/", "");

		PathwayManager.get().loadPathwaysByType(pathwayDatabase);

		PathwayManager.get().notifyPathwayLoadingFinished(true);

		super.run();
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

	public IDMappingManager getGeneIDMappingManager() {
		return geneIDMappingManager;
	}

	@Override
	public int getDataAmount() {
		// TODO Calculate properly
		return 0;
	}

	/**
	 * Adds the specified datacontainer to this datadomain.
	 * 
	 * @param dataContainer
	 */
	public void addDataContainer(PathwayDataContainer dataContainer) {
		if (dataContainer != null)
			dataContainers.add(dataContainer);

		DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public List<PathwayDataContainer> getDataContainers() {
		return dataContainers;
	}

	public void setDataContainers(List<PathwayDataContainer> dataContainers) {
		if (dataContainers != null)
			this.dataContainers = dataContainers;

		DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}
}
