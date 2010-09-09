package org.caleydo.view.filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Filter view showing a pipeline of filters for a data set.
 * 
 * @author Marc Streit
 */
public class RcpFilterView extends CaleydoRCPViewPart {

	public static final String VIEW_ID = "org.caleydo.view.filter";

	/**
	 * Constructor.
	 */
	public RcpFilterView() {
		super();
		
		try {
			viewContext = JAXBContext.newInstance(SerializedFilterView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = parent;

		Button button = new Button(parentComposite, SWT.None);
		button.setText("huhu");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedFilterView();
		determineDataDomain(serializedView);
	}
}