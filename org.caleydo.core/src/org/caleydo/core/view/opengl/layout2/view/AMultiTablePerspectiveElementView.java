/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.ReplaceTablePerspectiveEvent;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;

/**
 * a {@link AGLElementView} for handling multiple {@link TablePerspective} using {@link IMultiTablePerspectiveBasedView}
 * 
 * @author Samuel Gratzl
 * 
 */
public abstract class AMultiTablePerspectiveElementView extends AGLElementView implements
		IMultiTablePerspectiveBasedView {
	private static final List<TablePerspective> NONE = Collections.emptyList();

	protected final List<TablePerspective> tablePerspectives = new ArrayList<>();

	public AMultiTablePerspectiveElementView(IGLCanvas glCanvas, String viewType, String viewName) {
		super(glCanvas, viewType, viewName);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);

		applyTablePerspectives(getRootDecorator(), tablePerspectives, tablePerspectives, NONE);
	}

	protected abstract void applyTablePerspectives(GLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added,
			List<TablePerspective> removed);

	@Override
	protected final GLElementDecorator createRoot() {
		GLElementDecorator g = new GLElementDecorator();
		g.setContent(createContent());
		return g;
	}

	protected GLElement createContent() {
		return null;
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
	public final Set<IDataDomain> getDataDomains() {
		Set<IDataDomain> dd = new HashSet<>();
		for (TablePerspective p : tablePerspectives)
			dd.add(p.getDataDomain());
		return dd;
	}

	@Override
	public final void addTablePerspective(TablePerspective newTablePerspective) {
		addTablePerspectives(Collections.singletonList(newTablePerspective));
	}

	@Override
	public final void addTablePerspectives(List<TablePerspective> newTablePerspectives) {
		// remove already part of
		newTablePerspectives.removeAll(this.tablePerspectives);
		if (newTablePerspectives.isEmpty())
			return;
		this.tablePerspectives.addAll(newTablePerspectives);
		updateTablePerspectives(newTablePerspectives, NONE);
	}

	@Override
	public final void removeTablePerspective(TablePerspective tablePerspective) {
		boolean changed = false;
		for (Iterator<TablePerspective> it = tablePerspectives.iterator(); it.hasNext();) {
			if (it.next().equals(tablePerspective)) {
				it.remove();
				changed = true;
			}
		}

		if (changed)
			updateTablePerspectives(NONE, Collections.singletonList(tablePerspective));
	}

	private void updateTablePerspectives(List<TablePerspective> added, List<TablePerspective> removed) {
		fireTablePerspectiveChanged();
		GLElementDecorator root = getRootDecorator();
		if (root != null) {
			applyTablePerspectives(root, tablePerspectives, added, removed);
		}
	}

	@Override
	public final List<TablePerspective> getTablePerspectives() {
		return Collections.unmodifiableList(tablePerspectives);
	}

	private void fireTablePerspectiveChanged() {
		EventPublisher.trigger(new TablePerspectivesChangedEvent(this).from(this));
	}

	@ListenTo(sendToMe = true)
	private void onAddTablePerspective(AddTablePerspectivesEvent event) {
		List<TablePerspective> validTablePerspectives = getDataSupportDefinition().filter(
				event.getTablePerspectives());
		validTablePerspectives.removeAll(tablePerspectives);
		if (validTablePerspectives.isEmpty()) {
			// Make clear for (e.g. for DVI) that no perspectives have been added.
			fireTablePerspectiveChanged();
		} else {
			addTablePerspectives(validTablePerspectives);
		}
	}

	@ListenTo(sendToMe = true)
	private void onRemoveTablePerspective(RemoveTablePerspectiveEvent event) {
		removeTablePerspective(event.getTablePerspective());
	}

	@ListenTo(sendToMe = true)
	private void onReplaceTablePerspective(ReplaceTablePerspectiveEvent event) {
		TablePerspective oldPerspective = event.getOldPerspective();
		TablePerspective newPerspective = event.getNewPerspective();
		if (!getDataSupportDefinition().apply(newPerspective) || tablePerspectives.contains(newPerspective))
			newPerspective = null;

		boolean changed = false;
		for (ListIterator<TablePerspective> it = tablePerspectives.listIterator(); it.hasNext();) {
			if (it.next().equals(oldPerspective)) {
				if (newPerspective != null)
					it.set(newPerspective);
				else
					it.remove();
				changed = true;
			}
		}

		if (changed)
			updateTablePerspectives(newPerspective == null ? NONE : Collections.singletonList(newPerspective),
					Collections.singletonList(oldPerspective));
	}
}
