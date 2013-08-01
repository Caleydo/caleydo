/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;

public class CreateViewFromTablePerspectiveEvent extends AEvent {

	private String viewType;
	private IDataDomain dataDomain;
	private TablePerspective tablePerspective;
	private TablePerspectiveCreator creator;

	public CreateViewFromTablePerspectiveEvent(String viewType, IDataDomain dataDomain,
			TablePerspective tablePerspective) {
		this.setViewType(viewType);
		this.setDataDomain(dataDomain);
		this.setTablePerspective(tablePerspective);
	}

	public CreateViewFromTablePerspectiveEvent(String viewType, IDataDomain dataDomain, TablePerspectiveCreator creator) {
		this.setViewType(viewType);
		this.setDataDomain(dataDomain);
		this.setCreator(creator);
	}

	@Override
	public boolean checkIntegrity() {
		// TODO real check
		return true;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getViewType() {
		return viewType;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @param creator
	 *            setter, see {@link creator}
	 */
	public void setCreator(TablePerspectiveCreator creator) {
		this.creator = creator;
	}

	/**
	 * @return the creator, see {@link #creator}
	 */
	public TablePerspectiveCreator getCreator() {
		return creator;
	}

}
