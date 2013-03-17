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
package org.caleydo.view.enroute.path.node.mode;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.pathway.ESampleMappingMode;

/**
 * @author Alexander Lex
 *
 */
public class MappingRenderer extends ColorRenderer {

	private List<TablePerspective> tablePerspectives;
	private TablePerspective mappedPerspective;
	private ESampleMappingMode sampleMappingMode;

	private IDType idType = IDType.getIDType("DAVID");

	private APathwayPathRenderer pathRenderer;
	private ALinearizableNode node;

	/**
	 *
	 */
	public MappingRenderer(APathwayPathRenderer pathRenderer, ALinearizableNode node) {
		super(new float[] { 0, 0, 0, 1f });
		this.pathRenderer = pathRenderer;
		this.node = node;

	}

	@Override
	protected void renderContent(GL2 gl) {
		TablePerspective mappedPerspective = pathRenderer.getMappedPerspective();
		List<Integer> ids = node.getMappedDavidIDs();
		if (mappedPerspective != null && ids != null) {

			Average average = mappedPerspective.getContainerStatistics().getAverage(idType, ids.get(0));
			setColor(mappedPerspective.getDataDomain().getColorMapper().getColor((float) average.getArithmeticMean()));

		} else {
			setColor(new float[] { 1, 1, 1, 1 });
		}
		super.renderContent(gl);

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * @param sampleMappingMode
	 *            setter, see {@link sampleMappingMode}
	 */
	public void setSampleMappingMode(ESampleMappingMode sampleMappingMode) {
		this.sampleMappingMode = sampleMappingMode;
	}

	/**
	 * @param mappedPerspective
	 *            setter, see {@link mappedPerspective}
	 */
	public void setMappedPerspective(TablePerspective mappedPerspective) {
		this.mappedPerspective = mappedPerspective;
	}

}
