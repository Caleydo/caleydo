package org.caleydo.view.histogram;

import java.util.List;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IRemoteViewCreator;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;

public class HistogramRemoteViewCreator implements IRemoteViewCreator {

	public HistogramRemoteViewCreator() {
	}

	@Override
	public AGLView createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives) {
		GLHistogram histogramView = (GLHistogram) GeneralManager
				.get()
				.getViewManager()
				.createGLView(GLHistogram.class, remoteRenderingView.getParentGLCanvas(),
						remoteRenderingView.getParentComposite(),
						new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 1, 0, 1, -1, 1));

		histogramView.setRemoteRenderingGLView((IGLRemoteRenderingView) remoteRenderingView);
		TablePerspective tablePerspective = null;
		if (tablePerspectives.size() > 0) {
			tablePerspective = tablePerspectives.get(0);

			ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

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
				histogram = tablePerspective.getContainerStatistics().getHistogram();
			}

			histogramView.setDataDomain(dataDomain);
			histogramView.setHistogram(histogram);
		}
		histogramView.initialize();
		histogramView.setDetailLevel(EDetailLevel.LOW);

		// Ctable.getContentData(Set.CONTENT)
		// if (recordVA != null)
		// histogram.setRecordVA(recordVA);

		return histogramView;
	}

}
