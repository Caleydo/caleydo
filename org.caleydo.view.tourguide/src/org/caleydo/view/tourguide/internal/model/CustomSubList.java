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
package org.caleydo.view.tourguide.internal.model;

import java.util.AbstractList;
import java.util.List;

/**
 * sub list variant, not checking any concurrent modifications
 * 
 * @author Samuel Gratzl
 * 
 */
public class CustomSubList<T> extends AbstractList<T> {
	private final int offset;
	private final List<T> backend;
	private final int size;

	public CustomSubList(List<T> backend, int offset, int size) {
		super();
		this.backend = backend;
		this.offset = offset;
		this.size = size;
	}

	@Override
	public T get(int index) {
		return backend.get(offset + index);
	}

	@Override
	public int size() {
		return size;
	}

}
