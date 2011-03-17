package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.ui.Button;
import org.caleydo.view.visbricks.brick.ui.ButtonRenderer;
import org.caleydo.view.visbricks.brick.ui.FuelBarRenderer;
import org.caleydo.view.visbricks.brick.ui.HandleRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Brick layout for a compact overview containing a view and a small fuel bar.
 * 
 * @author Christian Partl
 * 
 */
public class CompactBrickLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 10;
	protected static final int FUEL_BAR_HEIGHT_PIXELS = 4;

	private static final int EXPAND_BUTTON_ID = 0;

	private GLVisBricks visBricks;

	// private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	// private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public CompactBrickLayoutTemplate(GLBrick brick, GLVisBricks visBricks,
			DimensionGroup dimensionGroup) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		// leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
		// visBricks, true);
		// rightRelationIndicatorRenderer = new RelationIndicatorRenderer(
		// brick, visBricks, false);
	}

	// public void setLeftRelationIndicatorRenderer(
	// RelationIndicatorRenderer leftRelationIndicatorRenderer) {
	// this.leftRelationIndicatorRenderer = leftRelationIndicatorRenderer;
	// }
	//
	// public void setRightRelationIndicatorRenderer(
	// RelationIndicatorRenderer rightRelationIndicatorRenderer) {
	// this.rightRelationIndicatorRenderer = rightRelationIndicatorRenderer;
	// }

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		// leftRelationIndicatorRenderer.updateRelations();
		// rightRelationIndicatorRenderer.updateRelations();

		// ElementLayout leftRelationIndicatorLayout = new ElementLayout(
		// "RightRelationIndicatorLayout");
		// // rightRelationIndicatorLayout.setDebug(true);
		// leftRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		// leftRelationIndicatorLayout.setPixelSizeX(3);
		// leftRelationIndicatorLayout.setRenderer(leftRelationIndicatorRenderer);
		// baseRow.append(leftRelationIndicatorLayout);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 1);
		// baseColumn.setDebug(true);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 1);

		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(FUEL_BAR_HEIGHT_PIXELS);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		baseRow.setRenderer(new BorderedAreaRenderer());

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick
					.getDimensionGroup(), pixelGLConverter, 10, brick
					.getTextureManager()));
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row viewRow = new Row("compactViewRow");
		viewRow.setFrameColor(0, 0, 1, 1);
		// viewRow.setDebug(true);
		viewRow.setPixelGLConverter(pixelGLConverter);
		viewRow.setPixelSizeY(16);

		ElementLayout viewLayout = new ElementLayout("compactViewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		// viewLayout.setDebug(true);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		ElementLayout expandButtonLayout = new ElementLayout(
				"expandButtonLayout");
		expandButtonLayout.setFrameColor(1, 0, 0, 1);
		// expandButtonLayout.setDebug(true);
		expandButtonLayout.setPixelGLConverter(pixelGLConverter);
		expandButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		// expandButtonLayout.setRatioSizeX(0.2f);
		expandButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		expandButtonLayout.setRenderer(new ButtonRenderer(new Button(
				EPickingType.BRICK_EXPAND_BUTTON, EXPAND_BUTTON_ID), brick,
				EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE, brick
						.getTextureManager(),
				ButtonRenderer.TEXTURE_ROTATION_180));

		viewRow.append(viewLayout);
		viewRow.append(spacingLayoutX);
		viewRow.append(expandButtonLayout);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(fuelBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(viewRow);
		baseColumn.append(spacingLayoutY);

		// ElementLayout rightRelationIndicatorLayout = new ElementLayout(
		// "RightRelationIndicatorLayout");
		// // rightRelationIndicatorLayout.setDebug(true);
		// rightRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		// rightRelationIndicatorLayout.setPixelSizeX(3);
		// rightRelationIndicatorLayout
		// .setRenderer(rightRelationIndicatorRenderer);
		// baseRow.append(rightRelationIndicatorLayout);

	}

	@Override
	protected void registerPickingListeners() {
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				DefaultBrickLayoutTemplate layoutTemplate = new DefaultBrickLayoutTemplate(
						brick, visBricks, dimensionGroup);
				brick.setBrickLayoutTemplate(layoutTemplate);
				brick.setRemoteView(EContainedViewType.OVERVIEW_HEATMAP);
				layoutTemplate.updateToolBarButtons(EContainedViewType.OVERVIEW_HEATMAP);
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_EXPAND_BUTTON, EXPAND_BUTTON_ID);

	}

	@Override
	public int getMinHeightPixels() {
		return 3 * SPACING_PIXELS + FUEL_BAR_HEIGHT_PIXELS
				+ viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		// TODO: implement
		return 0;
	}

	@Override
	protected void setValidViewTypes() {
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);
	}

	@Override
	public EContainedViewType getDefaultViewType() {
		return EContainedViewType.OVERVIEW_HEATMAP_COMPACT;
	}

}
