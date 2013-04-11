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
package org.caleydo.view.scatterplot;



import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
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
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
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
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.scatterplot.dialogues.DataSelectionConfiguration;
import org.caleydo.view.scatterplot.dialogues.DataSelectionDialogue;
import org.caleydo.view.scatterplot.event.ShowDataSelectionDialogEvent;
import org.caleydo.view.scatterplot.renderstyle.ScatterplotRenderStyle;
import org.caleydo.view.scatterplot.utils.EDataGenerationType;
import org.caleydo.view.scatterplot.utils.EVisualizationSpaceType;
import org.caleydo.view.scatterplot.utils.ScatterplotRenderUtils;
import org.eclipse.core.runtime.IStatus;
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
 */

public class GLScatterplot extends AGLView implements ISingleTablePerspectiveBasedView, 
IEventBasedSelectionManagerUser  {

	public static String VIEW_TYPE = "org.caleydo.view.scatterplot";

	public static String VIEW_NAME = "Scatterplot";

	//private LayoutManager layoutManager;

	//private Row rendererLayout = new Row("Templaterenderer base layout");

	//private ScatterplotRenderer templateRenderer = new ScatterplotRenderer(this, rendererLayout);

	//private ScatterplotRenderStyle renderStyle;
	
	//private TablePerspective tablePerspective;
	//private ATableBasedDataDomain dataDomain;
	
	
	private ArrayList<ArrayList<Float>> dataColumns; 
	
	private TablePerspective tablePerspective;
	
	private ATableBasedDataDomain dataDomain;
	
	EventBasedSelectionManager selectionManager;
	
	private DataSelectionConfiguration dataSelectionConf;
	
		
	private final EventListenerManager listeners = EventListenerManagers.wrap(this);
	
	/**
	 * Flag to check whether data for the view is loaded
	 * Data is loaded after proper selections are made in the initial {@link DataSelectionDialogue}
	 */
	private boolean areDataColumnsSet = false;
	
	

	/**
	 * Constructor.
	 *
	 * @param glCanvas
	 * @param viewLabel
	 * @param viewFrustum
	 */
	public GLScatterplot(IGLCanvas glCanvas, Composite parentComposite, ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);
		
		dataColumns = new ArrayList<>();
		
		//TODO: Update according to whether the view is a dimension or item visualizations
		// All these will go after the data selection dialogue
		
		List<GeneticDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class);
		
		
		selectionManager = new EventBasedSelectionManager(this, dataDomains.get(0).getSampleIDType());
		selectionManager.registerEventListeners();
		
		
	}

	/**
	 * @return the dataColumns
	 */
	public ArrayList<ArrayList<Float>> getDataColumns() {
		return dataColumns;
	}

	/**
	 * @param dataColumns the dataColumns to set
	 */
	public void setDataColumns(ArrayList<ArrayList<Float>> dataColumns) {
		this.dataColumns = dataColumns;
	}
	
	/**
	 * @return the selectionManager
	 */
	public EventBasedSelectionManager getSelectionManager() {
		return selectionManager;
	}

	/**
	 * @param selectionManager the selectionManager to set
	 */
	public void setSelectionManager(EventBasedSelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		//renderStyle = new ScatterplotRenderStyle(viewFrustum);

		//layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
		//rendererLayout.addForeGroundRenderer(templateRenderer);
		//layoutManager.setBaseElementLayout(rendererLayout);

		//layoutManager.updateLayout();
		detailLevel = EDetailLevel.HIGH;
		
		ShowDataSelectionDialogEvent dataSelectionEvent = new ShowDataSelectionDialogEvent(tablePerspective);
		eventPublisher.triggerEvent(dataSelectionEvent);
		
		//this.prepareData();
		//Get the statistics here depending on the type!
		
		//Just fill in random data for now
		
		
	}
	
	@Override
	public void initData() {
		super.initData();
	}
	

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}
	
	/**
	 * Initializes the data columns
	 */
	public void prepareData(DataSelectionConfiguration dataSelectionConf)
	{
		if (tablePerspective != null & dataSelectionConf != null) {
			if (tablePerspective.getDataDomain().getTable() instanceof NumericalTable) {
				//StatContainer statisticsContext = tablePerspective.getContainerStatistics().getHistogram();
				//statisticsFocus = 
				
				this.dataSelectionConf = dataSelectionConf;
				
				Table table = dataDomain.getTable();
				
				
				ArrayList<Float> col1 = new ArrayList<Float>();
				ArrayList<Float> col2 = new ArrayList<Float>();
				
				// Use the actual data in the visualizations
				if (dataSelectionConf.getDataResourceType() == EDataGenerationType.RAW_DATA)
				{
					// It is an items space visualization, visual entities are records (rows, items)
					// Get the data along the selected columns "dataSelectionConf.getAxisIDs()"
					if (dataSelectionConf.getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
					{
						VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
						for (Integer recordID : recordVA) {
							col1.add( (float) table.getNormalizedValue(dataSelectionConf.getAxisIDs().get(0), recordID));
							col2.add( (float) table.getNormalizedValue(dataSelectionConf.getAxisIDs().get(1), recordID));
						}
					}
					// It is a dimension space visualization, visual entities are dimensions (columns)
					// Get the data along the selected records (rows, items) "dataSelectionConf.getAxisIDs()"
					else if (dataSelectionConf.getVisSpaceType() == EVisualizationSpaceType.DIMENSIONS_SPACE)
					{
						VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
						for (Integer dimensionID : dimensionVA) {
							col1.add( (float) table.getNormalizedValue(dimensionID, dataSelectionConf.getAxisIDs().get(0)));
							col2.add( (float) table.getNormalizedValue(dimensionID, dataSelectionConf.getAxisIDs().get(1)));
						}
					}
				}
				else if (dataSelectionConf.getDataResourceType() == EDataGenerationType.DERIVED_DATA)
				{
					//TODO: Perform statistics computations here
				}
				
				dataColumns.add(col1);
				dataColumns.add(col2);
				this.areDataColumnsSet = true;
			}

		}
	}

	@Override
	public void displayLocal(GL2 gl) {
		pickingManager.handlePicking(this, gl);
		display(gl);
		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}

	}

	@Override
	public void displayRemote(GL2 gl) {
		display(gl);
	}

	@Override
	public void display(GL2 gl) {
		
		
		//checkForHits(gl);
		//layoutManager.render(gl);
		
		processEvents();

		if (tablePerspective == null)
			return;
		
		if (!areDataColumnsSet)
			return;	

		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
		}

		gl.glCallList(displayListIndex);
		// numSentClearSelectionEvents = 0;

		if (!lazyMode)
			checkForHits(gl);
		
		
	}
	
	private void buildDisplayList(final GL2 gl, int displayListIndex) {

		if (hasFrustumChanged) {
			hasFrustumChanged = false;
		}
		gl.glNewList(displayListIndex, GL2.GL_COMPILE);

		/*if (tablePerspective.getNrRecords() == 0 || tablePerspective.getNrDimensions() == 0) {
			renderSymbol(gl, EIconTextures.HEAT_MAP_SYMBOL.getFileName(), 2);
		} else {
			layoutManager.render(gl);
			
		}*/
		
		ScatterplotRenderUtils.render(gl, this);
		//this.renderHistogram(gl);
		gl.glEndList();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedScatterplotView serializedForm = new SerializedScatterplotView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		
		//templateRenderer.registerEventListeners();
		addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				//bUpdateColorPointPosition = true;
				//bIsFirstTimeUpdateColor = true;
				//iColorMappingPointMoved = pick.getObjectID();
				System.out.println("Clickeeddd!!!!!");
				setDisplayListDirty();
			}

		}, EPickingType.MOUSE_DOWN.name());
		
		listeners.register(this);

	}
		
	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		
		listeners.unregisterAll();
		selectionManager.unregisterEventListeners();
		

	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		isDisplayListDirty = true;
		
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		isDisplayListDirty = true;
		
	}

	@Override
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyOfSelectionChange(
			EventBasedSelectionManager selectionManager) {
		// TODO Auto-generated method stub
		
	}
	
	
	// View specific event handlers 
	
	@ListenTo
	public void showDataSelectionDialog(final ShowDataSelectionDialogEvent event) {

		getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				DataSelectionDialogue dialog = new DataSelectionDialogue(shell, event.getTablePerspective());
				dialog.create();
				dialog.setBlockOnOpen(true);

				if (dialog.open() == IStatus.OK) {
					prepareData(dialog.getDataSelectionConf());
					setDisplayListDirty();

				}
			}

		});
	}

	/**
	 * @return the dataSelectionConf
	 */
	public DataSelectionConfiguration getDataSelectionConf() {
		return dataSelectionConf;
	}

	/**
	 * @param dataSelectionConf the dataSelectionConf to set
	 */
	public void setDataSelectionConf(DataSelectionConfiguration dataSelectionConf) {
		this.dataSelectionConf = dataSelectionConf;
	}
	
	

}
