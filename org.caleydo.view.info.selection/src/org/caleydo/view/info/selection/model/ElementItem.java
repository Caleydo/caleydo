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
package org.caleydo.view.info.selection.model;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.eclipse.swt.graphics.Color;

/**
 * @author Samuel Gratzl
 *
 */
public class ElementItem implements ILabeled, Comparable<ElementItem> {
	private final SelectionTypeItem parent;
	private final Integer id;
	private final String label;

	public ElementItem(SelectionTypeItem parent, Integer id, String label) {
		super();
		this.parent = parent;
		this.id = id;
		this.label = label;
	}

	public Color getColor() {
		return parent.getColor();
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public SelectionTypeItem getParent() {
		return parent;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public int compareTo(ElementItem o) {
		return label.compareTo(o.label);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementItem other = (ElementItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return
	 */
	public IDType getIDType() {
		return parent.getParent().getManager().getIDType();
	}

}
