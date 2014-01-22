/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.ILabeled;

/**
 * @author Samuel Gratzl
 *
 */
public class CategoryItem implements ILabeled, Comparable<CategoryItem> {
	private final IDCategory category;
	private final SelectionManager manager;
	private final IIDTypeMapper<Integer, String> prim2human;

	private final NavigableSet<SelectionTypeItem> children = new TreeSet<>();

	public CategoryItem(IDCategory category, IEventBasedSelectionManagerUser listener) {
		super();
		this.category = category;
		prim2human = IDMappingManagerRegistry.get().getIDMappingManager(category)
				.getIDTypeMapper(category.getPrimaryMappingType(), category.getHumanReadableIDType());
		this.manager = new EventBasedSelectionManager(listener, category.getPrimaryMappingType());
		update();
	}

	/**
	 * @return the manager, see {@link #manager}
	 */
	public SelectionManager getManager() {
		return manager;
	}

	/**
	 * @return the children, see {@link #children}
	 */
	public NavigableSet<SelectionTypeItem> getChildren() {
		return children;
	}

	/**
	 * @return the category, see {@link #category}
	 */
	public IDCategory getCategory() {
		return category;
	}

	@Override
	public String getLabel() {
		return category.getDenomination(false);
	}

	public boolean update() {
		boolean dirty = false;
		List<SelectionType> selectionTypes = new ArrayList<>(manager.getSelectionTypes());
		for (Iterator<SelectionType> it = selectionTypes.iterator(); it.hasNext(); ) {
			SelectionType selectionType = it.next();
			int count = manager.getNumberOfElements(selectionType);
			if (count == 0)
				it.remove();
			SelectionTypeItem item = new SelectionTypeItem(this, selectionType);
			if (!children.contains(item)) {
				children.add(item);
				dirty = true;
			}
		}
		return dirty;

	}

	/**
	 * @param id
	 * @return
	 */
	public String toLabel(Integer id) {
		Set<String> label = prim2human == null ? null : prim2human.apply(id);
		if (label == null || label.isEmpty())
			return "~" + id + "~";
		return label.iterator().next();
	}

	@Override
	public int compareTo(CategoryItem o) {
		return getLabel().compareToIgnoreCase(o.getLabel());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
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
		CategoryItem other = (CategoryItem) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		return true;
	}

	/**
	 * @return
	 */
	public int size() {
		return IDMappingManagerRegistry.get().getIDMappingManager(category).getPrimaryTypeCounter();
	}

	/**
	 * @return
	 */
	public int getTotal() {
		return manager.getNumberOfElements();
	}

}
