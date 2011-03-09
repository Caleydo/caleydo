package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;

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

		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(15);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		baseRow.appendElement(baseColumn);
//		baseRow.appendElement(fuelBarLayout);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(15);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.setRenderer(viewRenderer);

		ElementLayout viewToolBarLayout = new ElementLayout("viewToolBarLayout");
		viewToolBarLayout.setFrameColor(0.5f, 0.5f, 0, 1);
		viewToolBarLayout.setPixelGLConverter(pixelGLConverter);
		viewToolBarLayout.setPixelSizeY(15);
		viewToolBarLayout.setRenderer(new ViewToolBarRenderer(brick));

//		baseColumn.appendElement(dimensionBarLayout);
		baseColumn.appendElement(fuelBarLayout);
		baseColumn.appendElement(viewLayout);
		baseColumn.appendElement(viewToolBarLayout);
		

	}

	void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

}
