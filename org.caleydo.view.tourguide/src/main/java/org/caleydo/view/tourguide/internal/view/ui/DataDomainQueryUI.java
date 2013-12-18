/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.api.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.api.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.api.model.StratificationDataDomainQuery;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;

import com.google.common.collect.Iterables;

public class DataDomainQueryUI extends GLElementContainer implements IGLLayout,
		IPickingListener, IHasMinSize {

	private final ITourGuideDataMode mode;

	private int counter = 0;

	public DataDomainQueryUI(Iterable<ADataDomainQuery> queries, ITourGuideDataMode specifics) {
		super();
		setLayout(this);
		this.mode = specifics;

		for (ADataDomainQuery q : queries) {
			add(createFor(q));
		}
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(130, size() * (20) + 4);
	}

	@Override
	public void pick(Pick pick) {
		if (pick.getPickingMode() == PickingMode.DOUBLE_CLICKED) {
			int id = pick.getObjectID();
			// deactivate all others
			for (ADataDomainElement child : Iterables.filter(this, ADataDomainElement.class)) {
				if (id != child.getPickingObjectId() && child.getModel().isActive())
					child.setSelected(false);
				else if (id == child.getPickingObjectId()) // activate the double clicked never the less was the
															// previous state was
					child.setSelected(true);
			}
		}

	}


	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		float y = 2;
		w -= 4;

		for (IGLLayoutElement child : children) {
			child.setBounds(2, y, w, 18);
			y += 20;
		}
	}

	private ADataDomainElement createFor(ADataDomainQuery q) {
		ADataDomainElement elem = wrap(q);
		elem.onPick(this);
		elem.setPickingObjectId(++counter);
		return elem;
	}

	private ADataDomainElement wrap(ADataDomainQuery q) {
		if (q instanceof CategoricalDataDomainQuery)
			return new CategoricalDataDomainElement((CategoricalDataDomainQuery) q);
		if (q instanceof PathwayDataDomainQuery)
			return new PathwayDataDomainElement((PathwayDataDomainQuery) q);
		if (q instanceof StratificationDataDomainQuery)
			return new TableDataDomainElement((StratificationDataDomainQuery) q);
		if (q instanceof InhomogenousDataDomainQuery)
			return new InhomogenousDataDomainElement((InhomogenousDataDomainQuery) q);
		return new GenericDataDomainElement(q);
	}

	public void updateSelections() {
		for (ADataDomainElement elem : Iterables.filter(this, ADataDomainElement.class)) {
			elem.updateSelection();
		}
	}

	/**
	 * @param query
	 */
	public void add(ADataDomainQuery query) {
		this.add(createFor(query));
		relayoutParent();
	}

	/**
	 * @param query
	 */
	public void remove(ADataDomainQuery query) {
		for (ADataDomainElement elem : Iterables.filter(this, ADataDomainElement.class)) {
			if (elem.getModel() == query) {
				remove(elem);
				break;
			}
		}
		relayoutParent();
	}
}
