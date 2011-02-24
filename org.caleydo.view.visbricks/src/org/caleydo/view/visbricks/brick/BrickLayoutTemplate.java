package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.layout.Template;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;

public class BrickLayoutTemplate extends Template {

	@Override
	public void setParameters() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 1);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("baseColumn");
//		setBaseElementLayout(baseColumn);
//		baseColumn.grabX();
		baseColumn.setFrameColor(0, 1, 0, 1);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 1);
		

		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeX(60);
		// fuelBarLayout.setRenderer(new FuelBarRenderer());

		baseRow.appendElement(baseColumn);
		baseRow.appendElement(fuelBarLayout);


		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 1);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(60);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(0, 1, 1, 1);

		ElementLayout viewToolBarLayout = new ElementLayout("viewToolBarLayout");
		viewToolBarLayout.setFrameColor(0.5f, 0.5f, 0, 1);
		viewToolBarLayout.setPixelGLConverter(pixelGLConverter);
		viewToolBarLayout.setPixelSizeY(60);

		baseColumn.appendElement(dimensionBarLayout);
		baseColumn.appendElement(viewLayout);
		baseColumn.appendElement(viewToolBarLayout);

	}

}
