/**
 * 
 */
package org.caleydo.core.data.datadomain;

import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * Default definition that can be used if all {@link DataDomain}s are supported.
 * 
 * @author Christian Partl
 * 
 */
public class DefaultDataSupportDefinition implements IDataSupportDefinition {

	@Override
	public boolean isDataDomainSupported(IDataDomain dataDomain) {
		return true;
	}

}
