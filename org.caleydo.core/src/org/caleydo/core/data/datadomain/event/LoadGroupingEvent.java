/**
 *
 */
package org.caleydo.core.data.datadomain.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.gui.dataimport.ImportGroupingDialog;

/**
 * Event for triggering the {@link ImportGroupingDialog}.
 *
 * @author Christian Partl
 *
 */
public class LoadGroupingEvent extends AEvent {
	/**
	 * Determines for which {@link IDCategory} the grouping should be loaded,
	 * i.e. whether rows or columns should be grouped.
	 */
	private IDCategory idCategory;

	public LoadGroupingEvent() {

	}

	public LoadGroupingEvent(ATableBasedDataDomain dataDomain, IDCategory idCategory) {
		setDataDomainID(dataDomain.getDataDomainID());
		this.idCategory = idCategory;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param idCategory
	 *            setter, see {@link #idCategory}
	 */
	public void setIdCategory(IDCategory idCategory) {
		this.idCategory = idCategory;
	}

	/**
	 * @return the idCategory, see {@link #idCategory}
	 */
	public IDCategory getIdCategory() {
		return idCategory;
	}

}
