/**
 * 
 */
package org.caleydo.core.data.datadomain;


/**
 * Specifies whether {@link IDataDomain}s are
 * supported. This is required for views to determine whether they are able to
 * handle certain data or not.
 * 
 * @author Christian Partl
 * 
 */
public interface IDataSupportDefinition {

	/**
	 * @param dataDomain
	 * @return True, if the specified DataDomain is supported, false otherwise.
	 */
	public boolean isDataDomainSupported(IDataDomain dataDomain);

}
