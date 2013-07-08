/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
