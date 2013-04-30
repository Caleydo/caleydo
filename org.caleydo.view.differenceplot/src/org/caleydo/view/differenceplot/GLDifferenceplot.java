/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.differenceplot;



import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.StatContainer;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.events.ISelectionHandler;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.events.IDimensionVAUpdateHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVAUpdateHandler;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;
import org.caleydo.core.view.listener.RemoveTablePerspectiveEvent;
import org.caleydo.core.view.listener.RemoveTablePerspectiveListener;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.differenceplot.dialogues.DataSelectionConfiguration;
import org.caleydo.view.differenceplot.dialogues.DataSelectionDialogue;
import org.caleydo.view.differenceplot.event.DifferenceplotDataSelectionEvent;
import org.caleydo.view.differenceplot.renderstyle.DifferenceplotRenderStyle;
import org.caleydo.view.differenceplot.toolbar.DataSelectionBarGUI;
import org.caleydo.view.differenceplot.utils.EDataGenerationType;
import org.caleydo.view.differenceplot.utils.EVisualizationSpaceType;
import org.caleydo.view.differenceplot.utils.DifferenceplotRenderUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * Sample GL2 view.
 * </p>
 * <p>
 * This Template is derived from {@link ATableBasedView}, but if the view does not use a table, changing that to
 * {@link AGLView} is necessary.
 * </p>
 * <p>
 * This assumes a {@link ISingleTablePerspectiveBasedView} - i.e., this view is managing one subset of data. The
 * alternative is an {@link IMultiTablePerspectiveBasedView}.
 * </p>
 * <p>
 * In addition the very common interfaces {@link IRecordVAUpdateHandler}, {@link IDimensionVAUpdateHandler} and
 * {@link ISelectionHandler} are implemented but may not be necessary depending on the type of view.
 * </p>
 *
 * @author Marc Streit
 * @author Alexander Lex
 * @author Cagatay Turkay
 */

public class GLDifferenceplot extends ASingleTablePerspectiveElementView {

	public static String VIEW_TYPE = "org.caleydo.view.differenceplot";
	public static String VIEW_NAME = "Differenceplot";
	
	DifferenceplotElement rootElement;
	DataSelectionBarGUI toolbar;
	
	public DataSelectionBarGUI getToolbar() {
		return toolbar;
	}


	public void setToolbar(DataSelectionBarGUI toolbar) {
		this.toolbar = toolbar;
	}


	public DifferenceplotElement getRootElement() {
		return rootElement;
	}


	public GLDifferenceplot(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	
	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDifferenceplotView serializedForm = new SerializedDifferenceplotView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}
	
	

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected void applyTablePerspective(AGLElementDecorator root, TablePerspective tablePerspective) {
		if (tablePerspective == null)
			root.setContent(null);
		else
		{
			rootElement = new DifferenceplotElement(tablePerspective, null);
			root.setContent(rootElement);
		}		
	}
	
	// View specific event handlers 
	
	@ListenTo
	public void differenceplotDataSelectionListener(DifferenceplotDataSelectionEvent event) {
		// When multiple instances of the view is open, this is to ensure 
		// that the data selection signal only propagates into the updated view
		if (event.getOwnerViewID() != this.getID())
		{
			//System.out.println("Event from another view, return!");
			return;
		}
		rootElement.prepareData(event.getDataSelectionConfiguration());
		
	}
}
