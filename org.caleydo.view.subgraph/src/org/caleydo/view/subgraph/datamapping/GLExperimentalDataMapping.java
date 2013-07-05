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
package org.caleydo.view.subgraph.datamapping;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.listener.PathwayMappingEvent;
import org.caleydo.view.subgraph.GLSubGraph;

/**
 * Data mapping for selecting datasets and perspectives.
 *
 * @author Marc Streit
 *
 */
public class GLExperimentalDataMapping extends AnimatedGLElementContainer implements ISelectionCallback {

	protected final GLSubGraph view;

	protected final DataMappingState dmState;

	GLElementContainer pathwayDatasets;

	public GLExperimentalDataMapping(GLSubGraph view) {
		this.view = view;

		setLayout(new GLFlowLayout(true, 2, new GLPadding(5)));
		setSize(-1, 50);

		dmState = new DataMappingState(view.getPathEventSpace());

		GLElementContainer generalMapping = new GLElementContainer(new GLFlowLayout(false, 10, new GLPadding(5)));

		GLElementContainer ddElements = new GLElementContainer(new GLFlowLayout(true, 10, new GLPadding(5)));
		GLElementContainer stratElements = new GLElementContainer(new GLFlowLayout(true, 10, new GLPadding(5)));

		generalMapping.setLayoutData(0.5f);
		generalMapping.add(ddElements);
		generalMapping.add(stratElements);

		pathwayDatasets = new GLElementContainer(new GLFlowLayout(true, 10, new GLPadding(10)));

		addPerspectiveElements(stratElements);
		addDataDomainElements(ddElements);
		updatePathwayDatasets();

		this.add(generalMapping);
		this.add(pathwayDatasets);

	}

	private void addDataDomainElements(GLElementContainer ddElements) {

		ddElements.add(new GLElement() {
			@Override
			protected void renderImpl(GLGraphics g, float w, float h) {
				g.drawText("Data sets:", 0, -2, w, h);
			}
		}.setSize(100, 15));
		for (ATableBasedDataDomain dd : DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class)) {
			TableBasedDataDomainElement ddElement = new TableBasedDataDomainElement(dd, dmState, this);
			ddElements.add(ddElement);
			if (dd.getLabel().contains("mRNA"))
				ddElement.setSelected(true);
		}
	}

	private void updatePathwayDatasets() {

		pathwayDatasets.clear();

		pathwayDatasets.add(new GLElement() {
			@Override
			protected void renderImpl(GLGraphics g, float w, float h) {
				g.drawText("Pathway Mapping:", 0, -2, w, h);
			}
		}.setSize(150, 15));

		RadioController radio = new RadioController(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				if (selected) {
					ATableBasedDataDomain dd = button.getLayoutDataAs(ATableBasedDataDomain.class, null);
					TablePerspective perspective = dmState.getMatchingTablePerspective(dd);
					dmState.setPathwayMappedTablePerspective(perspective);
					PathwayMappingEvent event = new PathwayMappingEvent(perspective);
					event.setEventSpace(view.getPathEventSpace());
					EventPublisher.trigger(event);


				}

			}
		});
		PathwayDataSetElement noMappingElement = new PathwayDataSetElement("No Mapping");

		pathwayDatasets.add(noMappingElement);
		radio.add(noMappingElement);

		PathwayDataSetElement pwMappingElement;
		boolean wasPreviouslySelected = false;
		for (ATableBasedDataDomain dd : dmState.getDataDomains()) {
			if (dd instanceof GeneticDataDomain) {
				pwMappingElement = new PathwayDataSetElement(dd);
				pathwayDatasets.add(pwMappingElement);
				radio.add(pwMappingElement);
				if (dmState.getPathwayMappedTablePerspective() != null
						&& dmState.getPathwayMappedTablePerspective().getDataDomain() == dd) {
					pwMappingElement.setSelected(true);
					wasPreviouslySelected = true;
				}
			}
		}
		if (!wasPreviouslySelected) {
			noMappingElement.setSelected(true);
			PathwayMappingEvent event = new PathwayMappingEvent();
			event.setEventSpace(view.getPathEventSpace());
			EventPublisher.trigger(event);
		}
	}

	private void addPerspectiveElements(GLElementContainer stratElements) {

		stratElements.add(new GLElement() {
			@Override
			protected void renderImpl(GLGraphics g, float w, float h) {
				g.drawText("Stratifications:", 0, -2, w, h);
			}
		}.setSize(100, 15));

		// FIXME: from which dataset should we take the stratifications? currently we always take them from mRNA
		ATableBasedDataDomain mRNAdd = null;
		for (ATableBasedDataDomain dd : DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class)) {
			if (dd.getLabel().contains("Cell")) {
				mRNAdd = dd;
				break;
			}
		}

		if (mRNAdd == null)
			return;

		RadioController radio = new RadioController(this);
		Perspective defaultPerspective = null;
		PerspectiveElement defaultElement = null;
		for (String recordPerspectiveIDs : mRNAdd.getRecordPerspectiveIDs()) {
			Perspective recordPerspective = mRNAdd.getTable().getRecordPerspective(recordPerspectiveIDs);
			if (recordPerspective.getLabel().contains("ignore"))
				continue;

			PerspectiveElement perspective = new PerspectiveElement(recordPerspective, dmState);
			stratElements.add(perspective);
			radio.add(perspective);

			if (recordPerspective.getVirtualArray().getGroupList().size() == 1) {
				defaultPerspective = recordPerspective;
				defaultElement = perspective;
			}

		}

		if (defaultPerspective == null) {
			defaultPerspective = mRNAdd.getTable().getDefaultRecordPerspective();
			// todo find default element
		}
		// Set default data
		dmState.setPerspective(defaultPerspective);
		if (defaultElement != null) {
			defaultElement.setSelected(true);
		}
		// dmState.addDataDomain(mRNAdd);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		dmState.setPerspective(button.getLayoutDataAs(Perspective.class, null));
	}

	/**
	 * @return the dmState, see {@link #dmState}
	 */
	public DataMappingState getDmState() {
		return dmState;
	}

	public void dataSetChanged() {
		updatePathwayDatasets();
	}

}
