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
package org.caleydo.view.heatmap.uncertainty;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class OverviewRenderer extends LayoutRenderer {

	private ClusterRenderer clusterRenderer;

	private final static int CLUSTER_SPACER_SIZE = 10;

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private GLHeatMap detailHeatMap;

	private ElementLayout lineSeparatorLayout;

	private Column overviewLayout;

	private int selectedClusterIndex = 0;

	private List<Row> clusterLayoutList = new ArrayList<Row>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public OverviewRenderer(GLUncertaintyHeatMap uncertaintyHeatMap, Column overviewLayout) {
		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.overviewLayout = overviewLayout;
		Zoomer zoomer = new Zoomer(uncertaintyHeatMap, overviewLayout);
		this.overviewLayout.setZoomer(zoomer);
	}

	public void init(GL2 gl) {

		// overviewLayout.clear();
		clusterLayoutList.clear();

		RecordVirtualArray recordVA = uncertaintyHeatMap.getTablePerspective()
				.getRecordPerspective().getVirtualArray();
		RecordGroupList clusterList = recordVA.getGroupList();

		int counter = 0;

		int lastLayoutElement = 0;

		// If the dataset is unclustered yet, the whole VA is given to the
		// detail heat map.
		if (clusterList == null && detailHeatMap != null) {
			detailHeatMap.setTablePerspective(uncertaintyHeatMap.getTablePerspective());
			detailHeatMap.setDisplayListDirty();
		}

		if (clusterList != null) {
			// int totalSpacerSize = spacerSize * (clusterList.size() - 1);
			for (int clusterIndex = 0; clusterIndex < clusterList.size(); clusterIndex++) {

				// creatinng Texture for each cluster

				// creating Layout for each cluster
				TablePerspective clusterContainer = this.getClusterContainer(clusterIndex);
				float ratio = (float) clusterContainer.getNrRecords()
						/ ((float) recordVA.getIDs().size());

				Row clusterLayout = new Row("clusterLayout_" + counter);
				clusterLayout.setRatioSizeY(ratio);
				clusterLayoutList.add(clusterLayout);

				clusterRenderer = new ClusterRenderer(uncertaintyHeatMap, clusterLayout,
						clusterContainer.getRecordPerspective().getVirtualArray(),
						clusterIndex);
				clusterLayout.setRenderer(clusterRenderer);

				overviewLayout.add(lastLayoutElement, clusterLayout);

				clusterRenderer.init(gl);
				counter++;

				if (clusterIndex < (clusterList.size() - 1)) {
					lineSeparatorLayout = new ElementLayout("lineSeparator");
					PixelGLConverter pixelGLConverter = uncertaintyHeatMap
							.getPixelGLConverter();
					lineSeparatorLayout.setPixelSizeY(CLUSTER_SPACER_SIZE);
					lineSeparatorLayout.setRatioSizeX(1);
					lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.3f);

					overviewLayout.add(lastLayoutElement, lineSeparatorLayout);
					// overviewLayout.append(lineSeparatorLayout);
				}

				// Initially the first cluster gets selected
				if (clusterIndex == 0 && detailHeatMap != null) {
					detailHeatMap.setTablePerspective(clusterContainer);
					detailHeatMap.setDisplayListDirty();
				}
			}
		} else {
			Row clusterLayout = new Row("clusterLayout");
			clusterLayout.setRatioSizeY(1);

			clusterRenderer = new ClusterRenderer(uncertaintyHeatMap, clusterLayout,
					recordVA, 0);
			clusterLayout.setRenderer(clusterRenderer);
			clusterRenderer.init(gl);
			overviewLayout.add(lastLayoutElement, clusterLayout);
		}

		overviewLayout.updateSubLayout();
	}

	public TablePerspective getClusterContainer(int clusterIndex) {
		TablePerspective sourceContainer = uncertaintyHeatMap.getTablePerspective();
		if (sourceContainer.getRecordPerspective().getVirtualArray().getGroupList()
				.size() == 1) {
			return sourceContainer;
		}

		TablePerspective clusterContainer = new TablePerspective();
		clusterContainer.setDataDomain(sourceContainer.getDataDomain());
		clusterContainer.setDimensionPerspective(sourceContainer
				.getDimensionPerspective());

		List<Integer> embeddedRecords = sourceContainer.getRecordPerspective()
				.getVirtualArray().getIDsOfGroup(clusterIndex);

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(embeddedRecords);

		RecordPerspective clusterRecordPerspective = new RecordPerspective(
				sourceContainer.getDataDomain());

		clusterRecordPerspective.init(data);

		clusterContainer.setRecordPerspective(clusterRecordPerspective);

		return clusterContainer;
	}

	public void setSelectedGroup(int selectedGroup) {
		this.selectedClusterIndex = selectedGroup;
	}

	public float getSelectedClusterY() {

		if (clusterLayoutList.size() == 0)
			return 0;

		return clusterLayoutList.get(selectedClusterIndex).getTranslateY()
				- overviewLayout.getTranslateY();
	}

	public float getSelectedClusterHeight() {

		if (clusterLayoutList.size() == 0)
			return uncertaintyHeatMap.getViewFrustum().getHeight();

		return clusterLayoutList.get(selectedClusterIndex).getSizeScaledY();
	}

	public ArrayList<ClusterRenderer> getClusterRendererList() {
		ArrayList<ClusterRenderer> ret = new ArrayList<ClusterRenderer>();
		for (Row layout : clusterLayoutList) {
			ret.add((ClusterRenderer) layout.getRenderer());
		}
		if (ret.size() == 0) {
			ret.add(clusterRenderer);
		}
		return ret;
	}

	public void setDetailHeatMap(GLHeatMap detailHeatMap) {
		this.detailHeatMap = detailHeatMap;
	}

	public GLUncertaintyHeatMap getUncertaintyHeatMap() {
		return uncertaintyHeatMap;
	}

	@Override
	public void renderContent(GL2 gl) {

	}
	
	@Override
	protected boolean permitsDisplayLists() {
		return false;
	}
}
