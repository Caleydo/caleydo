/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * The data domain for pathways triggers the loading of the pathways from KEGG.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class PathwayDataDomain extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.pathway";

	private IDMappingManager geneIDMappingManager;

	private IDType primaryIDType;

	/**
	 * {@link PathwayTablePerspective}s of this datadomain.
	 */
	@XmlTransient
	private List<PathwayTablePerspective> tablePerspectives = new ArrayList<PathwayTablePerspective>();

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

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER
				+ IDCreator.createPersistentID(PathwayDataDomain.class));

		this.dataSetDescription = new DataSetDescription();

		dataSetDescription.setDataSetName("Pathway Data");

		// set a neutral gray as the pathway color
		dataSetDescription.setColor(new Color(0.8f, 0.8f, 0.8f));

		// Pathways should not be serialized, as they are automatically loaded
		// when genetic data get loaded.
		isSerializeable = false;
	}

	@Override
	public void init() {

		super.init();

		primaryIDType = IDType.getIDType("PATHWAY_VERTEX");

		final IDCategory gene = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());

		geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(gene);

		addIDCategory(gene);

		metaboliteIDCategory = IDCategory.registerCategory("METABOLITE");

		metaboliteIDType = IDType.registerType("METABOLITE", metaboliteIDCategory, EDataType.INTEGER);

		addIDCategory(metaboliteIDCategory);

		// IDCategory pathwayIDCategory = IDCategory.registerCategory("PATHWAY");
		//
		// IDType.registerType("PATHWAY", pathwayIDCategory, EDataType.INTEGER);
		//
		// addIDCategory(pathwayIDCategory);
	}

	@Override
	public void run() {

		PathwayManager pathwayManager = PathwayManager.get();

		PathwayManager.createPathwayDatabases();
		pathwayManager.notifyPathwayLoadingFinished(true);

		super.run();
	}

	public IDType getPrimaryIDType() {
		return primaryIDType;
	}

	public IDType getDavidIDType() {
		return IDType.getIDType("DAVID");
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
	 * Adds the specified {@link TablePerspective} to this data domain.
	 *
	 * @param tablePerspective
	 */
	public void addTablePerspective(PathwayTablePerspective tablePerspective) {
		if (tablePerspective != null)
			tablePerspectives.add(tablePerspective);

		DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
		event.setSender(this);
		
		EventPublisher.INSTANCE.triggerEvent(event);
	}

	public List<PathwayTablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	public void setTablePerspectives(List<PathwayTablePerspective> tablePerspectives) {
		if (tablePerspectives != null)
			this.tablePerspectives = tablePerspectives;

		DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
		event.setSender(this);
		
		EventPublisher.INSTANCE.triggerEvent(event);
	}
}
