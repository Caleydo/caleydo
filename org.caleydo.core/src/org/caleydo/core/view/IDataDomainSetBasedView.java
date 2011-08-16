package org.caleydo.core.view;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;

/**
 * Interface for views that show data which is stored in sets.
 * 
 * @author Alexander Lex
 */
public interface IDataDomainSetBasedView
	extends IDataDomainBasedView<ATableBasedDataDomain>, IView {

}
