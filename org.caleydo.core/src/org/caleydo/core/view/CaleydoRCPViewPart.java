package org.caleydo.core.view;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.gui.dialog.ChooseDataDomainDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.startup.StartupProcessor;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Base class for all RCP views available in Caleydo.
 * 
 * @author Marc Streit
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
	protected void determineDataDomain(ASerializedView serializedView) {

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
			if (availableDomains == null)
				throw new IllegalStateException("Not able to determine which data domain to use");
			else if (availableDomains.size() == 0)
				throw new IllegalStateException("No datadomain for this view loaded");
			else if (availableDomains.size() == 1)
				serializedView.setDataDomainID(availableDomains.get(0).getDataDomainID());
			else if (availableDomains.size() > 1) {
				ChooseDataDomainDialog dialog = new ChooseDataDomainDialog(new Shell());
				dialog.setPossibleDataDomains(availableDomains);
				IDataDomain chosenDataDomain = dialog.open();
				serializedView.setDataDomainID(chosenDataDomain.getDataDomainID());
			}
		}
	}

	/**
	 * Creates a default serialized form ({@link ASerializedView}) of the contained gl-view
	 */
	public abstract void createDefaultSerializedView();

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

			if (!(view instanceof AGLView))
				return;

			marshaller.marshal(((AGLView) view).getSerializableRepresentation(), xmlOutputWriter);
			String xmlOutput = xmlOutputWriter.getBuffer().toString();
			memento.putString("serialized", xmlOutput);
		}
		catch (JAXBException ex) {
			ex.printStackTrace();
		}
	}
}
