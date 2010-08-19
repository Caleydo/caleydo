package org.caleydo.view.pathway;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;

public class RcpGLPathwayView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLPathwayView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedPathwayView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedPathwayView serializedView = new SerializedPathwayView(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLPathway.VIEW_ID;
	}

}