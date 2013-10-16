/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

/**
 * This Event should be triggered when a data domain or table perspective is selected and information about it should be
 * shown.
 *
 * @author Alexander Lex
 */
public class DataSetSelectedEvent extends AEvent {

	private IDataDomain dataDomain = null;
	private TablePerspective tablePerspective = null;

	/** Use if only the {@link IDataDomain} should be set but not table perspective */
	public DataSetSelectedEvent(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * Use if both {@link TablePerspective} and {@link IDataDomain} should be set as the tablePerspective holds the
	 * DataDomain.
	 *
	 * @param tablePerspective
	 */
	public DataSetSelectedEvent(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public boolean checkIntegrity() {
		if (tablePerspective == null && dataDomain == null)
			return false;
		return true;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		if (tablePerspective != null)
			return tablePerspective.getDataDomain();
		return dataDomain;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

}
