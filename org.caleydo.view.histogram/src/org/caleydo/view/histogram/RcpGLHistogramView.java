/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.ViewManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class RcpGLHistogramView
	extends ARcpGLViewPart
	implements IView, ISingleTablePerspectiveBasedView {

	protected Composite histoComposite;

	protected ATableBasedDataDomain dataDomain;
	protected TablePerspective tablePerspective;

	/**
	 * Constructor.
	 */
	public RcpGLHistogramView() {
		super(SerializedHistogramView.class);
	}

	@Override
	public void createPartControl(Composite parent) {

		minSizeComposite = new MinimumSizeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		minSizeComposite.setMinSize(0, 0);
		minSizeComposite.setExpandHorizontal(true);
		minSizeComposite.setExpandVertical(true);

		histoComposite = new Composite(minSizeComposite, SWT.NULL);
		minSizeComposite.setContent(histoComposite);
		minSizeComposite.setMinSize(160, 80);

		GridLayout baseLayout = new GridLayout(1, false);
		baseLayout.verticalSpacing = 2;
		histoComposite.setLayout(baseLayout);
		

		ViewManager viewManager = ViewManager.get();
		glCanvas = createGLCanvas(histoComposite);
		parentComposite = glCanvas.asComposite();
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewManager.registerGLCanvasToAnimator(glCanvas);

		view = new GLHistogram(glCanvas, serializedView.getViewFrustum());
		// ((GLHistogram) view).setRenderColorBars(false);
		initializeView();
		initialize();

		createPartControlGL();
		redrawView();
	}

	public void redrawView() {

	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedHistogramView();

		if (dataDomain == null)
			determineDataConfiguration(serializedView);
		else
			((ASerializedSingleTablePerspectiveBasedView) serializedView).setDataDomainID(dataDomain.getDataDomainID());
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public void initialize() {
		GLHistogram glHistogram = (GLHistogram) view;
		setDataDomain(glHistogram.getDataDomain());
		setTablePerspective(glHistogram.getTablePerspective());
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getViewType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public Set<IDataDomain> getDataDomains() {
		return Collections.singleton((IDataDomain) getDataDomain());
	}

	@Override
	public boolean isDataView() {
		return false;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		return ((ISingleTablePerspectiveBasedView) view).getTablePerspectives();
	}

	@Override
	public TablePerspective getTablePerspective() {
		return ((ISingleTablePerspectiveBasedView) view).getTablePerspective();
	}

	@Override
	public void setLabel(String label, boolean isLabelDefault) {
		view.setLabel(label, isLabelDefault);

	}

	@Override
	public String getLabel() {
		return view.getLabel();
	}

	@Override
	public boolean isLabelDefault() {
		return view.isLabelDefault();
	}

	@Override
	public String getProviderName() {
		return "Histogram";
	}

	@Override
	public void setLabel(String label) {
		view.setLabel(label);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.all;
	}

	@Override
	public int getInstanceNumber() {
		return 0;
	}

	@Override
	public void setInstanceNumber(int instanceNumber) {

	}

}
