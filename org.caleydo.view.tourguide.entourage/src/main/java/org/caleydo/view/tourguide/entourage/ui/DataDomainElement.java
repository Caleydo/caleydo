/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.EButtonIcon;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;

/**
 * @author Samuel Gratzl
 *
 */
class DataDomainElement extends GLButton {
	private final IDataDomain dataDomain;

	public DataDomainElement(IDataDomain dataDomain) {
		super(EButtonMode.CHECKBOX);
		this.dataDomain = dataDomain;
		setLayoutData(dataDomain);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (isSelected())
			g.color(dataDomain.getColor()).fillRect(2, 2, 14, 14);
		g.fillImage(EButtonIcon.CHECKBOX.get(false), 0, 0, 18, 18);

		g.drawText(this.dataDomain.getLabel(), 18, 1, w - 18, 14);
	}

}
