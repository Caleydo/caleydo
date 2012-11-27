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
package org.caleydo.view.stratomex.brick.viewcreation;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Creator for a remote rendered {@link GLHistogram}.
 *
 * @author Christian Partl
 *
 */
public class HistogramCreator implements IRemoteViewCreator {

	public HistogramCreator() {

	}

	@Override
	public AGLView createRemoteView(GLBrick remoteRenderingView, GL2 gl, GLMouseListener glMouseListener) {

		GLHistogram histogramView = (GLHistogram) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLHistogram.class, remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		histogramView.setRemoteRenderingGLView(remoteRenderingView);

		ATableBasedDataDomain dataDomain = remoteRenderingView.getDataDomain();
		TablePerspective tablePerspective = remoteRenderingView.getTablePerspective();

		Histogram histogram = null;

		if (dataDomain.getLabel().toLowerCase().contains("copy")) {
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(), tablePerspective
					.getRecordPerspective().getVirtualArray(), tablePerspective.getDimensionPerspective()
					.getVirtualArray(), 5);
		} else if (dataDomain.getLabel().toLowerCase().contains("mutation")) {
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(), tablePerspective
					.getRecordPerspective().getVirtualArray(), tablePerspective.getDimensionPerspective()
					.getVirtualArray(), 2);

		} else {
			histogram = remoteRenderingView.getTablePerspective().getContainerStatistics().getHistogram();
		}

		histogramView.setDataDomain(dataDomain);
		histogramView.setHistogram(histogram);
		histogramView.initialize();
		histogramView.initRemote(gl, remoteRenderingView, glMouseListener);
		histogramView.setDetailLevel(EDetailLevel.LOW);

		// Ctable.getContentData(Set.CONTENT)
		// if (recordVA != null)
		// histogram.setRecordVA(recordVA);

		return histogramView;
	}

}
