package org.caleydo.view.matchmaker;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLMatchmakerView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLMatchmakerView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedCompareView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		AGLView view = createGLView(initSerializedView, glCanvas.getID());
		minSizeComposite.setView(view);
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedCompareView serializedView = new SerializedCompareView(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLMatchmaker.VIEW_ID;
	}

}