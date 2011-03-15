package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.FuelBarRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.HandleRenderer;
import org.caleydo.view.visbricks.brick.RelationIndicatorRenderer;

/**
 * Layout LayoutTemplate for a Brick
 * 
 * @author Alexander Lex
 * 
 */
public class DefaultBrickLayoutTemplate extends BrickLayoutTemplate {

	private GLVisBricks visBricks;
	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public DefaultBrickLayoutTemplate(GLBrick brick, GLVisBricks visBricks) {
		super(brick);
		this.visBricks = visBricks;
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

		ElementLayout leftRelationIndicatorLayout = new ElementLayout(
				"RightRelationIndicatorLayout");
		// rightRelationIndicatorLayout.setDebug(true);
		leftRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		leftRelationIndicatorLayout.setPixelSizeX(3);
		leftRelationIndicatorLayout.setRenderer(leftRelationIndicatorRenderer);
		baseRow.append(leftRelationIndicatorLayout);

		Column baseColumn = new Column("baseColumn");
		// setBaseElementLayout(baseColumn);
		// baseColumn.grabX();
		baseColumn.setFrameColor(0, 1, 0, 0);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 0);

		baseRow.setRenderer(new BorderedAreaRenderer());
		baseRow.addForeGroundRenderer(new HandleRenderer(brick.getDimensionGroup(),
				pixelGLConverter, 10));
		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(12);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(4);
		spacingLayoutX.setRatioSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);
		// baseRow.appendElement(fuelBarLayout);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(12);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer());
		viewLayout.setRenderer(viewRenderer);

		// ElementLayout viewToolBarLayout = new
		// ElementLayout("viewToolBarLayout");
		// viewToolBarLayout.setFrameColor(0.5f, 0.5f, 0, 1);
		// viewToolBarLayout.setPixelGLConverter(pixelGLConverter);
		// viewToolBarLayout.setPixelSizeY(15);
		// viewToolBarLayout.setRenderer(new ViewToolBarRenderer(brick));

		Row toolBar = createBrickToolBar(16);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(4);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(fuelBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(viewLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(toolBar);
		baseColumn.append(spacingLayoutY);

		ElementLayout rightRelationIndicatorLayout = new ElementLayout(
				"RightRelationIndicatorLayout");
		// rightRelationIndicatorLayout.setDebug(true);
		rightRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		rightRelationIndicatorLayout.setPixelSizeX(3);
		rightRelationIndicatorLayout.setRenderer(rightRelationIndicatorRenderer);
		baseRow.append(rightRelationIndicatorLayout);

	}
}
