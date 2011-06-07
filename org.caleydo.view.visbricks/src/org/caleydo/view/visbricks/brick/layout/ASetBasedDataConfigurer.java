package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.picking.APickingListener;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.gui.StartClusteringDialog;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.visbricks.brick.ui.DimensionBarRenderer;
import org.caleydo.view.visbricks.brick.ui.FuelBarRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupCaptionRenderer;
import org.eclipse.swt.widgets.Shell;

public abstract class ASetBasedDataConfigurer implements IBrickConfigurer {

	protected static final int FUELBAR_HEIGHT_PIXELS = 4;
	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int DIMENSION_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 8;
	protected static final int SPACING_PIXELS = 4;

	protected static final int CLUSTER_BUTTON_ID = 1;
	
	protected ISet set;
	
	public ASetBasedDataConfigurer(ISet set) {
		this.set = set;
	}

	protected ArrayList<ElementLayout> createHeaderBarElements(
			CentralBrickLayoutTemplate layoutTemplate) {

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();
		final GLBrick brick = layoutTemplate.getBrick();

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				layoutTemplate.getDimensionGroup(), set.getLabel());
		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);
		headerBarElements.add(spacingLayoutX);

		Button clusterButton = new Button(
				EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, CLUSTER_BUTTON_ID,
				EIconTextures.CLUSTER_ICON);
		ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
		clusterButtonLayout.setPixelGLConverter(pixelGLConverter);
		clusterButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		clusterButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		clusterButtonLayout.setRenderer(new ButtonRenderer(clusterButton,
				brick, brick.getTextureManager()));

		headerBarElements.add(clusterButtonLayout);
		// headerBarElements.add(spacingLayoutX);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
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
										set.getID());
								event.setDataDomainType(brick.getDataDomain()
										.getDataDomainType());
								GeneralManager.get().getEventPublisher()
										.triggerEvent(event);
							}
						});
			}
		}, EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, CLUSTER_BUTTON_ID);

		return headerBarElements;
	}

	protected ArrayList<ElementLayout> createHeaderBarElements(
			CompactCentralBrickLayoutTemplate layoutTemplate) {

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				layoutTemplate.getDimensionGroup(), set.getLabel());
		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);

		return headerBarElements;
	}

	protected void registerViewSwitchingButtons(
			final ABrickLayoutTemplate layoutTemplate,
			final ArrayList<BrickViewSwitchingButton> viewSwitchingButtons,
			final GLBrick brick, final DimensionGroup dimensionGroup) {

		for (final BrickViewSwitchingButton button : viewSwitchingButtons) {

			brick.addPickingListener(new APickingListener() {

				@Override
				public void clicked(Pick pick) {
					for (BrickViewSwitchingButton button : viewSwitchingButtons) {
						button.setSelected(false);
					}
					button.setSelected(true);

					if (!(layoutTemplate instanceof CentralBrickLayoutTemplate)
							&& dimensionGroup.isGlobalViewSwitching()) {
						dimensionGroup.switchBrickViews(button.getViewType());
					} else {
						brick.setContainedView(button.getViewType());
					}
					dimensionGroup.updateLayout();
				}
			}, button.getPickingType(), button.getButtonID());

			layoutTemplate.registerViewTypeChangeListener(button);
		}
	}

	protected ArrayList<ElementLayout> createToolBarElements(
			CentralBrickLayoutTemplate layoutTemplate,
			ArrayList<BrickViewSwitchingButton> viewSwitchingButtons) {

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();
		final GLBrick brick = layoutTemplate.getBrick();
		final DimensionGroup dimensionGroup = layoutTemplate
				.getDimensionGroup();

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		for (int i = 0; i < viewSwitchingButtons.size(); i++) {
			BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
			ElementLayout buttonLayout = new ElementLayout();
			buttonLayout.setPixelGLConverter(pixelGLConverter);
			buttonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
			buttonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
			buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick
					.getTextureManager()));
			toolBarElements.add(buttonLayout);
			if (i != viewSwitchingButtons.size() - 1) {
				toolBarElements.add(spacingLayoutX);
			}
		}

		// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		registerViewSwitchingButtons(layoutTemplate, viewSwitchingButtons,
				brick, dimensionGroup);

		return toolBarElements;
	}

	protected ArrayList<ElementLayout> createFooterBarElements(
			CentralBrickLayoutTemplate layoutTemplate) {
		return createSummaryFooterBarElements(layoutTemplate);

	}

	protected ArrayList<ElementLayout> createFooterBarElements(
			CompactCentralBrickLayoutTemplate layoutTemplate) {
		return createSummaryFooterBarElements(layoutTemplate);

	}

	private ArrayList<ElementLayout> createSummaryFooterBarElements(
			ABrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> footerBarElements = new ArrayList<ElementLayout>();

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();
		GLBrick brick = layoutTemplate.getBrick();

		ElementLayout dimensionBarLaylout = new ElementLayout("dimensionBar");
		dimensionBarLaylout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLaylout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);
		dimensionBarLaylout.setRatioSizeX(1);
		dimensionBarLaylout.setRenderer(new DimensionBarRenderer(brick.getDataDomain()
				.getStorageVA(Set.STORAGE), set.getStorageData(Set.STORAGE).getStorageVA()));

		footerBarElements.add(dimensionBarLaylout);

		return footerBarElements;

	}

	protected ArrayList<ElementLayout> createFooterBarElements(
			DefaultBrickLayoutTemplate layoutTemplate) {

		return createDefaultFooterBarElements(layoutTemplate);
	}
	
	protected ArrayList<ElementLayout> createFooterBarElements(
			DetailBrickLayoutTemplate layoutTemplate) {

		return createDefaultFooterBarElements(layoutTemplate);
	}
	
	protected ArrayList<ElementLayout> createFooterBarElements(
			CompactBrickLayoutTemplate layoutTemplate) {

		return createDefaultFooterBarElements(layoutTemplate);
	}
	
	private ArrayList<ElementLayout> createDefaultFooterBarElements(
			ABrickLayoutTemplate layoutTemplate) {
		ArrayList<ElementLayout> footerBarElements = new ArrayList<ElementLayout>();

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();
		GLBrick brick = layoutTemplate.getBrick();

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 0);
		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(FUELBAR_HEIGHT_PIXELS);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick, set));

		footerBarElements.add(fuelBarLayout);

		return footerBarElements;
	}

	protected ArrayList<ElementLayout> createToolBarElements(
			ABrickLayoutTemplate layoutTemplate,
			ArrayList<BrickViewSwitchingButton> viewSwitchingButtons) {

		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();
		final GLBrick brick = layoutTemplate.getBrick();
		final DimensionGroup dimensionGroup = layoutTemplate
				.getDimensionGroup();

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		for (int i = 0; i < viewSwitchingButtons.size(); i++) {
			BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
			ElementLayout buttonLayout = new ElementLayout();
			buttonLayout.setPixelGLConverter(pixelGLConverter);
			buttonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
			buttonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
			buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick
					.getTextureManager()));
			toolBarElements.add(buttonLayout);
			if (i != viewSwitchingButtons.size() - 1) {
				toolBarElements.add(spacingLayoutX);
			}
		}

		// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		registerViewSwitchingButtons(layoutTemplate, viewSwitchingButtons,
				brick, dimensionGroup);

		return toolBarElements;
	}

}
