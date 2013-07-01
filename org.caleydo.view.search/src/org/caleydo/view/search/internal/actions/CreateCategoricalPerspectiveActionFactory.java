/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.internal.actions;

import java.util.Collection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.view.search.api.IResultRow;
import org.caleydo.view.search.api.ISearchResultActionFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
public class CreateCategoricalPerspectiveActionFactory implements ISearchResultActionFactory {

	@Override
	public boolean createPerspectiveActions(MenuManager mgr, IResultRow row, Collection<Perspective> perspectives) {
		boolean any = false;
		for (Perspective p : perspectives) {
			IDType type = p.getIdType();
			if (!row.has(type))
				continue;
			if (!isCategoricalIdType(p.getDataDomain(), type) || !Integer.class.isInstance(row.get(type)))
				continue;

			mgr.add(new CreateCategoricalAction((ATableBasedDataDomain) p.getDataDomain(), (Integer) row.get(type)));
		}
		return any;
	}

	private boolean isCategoricalIdType(IDataDomain dataDomain, IDType type) {
		if (!DataDomainOracle.isCategoricalDataDomain(dataDomain))
			return false;
		ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;
		if (!dd.getTable().isDataHomogeneous())
			return false;
		boolean isDim = dd.getDimensionIDType() == type;
		return isDim; // TODO assumption only the columns are categorical
	}

	@Override
	public boolean createIDTypeActions(MenuManager mgr, IResultRow row) {
		return false;
	}

	private static class CreateCategoricalAction extends Action {
		private final ATableBasedDataDomain dataDomain;
		private final int rowId;

		public CreateCategoricalAction(ATableBasedDataDomain dataDomain, int rowId) {
			setText("Create Categorization within " + dataDomain.getLabel());
			setDescription("Creates a new Tableperspective just with this selected element as a single column");
			this.dataDomain = dataDomain;
			this.rowId = rowId;
		}

		@Override
		public void run() {
			DataDomainOracle.createRowCategoricalPerspective(dataDomain, rowId, false);

			DataDomainUpdateEvent event = new DataDomainUpdateEvent(dataDomain);
			event.setSender(this);
			EventPublisher.trigger(event);

			// Switch to DVI view
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.caleydo.view.dvi");
			} catch (PartInitException e1) {
				e1.printStackTrace();
			}
		}
	}
}
