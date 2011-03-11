package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.caleydo.view.visbricks.brick.APickingListener;
import org.caleydo.view.visbricks.brick.ButtonRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.eclipse.swt.widgets.Shell;

public abstract class BrickLayoutTemplate extends LayoutTemplate {

	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int HISTOGRAM_BUTTON_ID = 3;

	protected GLBrick brick;
	protected LayoutRenderer viewRenderer;
	protected boolean pickingListenersRegistered;

	public BrickLayoutTemplate(GLBrick brick) {
		this.brick = brick;
		pickingListenersRegistered = false;
	}

	public void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	protected Row createBrickToolBar(int pixelHeight) {
		Row toolBar = new Row();
		toolBar.setPixelGLConverter(pixelGLConverter);
		toolBar.setPixelSizeY(pixelHeight);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(4);

		ElementLayout heatMapButtonLayout = new ElementLayout("heatMapButton");
		heatMapButtonLayout.setPixelGLConverter(pixelGLConverter);
		heatMapButtonLayout.setPixelSizeX(pixelHeight);
		heatMapButtonLayout.setPixelSizeY(pixelHeight);
		heatMapButtonLayout.setRenderer(new ButtonRenderer(brick,
				EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID,
				EIconTextures.HEAT_MAP_ICON, brick.getTextureManager()));
		
		toolBar.appendElement(heatMapButtonLayout);
		toolBar.appendElement(spacingLayoutX);
		
		ElementLayout parCoordsButtonLayout = new ElementLayout("parCoords");
		parCoordsButtonLayout.setPixelGLConverter(pixelGLConverter);
		parCoordsButtonLayout.setPixelSizeX(pixelHeight);
		parCoordsButtonLayout.setPixelSizeY(pixelHeight);
		parCoordsButtonLayout.setRenderer(new ButtonRenderer(brick,
				EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID,
				EIconTextures.PAR_COORDS_ICON, brick.getTextureManager()));
		
		toolBar.appendElement(parCoordsButtonLayout);
		toolBar.appendElement(spacingLayoutX);
		
		
		
		ElementLayout histogramButtonLayout = new ElementLayout("histogramButton");
		histogramButtonLayout.setPixelGLConverter(pixelGLConverter);
		histogramButtonLayout.setPixelSizeX(pixelHeight);
		histogramButtonLayout.setPixelSizeY(pixelHeight);
		histogramButtonLayout.setRenderer(new ButtonRenderer(brick,
				EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID,
				EIconTextures.HISTOGRAM_ICON, brick.getTextureManager()));
		
		toolBar.appendElement(histogramButtonLayout);
		toolBar.appendElement(spacingLayoutX);
		
		ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
		clusterButtonLayout.setPixelGLConverter(pixelGLConverter);
		clusterButtonLayout.setPixelSizeX(pixelHeight);
		clusterButtonLayout.setPixelSizeY(pixelHeight);
		clusterButtonLayout.setRenderer(new ButtonRenderer(brick,
				EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, 1,
				EIconTextures.CLUSTER_ICON, brick.getTextureManager()));
		
		toolBar.appendElement(clusterButtonLayout);
		
		
		registerPickingListeners();

		return toolBar;
	}

	protected void registerPickingListeners() {

		if (pickingListenersRegistered)
			return;

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.HEATMAP_VIEW);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.PARCOORDS_VIEW);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.HISTOGRAM_VIEW);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID);
		
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// set.cluster(clusterState);
				System.out.println("cluster");

				brick.getParentGLCanvas().getParentComposite().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								StartClusteringDialog dialog = new StartClusteringDialog(
										new Shell(), brick.getDataDomain());
								dialog.open();
								ClusterState clusterState = dialog
										.getClusterState();
								if (clusterState == null)
									return;

								StartClusteringEvent event = null;
								// if (clusterState != null && set != null)

								event = new StartClusteringEvent(clusterState,
										brick.getSet().getID());
								event.setDataDomainType(brick.getDataDomain()
										.getDataDomainType());
								GeneralManager.get().getEventPublisher()
										.triggerEvent(event);
							}
						});

			}
		}, EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, 1);

		pickingListenersRegistered = true;
	}


}
