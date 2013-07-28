/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.view;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;

import com.google.common.base.Objects;

/**
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ASingleTablePerspectiveElementView extends AGLElementView implements
		ISingleTablePerspectiveBasedView {
	protected TablePerspective tablePerspective;

	public ASingleTablePerspectiveElementView(IGLCanvas glCanvas, String viewType, String viewName) {
		super(glCanvas, viewType, viewName);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);

		applyTablePerspective(getRootDecorator(), tablePerspective);
	}

	protected abstract void applyTablePerspective(GLElementDecorator root, TablePerspective tablePerspective);

	@Override
	protected final GLElementDecorator createRoot() {
		return new GLElementDecorator();
	}

	protected final GLElementDecorator getRootDecorator() {
		return (GLElementDecorator) getRoot();
	}

	protected GLElement getContent() {
		GLElementDecorator rootDecorator = getRootDecorator();
		if (rootDecorator == null)
			return null;
		return rootDecorator.getContent();
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	public final void setDataDomain(ATableBasedDataDomain dataDomain) {
		// unused
	}

	@Override
	public final ATableBasedDataDomain getDataDomain() {
		if (tablePerspective != null)
			return tablePerspective.getDataDomain();
		return null;
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		if (tablePerspective != null)
			return Collections.singleton((IDataDomain) tablePerspective.getDataDomain());
		return Collections.emptySet();
	}

	@Override
	public final void setTablePerspective(TablePerspective tablePerspective) {
		if (Objects.equal(this.tablePerspective, tablePerspective))
			return;
		this.tablePerspective = tablePerspective;
		fireTablePerspectiveChanged();
		GLElementDecorator root = getRootDecorator();
		if (root != null) {
			applyTablePerspective(root, tablePerspective);
		}
	}

	@Override
	public final TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public final List<TablePerspective> getTablePerspectives() {
		return Collections.singletonList(getTablePerspective());
	}

	private void fireTablePerspectiveChanged() {
		EventPublisher.trigger(new TablePerspectivesChangedEvent(this).from(this));
	}

	@ListenTo
	private void onAddTablePerspective(AddTablePerspectivesEvent event) {
		Collection<TablePerspective> validTablePerspectives = getDataSupportDefinition().filter(
				event.getTablePerspectives());
		if (validTablePerspectives.isEmpty() || validTablePerspectives.size() > 1) {
			// Make clear for (e.g. for DVI) that no perspectives have been added.
			fireTablePerspectiveChanged();
		} else {
			setTablePerspective(validTablePerspectives.iterator().next());
		}
	}

	@ListenTo(sendToMe = true)
	private void onRemoveTablePerspective(RemoveTablePerspectiveEvent event) {
		if (Objects.equal(tablePerspective, event.getTablePerspective()))
			setTablePerspective(null);
	}
}
