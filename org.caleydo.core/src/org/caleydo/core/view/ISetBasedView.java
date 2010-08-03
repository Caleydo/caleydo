package org.caleydo.core.view;

import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.opengl.canvas.listener.INewSetHandler;

/**
 * Interface for views that show data which is stored in sets.
 * 
 * @author Alexander Lex
 */
public interface ISetBasedView
	extends IDataDomainBasedView<ASetBasedDataDomain>, IView, INewSetHandler {

}
