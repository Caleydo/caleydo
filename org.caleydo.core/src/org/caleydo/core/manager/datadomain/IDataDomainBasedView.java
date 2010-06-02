package org.caleydo.core.manager.datadomain;

import org.caleydo.core.manager.IDataDomain;


public interface IDataDomainBasedView <DataDomainType extends IDataDomain>{
	
	
	public void registerDataDomains();
	
//	/**
//	 * Tries to determine the data domain by the pool of registered and existing domains for that view.
//	 * If this is not possible because multiple data domains do exist, the user is asked to choose.
//	 */
//	public IDataDomain determineDataDomain();
	
	/**
	 * Set the data domain which determines the behavior of the view. Attention: The data domain need not be changed
	 * at runtime.
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
