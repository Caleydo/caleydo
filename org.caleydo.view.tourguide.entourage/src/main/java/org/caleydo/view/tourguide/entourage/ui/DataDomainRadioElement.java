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
class DataDomainRadioElement extends GLButton {

	public DataDomainRadioElement(IDataDomain dataDomain) {
		super(EButtonMode.CHECKBOX);
		setLayoutData(dataDomain);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		g.fillImage(EButtonIcon.RADIO.get(isSelected()), 0, 0, 18, 18);
	}

}
