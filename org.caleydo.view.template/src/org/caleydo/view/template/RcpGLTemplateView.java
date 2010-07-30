package org.caleydo.view.template;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLTemplateView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTemplateView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedTemplateView serializedView = new SerializedTemplateView(
				dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLTemplate.VIEW_ID;
	}

}