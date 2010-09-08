package org.caleydo.core.view;

import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;

/**
 * Interface for views that show data which is stored in sets.
 * 
 * @author Alexander Lex
 */
public interface IDataDomainSetBasedView
	extends IDataDomainBasedView<ASetBasedDataDomain>, IView {

}
