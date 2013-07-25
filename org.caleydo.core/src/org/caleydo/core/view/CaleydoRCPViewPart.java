/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IToolBarManager;
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
public abstract class CaleydoRCPViewPart extends ViewPart {

	/** serialized representation of the view to initialize the view itself */
	protected ASerializedView serializedView;

	/** {@link JAXBContext} for view (de-)serialization */
	protected JAXBContext viewContext;

	protected AView view;

	protected Composite parentComposite;

	public CaleydoRCPViewPart(Class<? extends ASerializedView> serializedViewClass) {
		try {
			viewContext = JAXBContext.newInstance(serializedViewClass);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
	}

	protected final void fillToolBar() {
		addToolBarContent(getViewSite().getActionBars().getToolBarManager());
	}

	/**
	 * Empty toolbar in base classe. If views need a toolbar, they need to override this method.
	 *
	 * @param toolBarManager
	 *            TODO
	 */
	protected void addToolBarContent(IToolBarManager toolBarManager) {

	}

	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}

	public IView getView() {
		return view;
	}

	public Composite getSWTComposite() {
		return parentComposite;
	}

	@Override
	public void dispose() {
		RCPViewManager.get().removeRCPView(this.getViewSite().getSecondaryId());
		this.parentComposite = null;
		this.view = null;
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	/**
	 * <p>
	 * If applicable initializes the {@link #view} with the {@link ADataDomain} and the {@link TablePerspective}, or
	 * with multiple TablePerspectives as they are specified in the {@link #serializedView}.
	 * </p>
	 * <p>
	 * Calls {@link AGLView#initialize()} and {@link IView#initFromSerializableRepresentation(ASerializedView)} with the
	 * {@link #serializedView} variable.
	 * </p>
	 */
	protected void initializeView() {
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleTablePerspectiveBasedView) serializedView).getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		if (view instanceof ISingleTablePerspectiveBasedView) {
			ISingleTablePerspectiveBasedView singleTablePerspectiveBasedView = (ISingleTablePerspectiveBasedView) view;

			ASerializedSingleTablePerspectiveBasedView serializedSingleTablePerspectiveBasedView = (ASerializedSingleTablePerspectiveBasedView) serializedView;

			ATableBasedDataDomain tDataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
					serializedSingleTablePerspectiveBasedView.getDataDomainID());

			if (tDataDomain != null) {
				TablePerspective tablePerspective = tDataDomain
						.getTablePerspective(serializedSingleTablePerspectiveBasedView.getTablePerspectiveKey());
				// In case the stored TablePerspective is not available in this
				// run
				if (tablePerspective == null) {
					createDefaultSerializedView();
					serializedSingleTablePerspectiveBasedView = (ASerializedSingleTablePerspectiveBasedView) serializedView;
				} else {
					singleTablePerspectiveBasedView.setTablePerspective(tablePerspective);
				}
			}

		} else if (view instanceof IMultiTablePerspectiveBasedView) {
			IMultiTablePerspectiveBasedView multiTablePerspectiveBasedView = (IMultiTablePerspectiveBasedView) view;
			ASerializedMultiTablePerspectiveBasedView serializedMultiTablePerspectiveBasedView = (ASerializedMultiTablePerspectiveBasedView) serializedView;

			if (serializedMultiTablePerspectiveBasedView.getDataDomainAndTablePerspectiveKeys() != null) {
				boolean inconsistentSerializedView = false;
				for (Pair<String, String> data : serializedMultiTablePerspectiveBasedView
						.getDataDomainAndTablePerspectiveKeys()) {
					if (data == null) {
						inconsistentSerializedView = true;
						break;
					}
					ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
							.getDataDomainByID(data.getFirst());
					if (dataDomain == null) {
						inconsistentSerializedView = true;
						break;
					}
					TablePerspective tablePerspective = dataDomain.getTablePerspective(data.getSecond());
					if (tablePerspective == null) {
						inconsistentSerializedView = true;
						break;
					}
					if (multiTablePerspectiveBasedView.getDataSupportDefinition().apply(
							tablePerspective.getDataDomain())) {
						multiTablePerspectiveBasedView.addTablePerspective(tablePerspective);
					}
				}
				if (inconsistentSerializedView) {
					createDefaultSerializedView();
					serializedMultiTablePerspectiveBasedView = (ASerializedMultiTablePerspectiveBasedView) serializedView;
				}
			}
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
		if (!(serializedView instanceof ASerializedSingleTablePerspectiveBasedView))
			return;

		ASerializedSingleTablePerspectiveBasedView serializedTopLevelDataView = (ASerializedSingleTablePerspectiveBasedView) serializedView;

		// then we check whether the serialization has a data domain already
		String dataDomainID = serializedTopLevelDataView.getDataDomainID();

		// check whether the data domain ID was provided during the view
		// creation
		if (dataDomainID == null) {
			RCPViewInitializationData rcpViewInitData = RCPViewManager.get().getRCPViewInitializationData(
					this.getViewSite().getSecondaryId());
			if (rcpViewInitData != null) {
				dataDomainID = rcpViewInitData.getDataDomainID();

				TablePerspective tablePerspective = rcpViewInitData.getTablePerspective();
				serializedTopLevelDataView.setDataDomainID(dataDomainID);
				if (tablePerspective != null) {
					serializedTopLevelDataView.setTablePerspectiveKey(tablePerspective.getTablePerspectiveKey());

				} else {
					serializedTopLevelDataView.setTablePerspectiveKey(((ATableBasedDataDomain) DataDomainManager.get()
							.getDataDomainByID(dataDomainID)).getDefaultTablePerspective().getTablePerspectiveKey());
				}
			}
		}

		// ask the user to choose the data domain ID
		if (dataDomainID == null) {
			List<ATableBasedDataDomain> availableDomains = DataDomainManager.get().getAssociationManager()
					.getTableBasedDataDomainsForView(serializedView.getViewType());

			List<ATableBasedDataDomain> supportedDataDomains = new ArrayList<ATableBasedDataDomain>(
					availableDomains.size());

			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] dataSupportConfigElements = registry
					.getConfigurationElementsFor("org.caleydo.view.DataSupport");
			IConfigurationElement dataSupportConfigForCurrentView = null;
			for (IConfigurationElement configurationElement : dataSupportConfigElements) {
				if (configurationElement.getAttribute("viewID").equals(serializedView.getViewType())) {
					dataSupportConfigForCurrentView = configurationElement;
					break;
				}
			}

			if (dataSupportConfigForCurrentView != null) {
				for (ATableBasedDataDomain dataDomain : availableDomains) {
					IDataSupportDefinition supportDefinition;
					try {
						supportDefinition = (IDataSupportDefinition) dataSupportConfigForCurrentView
								.createExecutableExtension("class");

						if (supportDefinition.apply(dataDomain)) {
							supportedDataDomains.add(dataDomain);
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			DataConfiguration config = DataConfigurationChooser.determineDataConfiguration(
					dataSupportConfigForCurrentView == null ? availableDomains : supportedDataDomains,
					serializedView.getViewLabel(), letUserChoose);

			// for some views its ok if initially no data is set
			if (config.getDataDomain() == null || config.getDimensionPerspective() == null
					|| config.getRecordPerspective() == null)
				return;

			serializedTopLevelDataView.setDataDomainID(config.getDataDomain().getDataDomainID());
			serializedTopLevelDataView.setTablePerspectiveKey(config
					.getDataDomain()
					.getTablePerspective(config.getRecordPerspective().getPerspectiveID(),
							config.getDimensionPerspective().getPerspectiveID()).getTablePerspectiveKey());
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
			} catch (JAXBException ex) {
				throw new RuntimeException("could not create xml unmarshaller", ex);
			}

			StringReader xmlInputReader = new StringReader(viewXml);
			try {
				serializedView = (ASerializedView) unmarshaller.unmarshal(xmlInputReader);
			} catch (JAXBException ex) {
				throw new RuntimeException("could not deserialize view-xml", ex);
			}
			if (serializedView instanceof ASerializedSingleTablePerspectiveBasedView
					&& DataDomainManager.get().getDataDomainByID(
							((ASerializedSingleTablePerspectiveBasedView) serializedView).getDataDomainID()) == null) {
				serializedView = null;
			}
			if (serializedView instanceof ASerializedMultiTablePerspectiveBasedView) {
				ASerializedMultiTablePerspectiveBasedView v = (ASerializedMultiTablePerspectiveBasedView) serializedView;
				// v.getDataDomainAndTablePerspectiveKeys();
			}
		}
		// this is the case if either the view has not been saved to a memento
		// before, or the configuration
		// has changed and the serialization is invalid (e.g. different
		// DataDomain is set)
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
		} catch (JAXBException ex) {
			throw new RuntimeException("could not create xml marshaller", ex);
		}

		StringWriter xmlOutputWriter = new StringWriter();
		try {
			marshaller.marshal(getSerializedView(), xmlOutputWriter);
			String xmlOutput = xmlOutputWriter.getBuffer().toString();
			memento.putString("serialized", xmlOutput);
		} catch (JAXBException ex) {
			ex.printStackTrace();
		}
	}

	public ASerializedView getSerializedView() {
		return serializedView;
	}

	/**
	 * Returns the purpose of a view. Support views change its content upon selection of a different data domain in
	 * another view.
	 *
	 * @return
	 */
	public boolean isSupportView() {
		return false;
	}
}
