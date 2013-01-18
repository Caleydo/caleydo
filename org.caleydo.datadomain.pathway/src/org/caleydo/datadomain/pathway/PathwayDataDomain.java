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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.data.PathwayRecordPerspective;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayDatabase;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

import com.google.common.collect.Lists;

/**
 * The data domain for pathways triggers the loading of the pathways from KEGG
 * and BioCarta.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class PathwayDataDomain
	extends ADataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.pathway";

	private IDMappingManager geneIDMappingManager;

	private IDType primaryIDType;

	/**
	 * {@link PathwayTablePerspective}s of this datadomain.
	 */
	@XmlTransient
	private List<PathwayTablePerspective> tablePerspectives = new ArrayList<PathwayTablePerspective>();

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static final AtomicInteger extensionID = new AtomicInteger();

	/**
	 * ID category for metabolites.
	 */
	protected IDCategory metaboliteIDCategory;

	/**
	 * IDType for metabolites.
	 */
	protected IDType metaboliteIDType;

	/**
	 * a list of special record perspectives holding a pathway and their corresponding david ids
	 */
	@XmlTransient
	private Collection<PathwayRecordPerspective> pathwayRecordPerspectives = Lists.newArrayList();

	/**
	 * Constructor.
	 */
	public PathwayDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE
 + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER
				+ extensionID.getAndDecrement());

		icon = EIconTextures.DATA_DOMAIN_PATHWAY;

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

		geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				IDCategory.getIDCategory("GENE"));

		addIDCategory(IDCategory.getIDCategory("GENE"));

		metaboliteIDCategory = IDCategory.registerCategory("METABOLITE");
		metaboliteIDType = IDType.registerType("METABOLITE", metaboliteIDCategory,
				EDataClass.NATURAL_NUMBER);

		addIDCategory(metaboliteIDCategory);

	}

	@Override
	public void run() {

		// Do not loa
		// PathwayDatabase pathwayDatabase =
		// PathwayManager.get().createPathwayDatabase(
		// EPathwayDatabaseType.BIOCARTA, "data/html/", "data/images/",
		// "data/html");
		//
		// PathwayManager.get().loadPathwaysByType(pathwayDatabase);

		PathwayDatabase pathwayDatabase = PathwayManager.get().createPathwayDatabase(
				EPathwayDatabaseType.KEGG, "data/xml/", "data/images/", "");

		PathwayManager.get().loadPathwaysByType(pathwayDatabase);

		PathwayManager.get().notifyPathwayLoadingFinished(true);

		pathwayRecordPerspectives.clear();
		for (PathwayGraph pathway : PathwayManager.get().getAllItems()) {
			PathwayRecordPerspective p = new PathwayRecordPerspective(pathway, this);
			p.reset();
			pathwayRecordPerspectives.add(p);
		}

		super.run();
	}

	/**
	 * @return the pathwayRecordPerspectives, see {@link #pathwayRecordPerspectives}
	 */
	public Collection<PathwayRecordPerspective> getPathwayRecordPerspectives() {
		return pathwayRecordPerspectives;
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
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	public List<PathwayTablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	public void setTablePerspectives(List<PathwayTablePerspective> tablePerspectives) {
		if (tablePerspectives != null)
			this.tablePerspectives = tablePerspectives;

		DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}
}
