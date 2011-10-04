package org.caleydo.core.view;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.configuration.DataConfiguration;
import org.caleydo.core.data.configuration.DataConfigurationChooser;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.startup.StartupProcessor;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Base class for all RCP views available in Caleydo.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class CaleydoRCPViewPart
	extends ViewPart {

	/** serialized representation of the view to initialize the view itself */
	protected ASerializedView serializedView;

	/** {@link JAXBContext} for view (de-)serialization */
	protected JAXBContext viewContext;

	protected static ArrayList<IAction> alToolbar;

	protected EventPublisher eventPublisher = null;

	protected IView view;

	/**
	 * stores the attach status of the viewpart, true means within caleydo's main window, false otherwise
	 */
	protected boolean attached;

	protected Composite parentComposite;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
	}

	/**
	 * Generates and returns a list of all views, caleydo-view-parts and gl-views, contained in this view.
	 * 
	 * @return list of all views contained in this view
	 */
	public List<IView> getAllViews() {
		List<IView> views = new ArrayList<IView>();
		views.add(getView());
		return views;
	}

	public IView getView() {
		return view;
	}

	public Composite getSWTComposite() {
		return parentComposite;
	}

	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		this.attached = attached;
	}

	@Override
	public void dispose() {
		// unregisterEventListeners();
		super.dispose();
	}

	/**
	 * Determines and sets the dataDomain based on the following rules:
	 * <ul>
	 * <li>If no dataDomain is registered, null is returned</li>
	 * <li>If a dataDomainType is set in the serializable representation this is used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can this is used</li>
	 * <li>Else an exception is thrown</li>
	 * <ul>
	 * 
	 * @param serializedView
	 */
	protected void determineDataConfiguration(ASerializedView serializedView) {

		// first we check if the data domain was manually specified
		for (Pair<String, String> startView : StartupProcessor.get().getAppInitData()
			.getAppArgumentStartViewWithDataDomain()) {
			if (startView.getFirst().equals(serializedView.getViewID())) {
				String dataDomainType = startView.getSecond();
				// StartupProcessor.get().getAppArgumentStartViewWithDataDomain().remove(startView);
				serializedView.setDataDomainID(dataDomainType);
			}
		}

		// then we check whether the serialization has a datadomain already
		String dataDomainID = serializedView.getDataDomainID();
		if (dataDomainID == null) {
			ArrayList<IDataDomain> availableDomains =
				DataDomainManager.get().getAssociationManager()
					.getAvailableDataDomainTypesForViewType(serializedView.getViewType());

			DataConfiguration config = DataConfigurationChooser.determineDataConfiguration(availableDomains);
			serializedView.setDataDomainID(config.getDataDomain().getDataDomainID());
			serializedView.setRecordPerspectiveID(config.getRecordPerspective().getPerspectiveID());
			serializedView.setDimensionPerspectiveID(config.getDimensionPerspective().getPerspectiveID());

		}
	}

	/**
	 * Creates a default serialized form ({@link ASerializedView}) of the contained gl-view
	 */
	public abstract void createDefaultSerializedView();

	/**
	 * Setting an external serialized view. Needed for RCP views that are embedded in another RCP view.
	 */
	public void setExternalSerializedView(ASerializedView serializedView) {
		this.serializedView = serializedView;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		String viewXml = null;
		if (memento != null) {
			viewXml = memento.getString("serialized");
		}
		if (viewXml != null) {
			// init view from memento
			JAXBContext jaxbContext = viewContext;
			Unmarshaller unmarshaller;
			try {
				unmarshaller = jaxbContext.createUnmarshaller();
			}
			catch (JAXBException ex) {
				throw new RuntimeException("could not create xml unmarshaller", ex);
			}

			StringReader xmlInputReader = new StringReader(viewXml);
			try {
				serializedView = (ASerializedView) unmarshaller.unmarshal(xmlInputReader);
			}
			catch (JAXBException ex) {
				throw new RuntimeException("could not deserialize view-xml", ex);
			}
			if (DataDomainManager.get().getDataDomainByID(serializedView.getDataDomainID()) == null)
				serializedView = null;
		}
		// this is the case if either the view has not been saved to a memento before, or the configuration
		// has changed and the serialization is invalid (e.g. different DataDomain is set)
		if (serializedView == null) {
			createDefaultSerializedView();
		}
	}

	@Override
	public void saveState(IMemento memento) {

		if (viewContext == null)
			return;

		JAXBContext jaxbContext = viewContext;
		Marshaller marshaller = null;
		try {
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("could not create xml marshaller", ex);
		}

		StringWriter xmlOutputWriter = new StringWriter();
		try {
			marshaller.marshal(serializedView, xmlOutputWriter);
			String xmlOutput = xmlOutputWriter.getBuffer().toString();
			memento.putString("serialized", xmlOutput);
		}
		catch (JAXBException ex) {
			ex.printStackTrace();
		}
	}

	public ASerializedView getSerializedView() {
		return serializedView;
	}
}
