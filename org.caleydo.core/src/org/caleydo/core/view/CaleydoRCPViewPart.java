/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.listener.ExtendedSelectionUpdateListener;
import org.caleydo.core.view.listener.IExtendedSelectionUpdateHandler;
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
public abstract class CaleydoRCPViewPart extends ViewPart implements IListenerOwner,
		IExtendedSelectionUpdateHandler {

	/** serialized representation of the view to initialize the view itself */
	protected ASerializedView serializedView;

	/** {@link JAXBContext} for view (de-)serialization */
	protected JAXBContext viewContext;

	protected static ArrayList<IAction> alToolbar;

	protected EventPublisher eventPublisher = null;

	protected AView view;

	/**
	 * Flat determines whether a view changes its content when another data
	 * domain is selected.
	 */
	protected boolean isSupportView = false;

	protected Composite parentComposite;

	protected ExtendedSelectionUpdateListener selectionUpdateListener;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		eventPublisher = GeneralManager.get().getEventPublisher();

		registerEventListeners();
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
	}

	/**
	 * Generates and returns a list of all views, caleydo-view-parts and
	 * gl-views, contained in this view.
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
		unregisterEventListeners();
		RCPViewManager.get().removeRCPView(this.getViewSite().getSecondaryId());
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	/**
	 * <p>
	 * If applicable initializes the {@link #view} with the {@link ADataDomain}
	 * and the {@link TablePerspective}, or with multiple TablePerspectives as they
	 * are specified in the {@link #serializedView}.
	 * </p>
	 * <p>
	 * Calls {@link AGLView#initialize()} and
	 * {@link IView#initFromSerializableRepresentation(ASerializedView)} with
	 * the {@link #serializedView} variable.
	 * </p>
	 */
	protected void initializeView() {
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleTablePerspectiveBasedView) serializedView)
							.getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		if (view instanceof ISingleTablePerspectiveBasedView) {
			ISingleTablePerspectiveBasedView singleTablePerspectiveBasedView = (ISingleTablePerspectiveBasedView) view;

			ASerializedSingleTablePerspectiveBasedView serializedSingleTablePerspectiveBasedView = (ASerializedSingleTablePerspectiveBasedView) serializedView;

			ATableBasedDataDomain tDataDomain = (ATableBasedDataDomain) DataDomainManager
					.get().getDataDomainByID(
							serializedSingleTablePerspectiveBasedView.getDataDomainID());

			TablePerspective container = tDataDomain
					.getTablePerspective(serializedSingleTablePerspectiveBasedView
							.getTablePerspectiveKey());
			// In case the stored TablePerspective is not available in this run
			if (container == null) {
				createDefaultSerializedView();
				serializedSingleTablePerspectiveBasedView = (ASerializedSingleTablePerspectiveBasedView) serializedView;
			} else {
				singleTablePerspectiveBasedView.setTablePerspective(container);
			}

		} else if (view instanceof IMultiTablePerspectiveBasedView) {
			IMultiTablePerspectiveBasedView multiTablePerspectiveBasedView = (IMultiTablePerspectiveBasedView) view;
			ASerializedMultiTablePerspectiveBasedView serializedMultiTablePerspectiveBasedView = (ASerializedMultiTablePerspectiveBasedView) serializedView;

			if (serializedMultiTablePerspectiveBasedView.getDataDomainAndTablePerspectiveKeys() != null) {
				boolean inconsistentSerializedView = false;
				for (Pair<String, String> data : serializedMultiTablePerspectiveBasedView
						.getDataDomainAndTablePerspectiveKeys()) {

					ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager
							.get().getDataDomainByID(data.getFirst());
					TablePerspective tablePerspective = ((ATableBasedDataDomain) dataDomain)
							.getTablePerspective(data.getSecond());
					if (tablePerspective == null) {
						inconsistentSerializedView = true;
						break;
					}
					multiTablePerspectiveBasedView.addTablePerspective(tablePerspective);
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
	 * Determines and sets the dataDomain to the {@link #serializedView} based
	 * on the following rules:
	 * <ul>
	 * <li>If no dataDomain is registered, null is returned</li>
	 * <li>If a dataDomainID is set in the serializable representation this is
	 * used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can
	 * this is used</li>
	 * <li>Else if there was a dataDomainID provided during the creation of the
	 * view</li>
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
	 * <li>If a dataDomainID is set in the serializable representation this is
	 * used</li>
	 * <li>Else if there is exactly one loaded dataDomain which the view can
	 * this is used</li>
	 * <li>Else if there was a dataDomainID provided during the creation of the
	 * view</li>
	 * <li>Else an exception is thrown</li>
	 * <ul>
	 * 
	 * @param serializedView
	 */
	protected void determineDataConfiguration(ASerializedView serializedView,
			boolean letUserChoose) {

		if (!(serializedView instanceof ASerializedSingleTablePerspectiveBasedView))
			return;

		ASerializedSingleTablePerspectiveBasedView serializedTopLevelDataView = (ASerializedSingleTablePerspectiveBasedView) serializedView;

		// then we check whether the serialization has a data domain already
		String dataDomainID = serializedTopLevelDataView.getDataDomainID();

		// check whether the data domain ID was provided during the view
		// creation
		if (dataDomainID == null) {
			RCPViewInitializationData rcpViewInitData = RCPViewManager.get()
					.getRCPViewInitializationData(this.getViewSite().getSecondaryId());
			if (rcpViewInitData != null) {
				dataDomainID = rcpViewInitData.getDataDomainID();

				TablePerspective tablePerspective = rcpViewInitData.getTablePerspective();
				serializedTopLevelDataView.setDataDomainID(dataDomainID);
				if (tablePerspective != null) {
					serializedTopLevelDataView.setTablePerspectiveKey(tablePerspective
							.getTablePerspectiveKey());

				} else {
					serializedTopLevelDataView
							.setTablePerspectiveKey(((ATableBasedDataDomain) DataDomainManager
									.get().getDataDomainByID(dataDomainID))
									.getDefaultTablePerspective().getTablePerspectiveKey());
				}
			}
		}

		// ask the user to choose the data domain ID
		if (dataDomainID == null) {
			ArrayList<ATableBasedDataDomain> availableDomains = DataDomainManager.get()
					.getAssociationManager()
					.getTableBasedDataDomainsForView(serializedView.getViewType());

			DataConfiguration config = DataConfigurationChooser
					.determineDataConfiguration(availableDomains,
							serializedView.getViewLabel(), letUserChoose);

			// for some views its ok if initially no data is set
			if (config.getDataDomain() == null
					|| config.getDimensionPerspective() == null
					|| config.getRecordPerspective() == null)
				return;

			serializedTopLevelDataView.setDataDomainID(config.getDataDomain()
					.getDataDomainID());
			serializedTopLevelDataView.setTablePerspectiveKey(config
					.getDataDomain()
					.getTablePerspective(config.getRecordPerspective().getPerspectiveID(),
							config.getDimensionPerspective().getPerspectiveID())
					.getTablePerspectiveKey());
		}
	}

	/**
	 * Creates a default serialized form ({@link ASerializedView}) of the
	 * contained gl-view
	 */
	public abstract void createDefaultSerializedView();

	/**
	 * Setting an external serialized view. Needed for RCP views that are
	 * embedded in another RCP view.
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
							((ASerializedSingleTablePerspectiveBasedView) serializedView)
									.getDataDomainID()) == null) {
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
	 * Returns the purpose of a view. Support views change its content upon
	 * selection of a different data domain in another view.
	 * 
	 * @return
	 */
	public boolean isSupportView() {
		return isSupportView;
	}

	@Override
	public void registerEventListeners() {

		selectionUpdateListener = new ExtendedSelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta, String dataDomainID) {

		if (!isSupportView())
			return;

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager
				.get().getDataDomainByID(dataDomainID);
		if (dataDomain == null)
			return;

		if (this instanceof IDataDomainBasedView) {
			((IDataDomainBasedView) this).setDataDomain(dataDomain);
		} else if (this.getView() instanceof IDataDomainBasedView) {
			((IDataDomainBasedView) (this)).setDataDomain(dataDomain);
		}
	}

	@Override
	public synchronized void queueEvent(
			final AEventListener<? extends IListenerOwner> listener, final AEvent event) {

		if (parentComposite == null || parentComposite.isDisposed())
			return;

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}
}
