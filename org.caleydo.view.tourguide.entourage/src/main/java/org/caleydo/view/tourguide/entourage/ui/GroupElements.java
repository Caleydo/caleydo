/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage.ui;

import gleem.linalg.Vec2f;

import java.util.Set;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class GroupElements extends GLElementContainer implements IHasMinSize {

	private ISelectionCallback callback;

	/**
	 *
	 */
	public GroupElements() {
		super(GLLayouts.flowVertical(4));
		setLayoutData(new Vec2f(130, 100));
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(130, (18 + 4) * size());
	}

	public void set(Perspective perspective) {
		this.clear();
		if (perspective == null)
			return;
		for (Group g : perspective.getVirtualArray().getGroupList()) {
			this.add(g);
		}
	}

	public Set<Group> getSelection() {
		ImmutableSet.Builder<Group> b = ImmutableSet.builder();
		for (GLButton button : Iterables.filter(this, GLButton.class))
			if (button.isSelected())
				b.add(button.getLayoutDataAs(Group.class, null));
		return b.build();
	}

	/**
	 * @param g
	 */
	private void add(Group g) {
		GLButton b = new GLButton(EButtonMode.CHECKBOX);
		b.setSelected(true);
		b.setRenderer(GLButton.createCheckRenderer(g.getLabel()));
		b.setLayoutData(g);
		b.setSize(-1, 18);
		b.setCallback(callback);
		this.add(b);
	}

	public void setCallback(GLButton.ISelectionCallback callback) {
		this.callback = callback;
		for (GLButton b : Iterables.filter(this, GLButton.class))
			b.setCallback(callback);
	}
}
