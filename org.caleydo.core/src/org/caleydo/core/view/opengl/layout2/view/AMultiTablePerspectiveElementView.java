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
package org.caleydo.core.view.opengl.layout2.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
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

	protected abstract void applyTablePerspectives(AGLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added,
			List<TablePerspective> removed);

	@Override
	protected final AGLElementDecorator createRoot() {
		return new WrapperRoot();
	}

	protected final AGLElementDecorator getRootDecorator() {
		return (AGLElementDecorator) getRoot();
	}

	protected GLElement getContent() {
		AGLElementDecorator rootDecorator = getRootDecorator();
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
		AGLElementDecorator root = getRootDecorator();
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

	@ListenTo
	private void onAddTablePerspective(AddTablePerspectivesEvent event) {
		List<TablePerspective> validTablePerspectives = getDataSupportDefinition().filter(
				event.getTablePerspectives());
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
}
