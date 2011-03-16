package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.APickingListener;
import org.caleydo.view.visbricks.brick.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.Button;
import org.caleydo.view.visbricks.brick.ButtonRenderer;
import org.caleydo.view.visbricks.brick.FuelBarRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.HandleRenderer;
import org.caleydo.view.visbricks.brick.RelationIndicatorRenderer;

public class CompactBrickLayoutTemplate extends ABrickLayoutTemplate {

	private static final int EXPAND_BUTTON_ID = 0;

	private GLVisBricks visBricks;
	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public CompactBrickLayoutTemplate(GLBrick brick, GLVisBricks visBricks) {
		super(brick);
		this.visBricks = visBricks;
		leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, true);
		rightRelationIndicatorRenderer = new RelationIndicatorRenderer(
				brick, visBricks, false);
	}

	public void setLeftRelationIndicatorRenderer(
			RelationIndicatorRenderer leftRelationIndicatorRenderer) {
		this.leftRelationIndicatorRenderer = leftRelationIndicatorRenderer;
	}

	public void setRightRelationIndicatorRenderer(
			RelationIndicatorRenderer rightRelationIndicatorRenderer) {
		this.rightRelationIndicatorRenderer = rightRelationIndicatorRenderer;
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);
		
		leftRelationIndicatorRenderer.updateRelations();
		rightRelationIndicatorRenderer.updateRelations();

		ElementLayout leftRelationIndicatorLayout = new ElementLayout(
				"RightRelationIndicatorLayout");
		// rightRelationIndicatorLayout.setDebug(true);
		leftRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		leftRelationIndicatorLayout.setPixelSizeX(3);
		leftRelationIndicatorLayout.setRenderer(leftRelationIndicatorRenderer);
		baseRow.append(leftRelationIndicatorLayout);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 1);
//		baseColumn.setDebug(true);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 1);

		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(4);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		baseRow.setRenderer(new BorderedAreaRenderer());

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick
					.getDimensionGroup(), pixelGLConverter, 10, brick
					.getTextureManager()));
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(4);
		spacingLayoutX.setPixelSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(12);

		Row viewRow = new Row("viewRow");
		viewRow.setFrameColor(0, 0, 1, 1);
//		viewRow.setDebug(true);
		viewRow.setPixelGLConverter(pixelGLConverter);
		viewRow.setPixelSizeY(16);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
//		viewLayout.setDebug(true);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		ElementLayout expandButtonLayout = new ElementLayout(
				"expandButtonLayout");
		expandButtonLayout.setFrameColor(1, 0, 0, 1);
//		expandButtonLayout.setDebug(true);
		expandButtonLayout.setPixelGLConverter(pixelGLConverter);
		expandButtonLayout.setPixelSizeX(16);
		expandButtonLayout.setPixelSizeY(16);
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
		spacingLayoutY.setPixelSizeY(4);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(fuelBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(viewRow);
		baseColumn.append(spacingLayoutY);

		ElementLayout rightRelationIndicatorLayout = new ElementLayout(
				"RightRelationIndicatorLayout");
		// rightRelationIndicatorLayout.setDebug(true);
		rightRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		rightRelationIndicatorLayout.setPixelSizeX(3);
		rightRelationIndicatorLayout
				.setRenderer(rightRelationIndicatorRenderer);
		baseRow.append(rightRelationIndicatorLayout);

	}

	@Override
	protected void registerPickingListeners() {
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				DefaultBrickLayoutTemplate layoutTemplate = new DefaultBrickLayoutTemplate(brick, visBricks);
				brick.setBrickLayoutTemplate(layoutTemplate);
				brick.setRemoteView(GLBrick.OVERVIEW_HEATMAP);
				layoutTemplate.updateToolBarButtons();
			}
		}, EPickingType.BRICK_EXPAND_BUTTON, EXPAND_BUTTON_ID);
		
	}
	
	@Override
	public int getMinHeightPixels() {
		return 32;
	}

	@Override
	public int getMinWidthPixels() {
		//TODO: implement
		return 0;
	}

}
