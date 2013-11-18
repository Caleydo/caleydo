/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
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

public class DataDomainQueryUI extends GLElementContainer implements IGLLayout, Comparator<GLElement>, IPickingListener {

	private final ITourGuideDataMode mode;

	private int counter = 0;

	public DataDomainQueryUI(Iterable<ADataDomainQuery> queries, ITourGuideDataMode specifics) {
		super();
		setLayout(this);
		this.mode = specifics;

		for (ADataDomainQuery q : queries) {
			add(createFor(q));
		}

		sortBy(this);

		setLayoutData(new Vec2f(130, guessMultiColumnHeight()));
	}

	@Override
	public void pick(Pick pick) {
		if (pick.getPickingMode() == PickingMode.DOUBLE_CLICKED) {
			System.out.println("double clicked");
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
	/**
	 * @return
	 */
	private float guessMultiColumnHeight() {
		float y = 2;

		int actCat = 0;
		float actMaxY = y;
		for (ADataDomainElement child : Iterables.filter(this, ADataDomainElement.class)) {
			int cat = mode.getCategory(child.getModel().getDataDomain());
			if (actCat != cat) {
				actCat = cat;
				if (y > actMaxY)
					actMaxY = y;
				y = 2;
			}
			y += 20;
		}
		return Math.max(y, actMaxY);
	}

	@Override
	public int compare(GLElement o1, GLElement o2) {
		ADataDomainQuery a1 = o1.getLayoutDataAs(ADataDomainQuery.class, null);
		ADataDomainQuery a2 = o2.getLayoutDataAs(ADataDomainQuery.class, null);

		return mode.getCategory(a1.getDataDomain()) - mode.getCategory(a2.getDataDomain());
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		if (w < 130 * 2) { // linear
			float y = 2;
			w -= 4;

			int actCat = 0;
			for (IGLLayoutElement child : children) {
				int cat = mode.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
				if (actCat != cat) {
					y += 2;
					actCat = cat;
				}
				child.setBounds(2, y, w, 18);
				y += 20;
			}
		} else {
			// in blocks
			float x = 2;
			float y = 2;
			w -= 4;

			int actCat = 0;
			float actMaxY = y;
			for (IGLLayoutElement child : children) {
				int cat = mode.getCategory(child.getLayoutDataAs(ADataDomainQuery.class, null).getDataDomain());
				if (actCat != cat) {
					x += w * (1.f / mode.getNumCategories());
					actCat = cat;
					if (y > actMaxY)
						actMaxY = y;
					y = 2;
				}
				child.setBounds(x, y, w * 1.f / mode.getNumCategories() - 2, 18);
				y += 20;
			}
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
		throw new IllegalStateException();
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
		sortBy(this);
		setLayoutData(new Vec2f(150, guessMultiColumnHeight()));
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
		setLayoutData(new Vec2f(150, guessMultiColumnHeight()));
		relayoutParent();
	}
}
