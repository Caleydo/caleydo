package org.caleydo.view.tourguide.stratomex.wizard;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.IHasHeader;

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

}