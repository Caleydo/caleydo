/**
 * 
 */
package org.caleydo.view.scatterplot;

import gleem.linalg.Vec2f;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.EStatisticsType;
import org.caleydo.core.data.perspective.table.StatisticsUtils;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.TablePerspectiveSelectionMixin;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.scatterplot.dialogues.DataSelectionConfiguration;
import org.caleydo.view.scatterplot.dialogues.DataSelectionDialogue;
import org.caleydo.view.scatterplot.event.ScatterplotDataSelectionEvent;
import org.caleydo.view.scatterplot.utils.EDataGenerationType;
import org.caleydo.view.scatterplot.utils.EVisualizationSpaceType;
import org.caleydo.view.scatterplot.utils.ScatterplotRenderUtils;
import org.caleydo.view.scatterplot.utils.SelectionRectangle;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;

/**
 * @author turkay
 *
 */
public class ScatterplotElement extends GLElement implements TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback 
{

	private final TablePerspective tablePerspective;

	

	@DeepScan
	private final TablePerspectiveSelectionMixin selection;
	
	private DataSelectionConfiguration dataSelectionConf;
	
	private int pickingId = -1;
	
	private SelectionRectangle selectionRect;
	
	private Point firstClickPoint;	

	private Point lastClickPoint;
	
	private ScatterplotRenderUtils renderUtil;
	
	private boolean rectanglePicked = false;
	
		
	/**
	 * Flag to check whether data for the view is loaded
	 * Data is loaded after proper selections are made in the initial {@link DataSelectionDialogue}
	 */
	private boolean dataColumnsSet = false;
	
	/**
	 * A flag to ensure that no rendering takes place
	 * when the data is being updated due to a change of data domain (triggered by toolbar)
	 */
	private boolean readyForRender = false;
	
	/**
	 * A flag to indicate if this view is rendering remote
	 * This could be updated when render details for GL_ELEMENT is updated
	 */
	private boolean renderRemote = false;
	

	private ArrayList<ArrayList<Float>> dataColumns;
	
	private IPickingListener canvasPickingListener;

	public ScatterplotElement(TablePerspective tablePerspective, DataSelectionConfiguration dataSelectionConfiguration) {
		this.tablePerspective = tablePerspective;
		this.selection = new TablePerspectiveSelectionMixin(tablePerspective, this);
		
		dataColumns = new ArrayList<>();
		
		setVisibility(EVisibility.PICKABLE);
		
		this.dataSelectionConf = dataSelectionConfiguration;
		
		if (dataSelectionConfiguration != null)
		{
			renderRemote = true;
			//this.prepareData(dataSelectionConfiguration);
			
		}
		
		
	}
	
	/**
	 * setup method, when adding a child to a parent
	 * @param context
	 */
	@Override
	protected void init(IGLElementContext context) 
	{
		super.init(context);
		this.prepareData(this.dataSelectionConf);
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		repaintAll();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		relayout();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (!dataColumnsSet | !readyForRender)
			return;	
		if (this.isRenderRemote())
		{
			if(h < 300 | w < 300)
			{
				h = ScatterPlotVisInfo.MIN_HEIGHT_PIXELS;
				w = ScatterPlotVisInfo.MIN_WIDTH_PIXELS;
			}
		}
		
		g.pushResourceLocator(Activator.getResourceLocator());
		//super.renderImpl(g, w, h);
		
		renderUtil.render(g.gl, this, w, h);
		
		renderUtil.renderSelectionRectangle(g.gl, selectionRect, w, h);
		
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (!dataColumnsSet | !readyForRender)
			return;	
		
		if (this.isRenderRemote())
		{
			if(h < 300 | w < 300)
			{
				h = ScatterPlotVisInfo.MIN_HEIGHT_PIXELS;
				w = ScatterPlotVisInfo.MIN_WIDTH_PIXELS;
			}
		}
		
		g.pushResourceLocator(Activator.getResourceLocator());
		//super.renderPickImpl(g, w, h);
		g.pushName(pickingId);
		renderUtil.render(g.gl, this, w, h);	
		g.popName();
		g.popResourceLocator();
	}
	
	/**
	 * Initializes the data columns
	 */
	public void prepareData(DataSelectionConfiguration dataSelectionConf)
	{
		this.readyForRender = false;
		System.out.println("Prepare Data called");
		dataColumns.clear();
		
		if (tablePerspective != null & dataSelectionConf != null) {
			if (tablePerspective.getDataDomain().getTable() instanceof NumericalTable) {
				//StatContainer statisticsContext = tablePerspective.getContainerStatistics().getHistogram();
				//statisticsFocus = 
				
				this.dataSelectionConf = dataSelectionConf;
				
				Table table = selection.getDataDomain().getTable();
				
				
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
					
					col1 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					col2 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
				}
				
				dataColumns.add(col1);
				dataColumns.add(col2);
				
				renderUtil = new ScatterplotRenderUtils(this.renderRemote);
				renderUtil.PerformDataLoadedOperations(this);
				
				// First time the dataset is loaded
				// also init listeners here
				if (!this.dataColumnsSet)
				{
					this.dataColumnsSet = true;
					initListeners();
					
				}
				else
				{
					
				}
				
				
				
				this.readyForRender = true;
				
				
				
				// Set the active selection manager, either 
				//activeSelectionManager = selection.getRecordSelectionManager();
				//if (dataSelectionConf.getVisSpaceType() == EVisualizationSpaceType.ITEMS_SPACE)
				//{
				//	activeSelectionManager = selection.getDimensionSelectionManager();
				//}
			}

		}
		
		
	}

	public void initListeners()
	{
		if (renderRemote)
		{
			canvasPickingListener = new IPickingListener() {
				@Override
				public void pick(Pick pick) {
					//onRecordPick(pick.getObjectID(), pick);
					handleMouseEventsRemoteMode(pick);
				}
			};
		}
		else
		{
			canvasPickingListener = new IPickingListener() {
				@Override
				public void pick(Pick pick) {
					//onRecordPick(pick.getObjectID(), pick);
					handleMouseEvents(pick);
				}
			};
		}
		
		pickingId = context.registerPickingListener(canvasPickingListener);
	}
	
	public void handleMouseEvents(Pick pick)
	{
		switch (pick.getPickingMode()) {
		case CLICKED:
			//System.out.println("clicked:  " + pick.getPickedPoint());
			firstClickPoint = pick.getPickedPoint();
			rectanglePicked = renderUtil.pickedSelectionRectangle(firstClickPoint, selectionRect);
			break;
		case DRAGGED:
			//Enlarge the selection rectangle here
			if(!rectanglePicked)
			{
				selectionRect = new SelectionRectangle();
				selectionRect.setLeft(firstClickPoint.x);
				selectionRect.setRight(pick.getPickedPoint().x);
				selectionRect.setTop(firstClickPoint.y);
				selectionRect.setBottom(pick.getPickedPoint().y);
			}
			else
			{
				selectionRect.moveRectangle(pick.getDx(), pick.getDy());
			}
			
			
			//selectionRect.ComputeScreenToDataMapping(renderUtil, dataColumns, getSize().x(), getSize().y());			
			//renderUtil.performBrushing(this, selectionRect);
			break;
		case MOUSE_RELEASED:
			//A single click to remove the selection
			
			if (Math.abs(pick.getPickedPoint().x - firstClickPoint.x) < 1 | Math.abs(pick.getPickedPoint().y - firstClickPoint.y) < 1)
			{
				if (!rectanglePicked)
				{
					selectionRect = null;
					renderUtil.clearSelection(this);
				}
			}
			else
			{
				selectionRect.ComputeScreenToDataMapping(renderUtil, dataColumns, getSize().x(), getSize().y());
				renderUtil.performBrushing(this, selectionRect);
			}
			break;	
		
		}
	}
	
	public void handleMouseEventsRemoteMode(Pick pick)
	{
		Point pickedPoint = new Point((int) this.toRelative(pick.getPickedPoint()).x(), (int) this.toRelative(pick.getPickedPoint()).y());
				
		switch (pick.getPickingMode()) {
		case CLICKED:
			//System.out.println("!!!!! clicked first picked point:  " + firstClickPoint);
			//firstClickPoint = pickedPoint;
			
			
			if(firstClickPoint == null)
			{
				firstClickPoint = pickedPoint;
				lastClickPoint = pickedPoint;
				return;
			}
			
			if (Math.abs(lastClickPoint.x - pickedPoint.x) > 3 | Math.abs(lastClickPoint.y - pickedPoint.y) > 3)
			{
					selectionRect = null;
					renderUtil.clearSelection(this);
					firstClickPoint = null;
					lastClickPoint = null;
			}

			break;
		case DRAGGED:
			//Enlarge the selection rectangle here
			
			//System.out.println("!!!!! dragged:  " + pick.getPickedPoint());
			if(firstClickPoint == null)
			{
				//System.out.println("******* dragged manual setting:  " + this.toRelative(pick.getPickedPoint()));
				firstClickPoint = pickedPoint;
			}
			
			lastClickPoint = pickedPoint;
			selectionRect = new SelectionRectangle();
			selectionRect.setLeft(firstClickPoint.x);
			selectionRect.setRight(pickedPoint.x);
			selectionRect.setTop(firstClickPoint.y);
			selectionRect.setBottom(pickedPoint.y);
			
			float renderWidth = 0;
			float renderHeight = 0;
			if(this.getSize().x() < 300 | this.getSize().y() < 300)
			{
				renderWidth = ScatterPlotVisInfo.MIN_WIDTH_PIXELS;
				renderHeight = ScatterPlotVisInfo.MIN_HEIGHT_PIXELS;
			}
			else
			{
				renderWidth = this.getSize().x();
				renderHeight = this.getSize().y();
			}
			selectionRect.ComputeScreenToDataMapping(renderUtil, dataColumns, renderWidth, renderHeight);
			renderUtil.performBrushing(this, selectionRect);
			break;
		
		
		}
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
	 * @return the selection
	 */
	public TablePerspectiveSelectionMixin getSelection() {
		return selection;
	}
	
	public boolean isDataColumnsSet() {
		return dataColumnsSet;
	}

	public void setAreDataColumnsSet(boolean dataColumnsSet) {
		this.dataColumnsSet = dataColumnsSet;
	}
	
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	public boolean isRenderRemote() {
		return renderRemote;
	}

	public void setRenderRemote(boolean renderRemote) {
		this.renderRemote = renderRemote;
	}
	
	

//	/**
//	 * @return the dataSelectionConf
//	 */
//	public DataSelectionConfiguration getDataSelectionConf() {
//		return dataSelectionConf;
//	}
//
//	/**
//	 * @param dataSelectionConf the dataSelectionConf to set
//	 */
//	public void setDataSelectionConf(DataSelectionConfiguration dataSelectionConf) {
//		this.dataSelectionConf = dataSelectionConf;
//	}
//	
//	@Override
//	public void init(GL2 gl) {
//		displayListIndex = gl.glGenLists(1);
//		//renderStyle = new ScatterplotRenderStyle(viewFrustum);
//
//		//layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
//		//rendererLayout.addForeGroundRenderer(templateRenderer);
//		//layoutManager.setBaseElementLayout(rendererLayout);
//
//		//layoutManager.updateLayout();
//		detailLevel = EDetailLevel.HIGH;
//		
//		ShowDataSelectionDialogEvent dataSelectionEvent = new ShowDataSelectionDialogEvent(tablePerspective);
//		eventPublisher.triggerEvent(dataSelectionEvent);
//		
//		//this.prepareData();
//		//Get the statistics here depending on the type!
//		
//		//Just fill in random data for now
//		
//		
//	}
//	
//	@Override
//	public void initData() {
//		super.initData();
//	}
//	
//
//	@Override
//	public void initLocal(GL2 gl) {
//		init(gl);
//	}
//
//	@Override
//	public void initRemote(final GL2 gl, final AGLView glParentView, final GLMouseListener glMouseListener) {
//
//		// Register keyboard listener to GL2 canvas
//		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				glParentView.getParentComposite().addKeyListener(glKeyListener);
//			}
//		});
//
//		this.glMouseListener = glMouseListener;
//
//		init(gl);
//	}
//	

//	
//	public void setupViewBasedOnDataSelection()
//	{
//		//selectionManager = new EventBasedSelectionManager(this, dataDomains.get(0).getSampleIDType());
//		//selectionManager.registerEventListeners();
//	}
//
//	@Override
//	public void displayLocal(GL2 gl) {
//		pickingManager.handlePicking(this, gl);
//		display(gl);
//		if (busyState != EBusyState.OFF) {
//			renderBusyMode(gl);
//		}
//
//	}
//
//	@Override
//	public void displayRemote(GL2 gl) {
//		display(gl);
//	}
//
//	@Override
//	public void display(GL2 gl) {
//		
//		
//		//checkForHits(gl);
//		//layoutManager.render(gl);
//		
//		processEvents();
//
//		if (tablePerspective == null)
//			return;
//		
//		if (!areDataColumnsSet)
//			return;	
//
//		if (isDisplayListDirty) {
//			buildDisplayList(gl, displayListIndex);
//			isDisplayListDirty = false;
//		}
//
//		gl.glCallList(displayListIndex);
//		// numSentClearSelectionEvents = 0;
//
//		if (!lazyMode)
//			checkForHits(gl);
//		
//		
//	}
//	
//	private void buildDisplayList(final GL2 gl, int displayListIndex) {
//
//		if (hasFrustumChanged) {
//			hasFrustumChanged = false;
//		}
//		gl.glNewList(displayListIndex, GL2.GL_COMPILE);
//
//		/*if (tablePerspective.getNrRecords() == 0 || tablePerspective.getNrDimensions() == 0) {
//			renderSymbol(gl, EIconTextures.HEAT_MAP_SYMBOL.getFileName(), 2);
//		} else {
//			layoutManager.render(gl);
//			
//		}*/
//		
//		ScatterplotRenderUtils.render(gl, this);
//		//this.renderHistogram(gl);
//		gl.glEndList();
//	}

	
	




	

}
