package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Renderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.Template;

/**
 * Layout Template for a Brick
 * 
 * @author Alexander Lex
 * 
 */
public class BrickLayoutTemplate extends Template {

	private GLBrick brick;
	private Renderer viewRenderer;

	public BrickLayoutTemplate(GLBrick brick) {
		this.brick = brick;
	}

	@Override
	public void setParameters() {
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
		fuelBarLayout.setPixelSizeX(20);
		// fuelBarLayout.setRenderer(new FuelBarRenderer());

		baseRow.appendElement(baseColumn);
		baseRow.appendElement(fuelBarLayout);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(20);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.setRenderer(viewRenderer);

		ElementLayout viewToolBarLayout = new ElementLayout("viewToolBarLayout");
		viewToolBarLayout.setFrameColor(0.5f, 0.5f, 0, 1);
		viewToolBarLayout.setPixelGLConverter(pixelGLConverter);
		viewToolBarLayout.setPixelSizeY(20);
		viewToolBarLayout.setRenderer(new ViewToolBarRenderer(brick));

		baseColumn.appendElement(dimensionBarLayout);
		baseColumn.appendElement(viewLayout);
		baseColumn.appendElement(viewToolBarLayout);

	}

	void setViewRenderer(Renderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

}
