package org.caleydo.view.stratomex.tourguide;

import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.IHasHeader;
import org.caleydo.view.tourguide.stratomex.wizard.WizardElementLayout;

class WizardHasHaeder implements IHasHeader {
	private final WizardElementLayout wizard;
	private final ElementLayout body;

	public WizardHasHaeder(WizardElementLayout wizard) {
		this.wizard = wizard;
		this.body = wizard.getBody();
	}

	@Override
	public boolean abort() {
		return false;
	}

	@Override
	public boolean isDetailBrickShown() {
		return false;
	}

	@Override
	public float getHeaderBrickBottom() {
		return body.getTranslateY()
				+ body.getSizeScaledY()
				- body.getLayoutManager().getPixelGLConverter()
						.getGLHeightForPixelHeight(GLStratomex.ARCH_PIXEL_HEIGHT);
	}

	@Override
	public float getHeaderBrickTop() {
		return body.getTranslateY() + body.getSizeScaledY();
	}

	@Override
	public float getOffset() {
		return wizard.getTranslateX() - body.getTranslateX();
	}

	@Override
	public float getHeaderOffset() {
		return body.getTranslateX();
	}

	renderEmptyViewText(gl, new String[] { "To add a column showing a dataset",
			" click the \"+\" button at the top", "or use the LineUp or Data-View Integrator view", "",
			"Refer to http://help.caleydo.org for more information." });

}