package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
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
	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 4;

	private static final int EXPAND_BUTTON_ID = 0;

	private GLVisBricks visBricks;
	
	protected ArrayList<ElementLayout> footerBarElements;
	protected boolean showFooterBar;

	// private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	// private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public CompactBrickLayoutTemplate(GLBrick brick, GLVisBricks visBricks,
			DimensionGroup dimensionGroup, IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		footerBarElements = new ArrayList<ElementLayout>();
		configurer.configure(this);
		registerPickingListeners();
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

//		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
//		fuelBarLayout.setFrameColor(0, 1, 0, 1);
//
//		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
//		fuelBarLayout.setPixelSizeY(FUEL_BAR_HEIGHT_PIXELS);
//		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		baseRow.setRenderer(new BorderedAreaRenderer(brick));
		
		Row footerBar = createFooterBar();

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick,
					pixelGLConverter, 10, brick.getTextureManager(),
					HandleRenderer.MOVE_VERTICALLY_HANDLE));
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
		// viewRow.setPixelGLConverter(pixelGLConverter);
		// viewRow.setPixelSizeY(16);

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
				EPickingType.BRICK_EXPAND_BUTTON, EXPAND_BUTTON_ID,
				EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE), brick, brick
				.getTextureManager(), ButtonRenderer.TEXTURE_ROTATION_180));

		viewRow.append(viewLayout);
		viewRow.append(spacingLayoutX);
		viewRow.append(expandButtonLayout);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(footerBar);
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
	
	protected Row createFooterBar() {
		Row footerBar = new Row("footerBar");
		footerBar.setPixelGLConverter(pixelGLConverter);
		footerBar.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : footerBarElements) {
			footerBar.append(element);
		}

		return footerBar;
	}

	@Override
	protected void registerPickingListeners() {
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// DefaultBrickLayoutTemplate layoutTemplate = new
				// DefaultBrickLayoutTemplate(
				// brick, visBricks, dimensionGroup, brick
				// .getLayoutConfigurer());
				// brick.setBrickLayoutTemplate(layoutTemplate);
				brick.expand();
				// brick.setRemoteView(EContainedViewType.OVERVIEW_HEATMAP);
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_EXPAND_BUTTON, EXPAND_BUTTON_ID);

	}

	@Override
	public int getMinHeightPixels() {
		// FIXME This is dirty
		if (viewRenderer == null)
			return 3 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS;
		return 3 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS
				+ viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		//
		// return pixelGLConverter.getPixelWidthForGLWidth(dimensionGroup
		// .getMinWidth());
		// FIXME This is dirty
		if (viewRenderer == null)
			return 3 * SPACING_PIXELS + 60 // viewRenderer.getMinWidthPixels()
					+ BUTTON_WIDTH_PIXELS;
		return 3 * SPACING_PIXELS + viewRenderer.getMinWidthPixels()
				+ BUTTON_WIDTH_PIXELS;

	}

	// @Override
	// protected void setValidViewTypes() {
	// validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);
	// }

	// @Override
	// public EContainedViewType getDefaultViewType() {
	// return EContainedViewType.OVERVIEW_HEATMAP_COMPACT;
	// }

//	@Override
//	public void viewTypeChanged(EContainedViewType viewType) {
//
//	}

	@Override
	public void setLockResizing(boolean lockResizing) {
		// TODO Auto-generated method stub

	}

	@Override
	public ABrickLayoutTemplate getCollapsedLayoutTemplate() {
		return this;
	}

	@Override
	public ABrickLayoutTemplate getExpandedLayoutTemplate() {
		return new DefaultBrickLayoutTemplate(brick, visBricks, dimensionGroup,
				brick.getBrickConfigurer());
	}

	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}
	
	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

	// @Override
	// public void configure(IBrickLayoutConfigurer configurer) {
	// configurer.configure(this);
	// }

}
