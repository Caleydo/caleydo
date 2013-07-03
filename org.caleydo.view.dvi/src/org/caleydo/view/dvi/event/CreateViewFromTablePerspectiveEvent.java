/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

public class CreateViewFromTablePerspectiveEvent extends AEvent {

	private String viewType;
	private IDataDomain dataDomain;
	private TablePerspective tablePerspective;

	public CreateViewFromTablePerspectiveEvent(String viewType, IDataDomain dataDomain,
			TablePerspective tablePerspective) {
		this.setViewType(viewType);
		this.setDataDomain(dataDomain);
		this.setTablePerspective(tablePerspective);
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

}
