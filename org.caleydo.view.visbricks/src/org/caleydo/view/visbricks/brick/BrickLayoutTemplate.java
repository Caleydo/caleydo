package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutContainer;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;

/**
 * Layout LayoutTemplate for a Brick
 * 
 * @author Alexander Lex
 * 
 */
public class BrickLayoutTemplate extends LayoutTemplate {

	private GLBrick brick;
	private LayoutRenderer viewRenderer;

	public BrickLayoutTemplate(GLBrick brick) {
		this.brick = brick;
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("baseColumn");
		// setBaseElementLayout(baseColumn);
		// baseColumn.grabX();
		baseColumn.setFrameColor(0, 1, 0, 0);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 0);
		
		baseRow.setRenderer(new BorderedAreaRenderer());
		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(12);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));
		
		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(4);

		baseRow.appendElement(spacingLayoutX);
		baseRow.appendElement(baseColumn);
		baseRow.appendElement(spacingLayoutX);
//		baseRow.appendElement(fuelBarLayout);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(12);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer());
		viewLayout.setRenderer(viewRenderer);

		ElementLayout viewToolBarLayout = new ElementLayout("viewToolBarLayout");
		viewToolBarLayout.setFrameColor(0.5f, 0.5f, 0, 1);
		viewToolBarLayout.setPixelGLConverter(pixelGLConverter);
		viewToolBarLayout.setPixelSizeY(15);
		viewToolBarLayout.setRenderer(new ViewToolBarRenderer(brick));
		
		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(4);

//		baseColumn.appendElement(dimensionBarLayout);
		baseColumn.appendElement(spacingLayoutY);
		baseColumn.appendElement(fuelBarLayout);
		baseColumn.appendElement(spacingLayoutY);
		baseColumn.appendElement(viewLayout);
		baseColumn.appendElement(spacingLayoutY);
		baseColumn.appendElement(viewToolBarLayout);
		baseColumn.appendElement(spacingLayoutY);
		

	}

	void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

}
