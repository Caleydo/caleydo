package org.caleydo.core.manager.datadomain;

public interface IDataDomainBasedView<DataDomainType extends IDataDomain> {

	/**
	 * Set the data domain which determines the behavior of the view. Attention: The data domain need not be
	 * changed at runtime.
	 * 
	 * @param dataDomain
	 */
	public void setDataDomain(DataDomainType dataDomain);

	/**
	 * Get the data domain the view is operating on
	 * 
	 * @return
	 */
	public DataDomainType getDataDomain();

}
