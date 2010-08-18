package org.caleydo.core.view.swt;

import org.caleydo.core.view.IView;
import org.eclipse.swt.widgets.Composite;

public interface ISWTView
	extends IView {
	/**
	 * Same as initView() but creation of SWT Container via GeneralManager is replaced by external creation of
	 * SWT Container.
	 */
	public void initViewRCP(Composite parentComposite);

	public void initViewSWTComposite(Composite parentComposite);

	/**
	 * Initialization of the view. All initialization sets must be accomplished in this method.
	 */
	public void initView();

	/**
	 * Method is responsible for filling the composite with content.
	 */
	public void drawView();

	/**
	 * Returns the current SWT composite in which the view renders.
	 */
	public Composite getComposite();
}
