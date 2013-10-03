/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainElements extends GLElementContainer {

	private ISelectionCallback callback;

	/**
	 *
	 */
	public DataDomainElements() {
		super(GLLayouts.flowVertical(4));
		setLayoutData(new Vec2f(130, 100));
	}

	public void addDataDomain(IDataDomain dataDomain) {
		final DataDomainElement new_ = new DataDomainElement(dataDomain);
		new_.setSize(-1, 18);
		this.add(new_);
		new_.setCallback(callback);
	}

	public void setCallback(GLButton.ISelectionCallback callback) {
		this.callback = callback;
		for (GLButton b : Iterables.filter(this, GLButton.class))
			b.setCallback(callback);
	}
}
