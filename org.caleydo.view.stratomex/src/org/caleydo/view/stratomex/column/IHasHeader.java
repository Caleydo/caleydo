/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.column;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.tourguide.WizardElementLayout;

/**
 * @author Samuel Gratzl
 *
 */
public interface IHasHeader {
	boolean abort();

	float getHeaderBrickBottom();

	float getHeaderBrickTop();

	float getOffset();

	/**
	 * @return
	 */
	float getHeaderOffset();

	public boolean isDetailBrickShown();

}

class BrickColumnHasHeader implements IHasHeader {
	private final BrickColumn brick;
	private final GLBrick header;

	public BrickColumnHasHeader(BrickColumn dimGroup) {
		this.brick = dimGroup;
		this.header = brick.getHeaderBrick();
	}

	@Override
	public boolean abort() {
		return isDetailBrickShown() && !brick.isExpandLeft();
	}

	@Override
	public boolean isDetailBrickShown() {
		return brick.isDetailBrickShown();
	}

	@Override
	public float getHeaderBrickBottom() {
		return header.getLayout().getTranslateY();
	}

	@Override
	public float getHeaderBrickTop() {
		ElementLayout layout = header.getLayout();
		return layout.getTranslateY() + layout.getSizeScaledY();
	}

	@Override
	public float getOffset() {
		return brick.getLayout().getTranslateX() - header.getLayout().getTranslateX();
	}

	@Override
	public float getHeaderOffset() {
		return header.getLayout().getTranslateX();
	}
}

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
