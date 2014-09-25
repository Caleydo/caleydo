/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;

import com.google.common.collect.Iterators;

/**
 * a controller class for a bunch of {@link GLButton}s that act as a single radio group, i.e only one can be selected at
 * one time
 *
 * @author Samuel Gratzl
 *
 */
public class RadioController implements GLButton.ISelectionCallback, Iterable<GLButton> {
	private List<GLButton> buttons = new ArrayList<>();
	private int selected = 0;

	private GLButton.ISelectionCallback callback = GLButton.DUMMY_CALLBACK;

	public RadioController(GLButton.ISelectionCallback callback) {
		this.callback = callback;
	}

	public RadioController() {
	}

	@Override
	public Iterator<GLButton> iterator() {
		return Iterators.unmodifiableIterator(buttons.iterator());
	}

	/**
	 * @param callback
	 *            setter, see {@link callback}
	 */
	public void setCallback(GLButton.ISelectionCallback callback) {
		if (callback == null)
			callback = GLButton.DUMMY_CALLBACK;
		this.callback = callback;
	}

	/**
	 * adds another button to this controller
	 *
	 * @param b
	 */
	public void add(GLButton b) {
		buttons.add(b);
		b.setMode(EButtonMode.BUTTON);
		b.setSelected(selected == (buttons.size() - 1));
		b.setCallback(this);
	}

	/**
	 * sets the selected index
	 *
	 * @param index
	 */
	public void setSelected(int index) {
		if (this.selected == index)
			return;
		int i = 0;
		for (GLButton b : buttons) {
			b.setCallback(null);
			b.setSelected((i++) == index);
			b.setCallback(this);
		}
		this.selected = index;
		callback.onSelectionChanged(index < 0 ? null : buttons.get(index), true);
	}

	/**
	 * @return the selected, see {@link #selected}
	 */
	public int getSelected() {
		return selected;
	}

	/**
	 * 
	 * @return the selected button or nul if nothing is selected
	 */
	public GLButton getSelectedItem() {
		return selected < 0 || selected >= buttons.size() ? null : buttons.get(selected);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		int index = buttons.indexOf(button);
		if (this.selected == index)
			return;
		setSelected(index);
	}
}
