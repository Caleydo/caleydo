/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.loader;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.caleydo.data.loader.ResourceLocators.IResourceLocator;

/**
 * a special composite of a resource locator using a manipulatable stack of underlying resource locators
 *
 * @author Samuel Gratzl
 *
 */
public class StackedResourceLocator implements IResourceLocator {
	private Deque<IResourceLocator> stack = new ArrayDeque<>(3);

	public void push(IResourceLocator elem) {
		stack.push(elem);
	}

	public void pop() {
		stack.pop();
	}

	@Override
	public InputStream get(String res) {
		for (Iterator<IResourceLocator> it = stack.descendingIterator(); it.hasNext();) {
			IResourceLocator elem = it.next();
			InputStream result = elem.get(res);
			if (result != null)
				return result;
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("STACK:\n");
		for (Iterator<IResourceLocator> it = stack.descendingIterator(); it.hasNext();) {
			IResourceLocator elem = it.next();
			b.append('\t').append(elem).append("\n");
		}
		b.setLength(b.length() - 1);
		return b.toString();
	}
}

