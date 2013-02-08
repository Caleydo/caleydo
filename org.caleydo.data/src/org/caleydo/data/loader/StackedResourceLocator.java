/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
	private Deque<IResourceLocator> stack = new ArrayDeque<>();

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
}

