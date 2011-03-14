package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.visbricks.brick.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.FuelBarRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ViewToolBarRenderer;

public class CentralBrickLayoutTemplate extends BrickLayoutTemplate {

	public CentralBrickLayoutTemplate(GLBrick brick) {
		super(brick);
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("brickBaseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("brickBaseColumn");
		// baseColumn.setDebug(true);

		// setBaseElementLayout(baseColumn);
		// baseColumn.grabX();
		baseColumn.setFrameColor(0, 1, 0, 1);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 1);

		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(12);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		baseRow.append(baseColumn);
		// baseRow.appendElement(fuelBarLayout);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 1);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(12);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer());
		viewLayout.setRenderer(viewRenderer);

		ElementLayout viewToolBarLayout = new ElementLayout("viewToolBarLayout");
		viewToolBarLayout.setFrameColor(0.5f, 0.5f, 0, 1);
		viewToolBarLayout.setPixelGLConverter(pixelGLConverter);
		viewToolBarLayout.setPixelSizeY(16);
		viewToolBarLayout.setRenderer(new ViewToolBarRenderer(brick));

		Row toolBar = createBrickToolBar(16);

		// toolBar.setYDynamic(true);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(4);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(fuelBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(viewLayout);
		baseColumn.append(spacingLayoutY);
		// baseColumn.appendElement(viewToolBarLayout);
		baseColumn.append(toolBar);

	}
}
