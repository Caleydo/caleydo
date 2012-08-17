/**
 * 
 */
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

/**
 * Event that triggers a dialog to rename a specified {@link IDataDomain}.
 * 
 * @author Christian Partl
 * 
 */
public class RenameDataDomainEvent extends AEvent {

	/**
	 * The dataDomain to rename.
	 */
	private IDataDomain dataDomain;

	/**
	 * 
	 */
	public RenameDataDomainEvent() {
		// TODO Auto-generated constructor stub
	}

	public RenameDataDomainEvent(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public boolean checkIntegrity() {
		return dataDomain != null;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

}
