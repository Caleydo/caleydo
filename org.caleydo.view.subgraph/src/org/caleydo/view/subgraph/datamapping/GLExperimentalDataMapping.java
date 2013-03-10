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
import org.caleydo.core.view.opengl.layout2.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.view.subgraph.GLSubGraph;

/**
 * Data mapping for selecting datasets and perspectives.
 *
 * @author Marc Streit
 * 
 */
public class GLExperimentalDataMapping extends AnimatedGLElementContainer {// implements ISelectionCallback {

	protected final GLSubGraph view;

	public GLExperimentalDataMapping(GLSubGraph view) {
		this.view = view;

		setLayout(new GLFlowLayout(false, 2, new GLPadding(2)));
		setSize(-1, 40);

		GLElementContainer ddElements = new GLElementContainer(new GLFlowLayout(true, 10, new GLPadding(5)));
		this.add(ddElements);

		// FIXME: from which dataset should we take the stratifications? currently we always take them from mRNA
		ATableBasedDataDomain mRNAdd = null;

		ddElements.add(new GLElement() {
			@Override
			protected void renderImpl(GLGraphics g, float w, float h) {
				g.drawText("Data sets:", 0, -2, w, h);
			}
		}.setSize(100, 15));
		// RadioController radio = new RadioController(this);
		for (ATableBasedDataDomain dd : DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class)) {
			TableBasedDataDomainElement ddElement = new TableBasedDataDomainElement(dd, view.getPathEventSpace());
			ddElements.add(ddElement);
			// radio.add(ddElement);

			if (dd.getLabel().equals("mRNA"))
				mRNAdd = dd;
		}

		GLElementContainer stratElements = new GLElementContainer(new GLFlowLayout(true, 10, new GLPadding(5)));
		this.add(stratElements);

		if (mRNAdd == null)
			return;

		stratElements.add(new GLElement() {
			@Override
			protected void renderImpl(GLGraphics g, float w, float h) {
				g.drawText("Stratifications:", 0, -2, w, h);
			}
		}.setSize(100, 15));

		for (String recordPerspectiveIDs : mRNAdd.getRecordPerspectiveIDs()) {
			stratElements.add(new PerspectiveElement(mRNAdd.getTable().getRecordPerspective(recordPerspectiveIDs), view
					.getPathEventSpace()));
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		// TODO: render stuff here that can not be rendered in children
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		// TODO: render stuff here that can not be rendered in children, for picking (this should be simple objects, if
		// possible)
	}

	// @Override
	// public void onSelectionChanged(GLButton button, boolean selected) {
	// System.out.println("RADIO");
	// }
}
