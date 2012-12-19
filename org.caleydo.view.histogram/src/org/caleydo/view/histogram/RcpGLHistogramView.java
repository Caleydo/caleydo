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
package org.caleydo.view.histogram;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
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
import org.eclipse.jface.action.IAction;
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
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedHistogramView.class);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
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

		ViewManager viewManager = GeneralManager.get().getViewManager();
		glCanvas = createGLCanvas(histoComposite);
		parentComposite = glCanvas.asComposite();
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewManager.registerGLCanvasToAnimator(glCanvas);

		view = new GLHistogram(glCanvas, parentComposite, serializedView.getViewFrustum());
		// ((GLHistogram) view).setRenderColorBars(false);
		initializeView();
		initialize();

		createPartControlGL();
		redrawView();
	}

	public void redrawView() {

	}

	public static void createToolBarItems(int viewID) {
		alToolbar = new ArrayList<IAction>();
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
	public String getViewGUIID() {
		return GLHistogram.VIEW_TYPE;
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

}
