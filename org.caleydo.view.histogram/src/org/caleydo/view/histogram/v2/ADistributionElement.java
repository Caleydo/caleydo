/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2;

import gleem.linalg.Vec2f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation.ILocator;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.view.histogram.v2.internal.IDistributionData;
import org.caleydo.view.histogram.v2.internal.IDistributionData.DistributionEntry;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * Rendering the distribution of a categorical element in various forms
 *
 * @author Samuel Gratzl
 */
public abstract class ADistributionElement extends PickableGLElement implements IHasMinSize, IPickingLabelProvider,
		ILocator {

	protected static final List<SelectionType> SELECTIONTYPES = Arrays.asList(SelectionType.MOUSE_OVER,
			SelectionType.SELECTION);

	protected PickingPool bucketPickingIds;
	protected int hovered = -1;

	@DeepScan
	protected final IDistributionData data;

	public ADistributionElement(IDistributionData data) {
		this.data = data;
		data.onChange(this);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		IPickingListener pick = PickingListenerComposite.concat(context.getSWTLayer().createTooltip(this), new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onBucketPick(pick);
			}
		});
		bucketPickingIds = new PickingPool(context, pick);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isAssignableFrom(Vec2f.class))
			return clazz.cast(getMinSize());
		return super.getLayoutDataAs(clazz, default_);
	}

	@Override
	protected void takeDown() {
		bucketPickingIds.clear();
		bucketPickingIds = null;
		super.takeDown();
	}

	@Override
	public String getLabel(Pick pick) {
		int bucket = pick.getObjectID();
		StringBuilder b = new StringBuilder();
		final DistributionEntry entry = data.get(bucket);
		b.append(String.format("%s: %f (%.2f%%)", entry.getLabel(), entry.getValue() * data.size(),
				entry.getValue() * 100));
		for (SelectionType selectionType : SELECTIONTYPES) {
			Set<Integer> elements = data.getElements(selectionType);
			if (elements.isEmpty())
				continue;
			Set<Integer> ids = entry.getIDs();
			int scount = Sets.intersection(elements, ids).size();
			if (scount > 0)
				b.append(String.format("\n  %s: %d (%.2f%%)", selectionType.getType(), scount,
						scount * 100f / ids.size()));
		}
		return b.toString();
	}

	/**
	 * @param pick
	 */
	protected void onBucketPick(Pick pick) {
		int bucket = pick.getObjectID();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = bucket;
			data.select(data.get(pick.getObjectID()).getIDs(), SelectionType.MOUSE_OVER, true);
			repaint();
			break;
		case MOUSE_OUT:
			hovered = -1;
			data.select(Collections.<Integer> emptyList(), SelectionType.MOUSE_OVER, true);
			repaint();
			break;
		case CLICKED:
			// select bucket:
			data.select(data.get(pick.getObjectID()).getIDs(), SelectionType.SELECTION, true);
			break;
		default:
			break;
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		render(g, w, h);
		g.lineWidth(1);
	}

	protected abstract void render(GLGraphics g, float w, float h);

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);

		if (getVisibility() != EVisibility.PICKABLE)
			return;

		g.incZ();
		render(g, w, h);
		g.decZ();
	}

	protected static Color toHighlightColor(SelectionType selectionType) {
		Color c = selectionType.getColor().clone();
		c.a = 0.75f;
		return c;
	}


	protected Color toHighlight(Color color, int bucket) {
		if (bucket == hovered)
			return color.darker();
		return color;
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
	}

	@Override
	public GLLocation apply(Integer input) {
		return GLLocation.applyPrimitive(this, input);
	}
}
