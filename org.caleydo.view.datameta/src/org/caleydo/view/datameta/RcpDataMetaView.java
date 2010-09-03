package org.caleydo.view.datameta;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.histogram.RcpGLHistogramView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpDataMetaView extends CaleydoRCPViewPart {

	public final static String VIEW_ID = "org.caleydo.view.datameta";
	
	private DataMetaView dataMetaView;
	
	/**
	 * Constructor.
	 */
	public RcpDataMetaView() {
		super();
		
		// Make sure the plugin is loaded and the view creator initializes the data domains for the views.
		// This is essential when the view is created by the workbench 
		GeneralManager.get().getViewGLCanvasManager().getViewCreator(VIEW_ID);
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedDataMetaView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		
		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));
		
	    GridData gridData = new GridData();
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.verticalAlignment = GridData.FILL;
		
		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayoutData(gridData);
		infoComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite histoComposite = new Composite(parentComposite, SWT.NULL);
		histoComposite.setLayoutData(gridData);
		histoComposite.setLayout(new FillLayout(SWT.VERTICAL));

		dataMetaView = new DataMetaView();
		SerializedDataMetaView serializedView = new SerializedDataMetaView();
		dataMetaView.setDataDomain((ASetBasedDataDomain) DataDomainManager
				.getInstance().getDataDomain(determineDataDomain(serializedView)));
		dataMetaView.createControl(infoComposite);
		
		RcpGLHistogramView histogramView = new RcpGLHistogramView();
		histogramView.setDataDomain(dataMetaView.getDataDomain());
		histogramView.createPartControl(histoComposite);
		// Usually the canvas is registered to the GL animator in the PartListener.
		// Because the GL histogram is no usual RCP view we have to do it on our own
		GeneralManager.get().getViewGLCanvasManager().registerGLCanvasToAnimator(histogramView.getGLCanvas());
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

}