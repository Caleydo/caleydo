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
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

public class CreateViewFromDataContainerEvent extends AEvent {

	private String viewType;
	private IDataDomain dataDomain;
	private TablePerspective tablePerspective;

	public CreateViewFromDataContainerEvent(String viewType, IDataDomain dataDomain,
			TablePerspective tablePerspective) {
		this.setViewType(viewType);
		this.setDataDomain(dataDomain);
		this.setDataContainer(tablePerspective);
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

	public void setDataContainer(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	public TablePerspective getDataContainer() {
		return tablePerspective;
	}

}
