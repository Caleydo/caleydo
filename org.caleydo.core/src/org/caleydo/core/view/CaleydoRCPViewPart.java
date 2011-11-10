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
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
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

	protected AView view;

	/**
	 * Flat determines whether a view changes its content when another data domain is selected.
	 */
	protected boolean isSupportView = false;

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

	@Override
	public void dispose() {
		// unregisterEventListeners();
		RCPViewManager.get().removeRCPView(this.getViewSite().getSecondaryId());
		super.dispose();
	}

	/**
	 * <p>
	 * If applicable initializes the {@link #view} with the {@link ADataDomain} and the {@link DataContainer}
	 * as they are specified in the {@link #serializedView}.
	 * </p>
	 * <p>
	 * Calls {@link AGLView#initialize()} and
	 * {@link IView#initFromSerializableRepresentation(ASerializedView)} with the {@link #serializedView}
	 * variable.
	 * </p>
	 */
	protected void initializeViewWithData() {
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain =
				DataDomainManager.get().getDataDomainByID(
					((ASerializedTopLevelDataView) serializedView).getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
	}

	/**
	 * Determines and sets the dataDomain to the {@link #serializedView} based on the following rules:
	 * <ul>
	 * <li>If no dataDomain is registered, null is returned</li>
	 * <li>If a dataDomainID is set in the serializable representation this is used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can this is used</li>
	 * <li>Else if there was a dataDomainID provided during the creation of the view</li>
	 * <li>Else an exception is thrown</li>
	 * <ul>
	 * 
	 * @param serializedView
	 */
	protected void determineDataConfiguration(ASerializedView serializedView) {
		determineDataConfiguration(serializedView, true);
	}

	/**
	 * Determines and sets the dataDomain based on the following rules:
	 * <ul>
	 * <li>If no dataDomain is registered, null is returned</li>
	 * <li>If a dataDomainID is set in the serializable representation this is used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can this is used</li>
	 * <li>Else if there was a dataDomainID provided during the creation of the view</li>
	 * <li>Else an exception is thrown</li>
	 * <ul>
	 * 
	 * @param serializedView
	 */
	protected void determineDataConfiguration(ASerializedView serializedView, boolean letUserChoose) {

		if (!(serializedView instanceof ASerializedTopLevelDataView))
			return;

		ASerializedTopLevelDataView serializedTopLevelDataView = (ASerializedTopLevelDataView) serializedView;

		// then we check whether the serialization has a data domain already
		String dataDomainID = serializedTopLevelDataView.getDataDomainID();

		// check whether the data domain ID was provided during the view creation
		if (dataDomainID == null) {
			RCPViewInitializationData rcpViewInitData =
				RCPViewManager.get().getRCPViewInitializationData(this.getViewSite().getSecondaryId());
			if (rcpViewInitData != null) {
				dataDomainID = rcpViewInitData.getDataDomainID();
				serializedTopLevelDataView.setDataDomainID(dataDomainID);

				serializedTopLevelDataView.setRecordPerspectiveID(((ATableBasedDataDomain) DataDomainManager
					.get().getDataDomainByID(dataDomainID)).getTable().getDefaultRecordPerspective().getID());

				serializedTopLevelDataView
					.setDimensionPerspectiveID(((ATableBasedDataDomain) DataDomainManager.get()
						.getDataDomainByID(dataDomainID)).getTable().getDefaultDimensionPerspective().getID());
			}
		}

		// ask the user to choose the data domain ID
		if (dataDomainID == null) {
			ArrayList<IDataDomain> availableDomains =
				DataDomainManager.get().getAssociationManager()
					.getAvailableDataDomainTypesForViewType(serializedView.getViewType());

			DataConfiguration config =
				DataConfigurationChooser.determineDataConfiguration(availableDomains,
					serializedView.getViewType(), letUserChoose);

			// for some views its ok if initially no data is set
			if (config.getDataDomain() == null)
				return;

			serializedTopLevelDataView.setDataDomainID(config.getDataDomain().getDataDomainID());
			serializedTopLevelDataView.setRecordPerspectiveID(config.getRecordPerspective().getID());
			serializedTopLevelDataView.setDimensionPerspectiveID(config.getDimensionPerspective().getID());
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
			if (serializedView instanceof ASerializedTopLevelDataView
				&& DataDomainManager.get().getDataDomainByID(
					((ASerializedTopLevelDataView) serializedView).getDataDomainID()) == null)
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

	/**
	 * Returns the purpose of a view. Support views change its content upon selection of a different data
	 * domain in another view.
	 * 
	 * @return
	 */
	public boolean isSupportView() {
		return isSupportView;
	}
}
