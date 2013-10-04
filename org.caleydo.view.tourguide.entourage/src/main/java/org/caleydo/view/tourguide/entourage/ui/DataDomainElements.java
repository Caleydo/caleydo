/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.EButtonIcon;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainElements extends GLElementContainer implements IHasMinSize {

	private ISelectionCallback selectionCallback;
	private RadioController onNodeController = new RadioController();

	private GLButton onNodeNode = createSelectAllNone();
	/**
	 *
	 */
	public DataDomainElements() {
		super(GLLayouts.flowVertical(4));
		setLayoutData(new Vec2f(130, 100));
	}

	public void addDataDomain(IDataDomain dataDomain) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(2));
		c.setSize(-1, 18);
		final DataDomainElement new_ = new DataDomainElement(dataDomain);
		new_.setCallback(selectionCallback);
		final DataDomainRadioElement new_2 = new DataDomainRadioElement(dataDomain);
		new_2.setSize(16, -1);
		onNodeController.add(new_2);
		c.add(new_2);
		c.add(new_);
		this.add(c);
	}

	/**
	 * @return the onNodeNode, see {@link #onNodeNode}
	 */
	public GLButton getOnNoneNode() {
		return onNodeNode;
	}

	private GLButton createSelectAllNone() {
		GLButton b = new GLButton(EButtonMode.CHECKBOX);
		onNodeController.add(b);
		b.setRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				final boolean s = ((GLButton)parent).isSelected();
				g.fillImage(EButtonIcon.RADIO.get(s), 0, 0, 18, 18);

				g.drawText("None", 26+20, 1, w - 26-20, 14);
			}
		});
		b.setSize(-1, 18);
		return b;
	}

	public Iterable<GLButton> getSelectionButtons() {
		List<GLButton> b = new ArrayList<>(size());
		for (GLElementContainer c : Iterables.filter(this, GLElementContainer.class)) {
			b.add((GLButton) c.get(1));
		}
		return b;
	}

	public GLButton getActiveOnNodeButton() {
		return onNodeController.getSelectedItem();
	}

	public void setActiveOnNodeDataDomain(IDataDomain dataDomain) {
		if (dataDomain == null)
			onNodeController.setSelected(0);
		else {
			int i = 0;
			for (GLButton b : onNodeController) {
				IDataDomain d = b.getLayoutDataAs(IDataDomain.class, null);
				if (Objects.equals(d, dataDomain)) {
					onNodeController.setSelected(i);
				}
				i++;
			}
		}

	}


	public void setCallback(GLButton.ISelectionCallback callback) {
		this.selectionCallback = callback;
		for (GLButton b : getSelectionButtons())
			b.setCallback(callback);
	}

	public void setOnNodeCallback(GLButton.ISelectionCallback callback) {
		onNodeController.setCallback(callback);
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(130, (18 + 4) * size());
	}
}
