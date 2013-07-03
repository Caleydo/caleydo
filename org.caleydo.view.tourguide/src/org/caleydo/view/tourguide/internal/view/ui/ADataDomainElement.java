/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.ICON_FILTER;
import static org.caleydo.view.tourguide.internal.TourGuideRenderStyle.ICON_FILTER_DISABLED;

import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainActions;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.external.ExternalScoringDataDomainActionFactory;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;

public abstract class ADataDomainElement extends GLButton implements GLButton.ISelectionCallback {
	protected final ADataDomainQuery model;
	private boolean hasFilter = false;

	private int filterPickingId = -1;

	public ADataDomainElement(ADataDomainQuery model) {
		this.model = model;
		setHasFilter(model.hasFilter());
		setLayoutData(model);
		setMode(EButtonMode.CHECKBOX);
		setSize(-1, 18);
		setSelected(model.isActive());
		setCallback(this);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		filterPickingId = context.registerPickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					onFilterEdit(true, null);
			}
		});
	}

	@Override
	protected void takeDown() {
		context.unregisterPickingListener(filterPickingId);
		super.takeDown();
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		model.setActive(selected);
	}

	public void updateSelection() {
		if (this.isSelected() && !model.isActive())
			this.setSelected(false);
	}
	/**
	 * @return the model, see {@link #model}
	 */
	public ADataDomainQuery getModel() {
		return model;
	}

	/**
	 * @param hasFilter
	 *            setter, see {@link hasFilter}
	 */
	public final void setHasFilter(boolean hasFilter) {
		if (this.hasFilter == hasFilter)
			return;
		this.hasFilter = hasFilter;
		repaint();
	}

	/**
	 * @return the hasFilter, see {@link #hasFilter}
	 */
	public final boolean isHasFilter() {
		return hasFilter;
	}

	@Override
	protected final void onRightClicked(Pick pick) {
		if (pick.isAnyDragging())
			return;

		ContextMenuCreator creator = new ContextMenuCreator();
		createContextMenu(creator);

		Collection<Pair<String, ? extends AEvent>> collection = ExternalScoringDataDomainActionFactory.create(
				model.getDataDomain(), context);
		if (!collection.isEmpty()) {
			creator.addAll(collection);
			creator.addSeparator();
		}
		DataDomainActions.add(creator, model.getDataDomain(), this, true);

		context.getSWTLayer().showContextMenu(creator);
	}

	/**
	 * @param creator
	 */
	protected void createContextMenu(ContextMenuCreator creator) {
		creator.addContextMenuItem(new GenericContextMenuItem("Edit filter", new EditDataDomainFilterEvent().to(this)));
		creator.addSeparator();
	}

	protected abstract void onFilterEdit(boolean isStartEditing, Object payload);

	@ListenTo(sendToMe = true)
	private void onEditDataDomainFilter(final EditDataDomainFilterEvent e) {
		onFilterEdit(e.isStartEditing(), e.getPayload());
	}


	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (isSelected())
			g.color(model.getDataDomain().getColor()).fillRect(2, 2, 14, 14);
		g.fillImage(getStandardIcon("checkbox", false), 0, 0, 18, 18);

		float tw = Math.min(g.text.getTextWidth(getLabel(), 14), w - 18 - 18);
		g.drawText(getLabel(), 18, 1, w - 18 - 18, 14);
		if (model.isFilteringPossible())
			g.fillImage(hasFilter ? ICON_FILTER : ICON_FILTER_DISABLED, 18 + tw + 2, 2, 12, 12);
	}

	protected String getLabel() {
		return model.getDataDomain().getLabel();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);

		g.incZ();
		float tw = Math.min(g.text.getTextWidth(getLabel(), 14), w - 18 - 18);
		g.pushName(filterPickingId);
		g.fillRect(18 + tw + 2, 2, 12, 12);
		g.popName();
		g.decZ();
	}
}
