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
package org.caleydo.core.view.swt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.view.TablePerspectivesChangedEvent;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ASingleTablePerspectiveSWTView extends AView implements ISingleTablePerspectiveBasedView,
		ITablePerspectiveMixinCallback {
	protected final EventListenerManager eventListeners = EventListenerManagers.createSWTDirect();

	protected TablePerspectiveSelectionMixin selection;

	protected TablePerspective tablePerspective;

	public ASingleTablePerspectiveSWTView(Composite parentComposite, String viewType, String viewName) {
		super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.GL_VIEW), parentComposite, viewType,
				viewName);
	}

	@Override
	public void initialize() {
		eventListeners.register(this);
		GeneralManager.get().getViewManager().registerView(this, true);
	}

	public void dispose() {
		eventListeners.unregisterAll();
		ViewManager.get().destroyView(this);
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {

	}

	protected void onSetTablePerspective() {
		if (selection != null) { // cleanup old
			eventListeners.unregister(selection);
			this.selection = null;
		}

		if (tablePerspective != null) {
			selection = new TablePerspectiveSelectionMixin(tablePerspective, this);
			eventListeners.register(selection);
		}
	}

	@Override
	public boolean isDataView() {
		return true;
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
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
		this.tablePerspective = tablePerspective;
		fireTablePerspectiveChanged();

		onSetTablePerspective();
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
		if (tablePerspective == event.getTablePerspective())
			setTablePerspective(null);
	}
}
