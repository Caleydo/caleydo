package org.caleydo.view.bookmark;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpBookmarkView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpBookmarkView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedBookmarkView.class);
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
		SerializedBookmarkView serializedView = new SerializedBookmarkView(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLBookmarkView.VIEW_ID;
	}

}