/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.dataset;

import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.view.info.dataset.internal.DataSetItems;
import org.caleydo.view.info.dataset.spi.IDataDomainDataSetItem;
import org.caleydo.view.info.dataset.spi.IDataSetItem;
import org.caleydo.view.info.dataset.spi.ITablePerspectiveDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

/**
 * Data meta view showing details about a data table.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class RcpDatasetInfoView extends CaleydoRCPViewPart implements IDataDomainBasedView<IDataDomain> {
	public static final String VIEW_TYPE = "org.caleydo.view.info.dataset";

	private IDataDomain dataDomain;
	private TablePerspective tablePerspective;

	// private Label unmappedDimensionElements;

	// private StyledText metaDataInfo;

	private final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	@DeepScan
	private final Collection<IDataSetItem> items = DataSetItems.create();

	/**
	 * Constructor.
	 */
	public RcpDatasetInfoView() {
		super(SerializedDatasetInfoView.class);
		listeners.register(this);
	}

	@Override
	public void dispose() {
		listeners.unregisterAll();
		super.dispose();
	}

	@Override
	public boolean isSupportView() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		ExpandBar expandBar = new ExpandBar(parent, SWT.V_SCROLL | SWT.NO_BACKGROUND);
		expandBar.setSpacing(1);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		parentComposite = expandBar;

		for(IDataSetItem item : items)
			item.create(expandBar);

		if (dataDomain == null) {
			setDataDomain(DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleTablePerspectiveBasedView) serializedView).getDataDomainID()));
		}

		parent.layout();
	}





	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		// Do nothing if new datadomain is the same as the current one, or if dd
		// is null
		if (dataDomain == this.dataDomain || dataDomain == null)
			return;

		this.dataDomain = dataDomain;

		updateDataSetInfo();

	}

	private void updateDataSetInfo() {
		for (IDataSetItem item : items) {
			if (item instanceof ITablePerspectiveDataSetItem)
				((ITablePerspectiveDataSetItem) item).update(dataDomain, tablePerspective);
			else if (item instanceof IDataDomainDataSetItem)
				((IDataDomainDataSetItem) item).update(dataDomain);
		}
	}


	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDatasetInfoView();
		determineDataConfiguration(serializedView, false);
	}

	@ListenTo
	private void onDataDomainUpdate(DataSetSelectedEvent event) {

		IDataDomain dd = event.getDataDomain();
		TablePerspective tp = event.getTablePerspective();
		// Do nothing if new datadomain is the same as the current one, or if dd
		// is null
		if (dd == null || (dd == this.dataDomain && tp == this.tablePerspective))
			return;

		this.dataDomain = dd;
		this.tablePerspective = tp;

		updateDataSetInfo();
		setDataDomain(event.getDataDomain());
	}



}
