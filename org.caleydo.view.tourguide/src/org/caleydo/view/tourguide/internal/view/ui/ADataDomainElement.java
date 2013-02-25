package org.caleydo.view.tourguide.internal.view.ui;

import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainActions;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.tourguide.internal.external.ExternalScoringDataDomainActionFactory;
import org.caleydo.view.tourguide.internal.view.model.ADataDomainQuery;

public abstract class ADataDomainElement extends GLButton implements GLButton.ISelectionCallback {
	protected final ADataDomainQuery model;
	private boolean hasFilter = false;

	public ADataDomainElement(ADataDomainQuery model) {
		this.model = model;
		setLayoutData(model);
		setMode(EButtonMode.CHECKBOX);
		setSize(-1, 18);
		setCallback(this);
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

		context.showContextMenu(creator);
	}

	/**
	 * @param creator
	 */
	protected abstract void createContextMenu(ContextMenuCreator creator);

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		if (isSelected()) {
			g.fillImage("resources/icons/view/tourguide/accept.png", 3, 2, 14, 14);
		} else {
			g.fillImage("resources/icons/view/tourguide/accept_disabled.png", 3, 2, 14, 14);
		}

		g.color(model.getDataDomain().getColor()).fillRect(18, 2, 14, 14);
		if (hasFilter) {
			g.fillImage("resources/icons/view/tourguide/filter.png", 18, 0, 8, 8);
		}
		g.drawText(model.getDataDomain(), 18 + 18, 1, w - 18, 14);
	}
}