/**
 * 
 */
package org.caleydo.view.dvi.event;

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
	 * Datadomain for which a grouping should be loaded.
	 */
	private ATableBasedDataDomain dataDomain;

	/**
	 * Determines for which {@link IDCategory} the grouping should be loaded,
	 * i.e. whether rows or columns should be grouped.
	 */
	private IDCategory idCategory;

	public LoadGroupingEvent() {

	}

	public LoadGroupingEvent(ATableBasedDataDomain dataDomain, IDCategory idCategory) {
		this.dataDomain = dataDomain;
		this.idCategory = idCategory;
	}

	@Override
	public boolean checkIntegrity() {
		return dataDomain != null;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
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
