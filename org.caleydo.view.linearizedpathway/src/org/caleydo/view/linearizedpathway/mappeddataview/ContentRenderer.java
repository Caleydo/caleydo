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
package org.caleydo.view.linearizedpathway.mappeddataview;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.datadomain.genetic.GeneticDataDomain;

/**
 * @author alexsb
 * 
 */
public abstract class ContentRenderer extends SelectableRenderer {

	Integer geneID;
	DataContainer dataContainer;
	ADataPerspective<?, ?, ?, ?> experimentPerspective;
	GeneticDataDomain dataDomain;
	Integer davidID;
	float z = 0.05f;
	Group group;
	/** The sample ID Type of the local sample VA */
	IDType sampleIDType;
	APickingListener pickingListener;

	IDMappingManager sampleIDMappingManager;

	public ContentRenderer(Integer geneID, Integer davidID, GeneticDataDomain dataDomain,
			DataContainer dataContainer,
			ADataPerspective<?, ?, ?, ?> experimentPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group) {
		super(parentView, parent);
		this.davidID = davidID;
		this.geneID = geneID;

		topBarColor = MappedDataRenderer.BAR_COLOR;
		bottomBarColor = topBarColor;
		this.dataDomain = dataDomain;
		this.dataContainer = dataContainer;
		this.experimentPerspective = experimentPerspective;
		this.group = group;
		sampleIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				parent.sampleIDType.getIDCategory());
		sampleIDType = experimentPerspective.getIdType();
		init();
	}

	public ContentRenderer(IContentRendererInitializor contentRendererInitializor) {
		super(contentRendererInitializor.getView(), contentRendererInitializor
				.getMappedDataRenderer());
		this.davidID = contentRendererInitializor.getDavidID();
		this.geneID = contentRendererInitializor.getGeneID();

		topBarColor = MappedDataRenderer.BAR_COLOR;
		bottomBarColor = topBarColor;
		this.dataDomain = contentRendererInitializor.getDataDomain();
		this.dataContainer = contentRendererInitializor.getDataContainer();
		this.experimentPerspective = contentRendererInitializor
				.getExperimentPerspective();
		this.group = contentRendererInitializor.getGroup();
		sampleIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				parent.sampleIDType.getIDCategory());
		sampleIDType = experimentPerspective.getIdType();
		init();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		unRegisterPickingListener();
	}

	public abstract void init();

	private void unRegisterPickingListener() {
		parentView.removePickingListener(pickingListener);
	}

}
