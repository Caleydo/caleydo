/**
 * 
 */
package org.caleydo.view.differenceplot;

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
import org.caleydo.core.view.opengl.layout2.WindowGLElement;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.differenceplot.dialogues.DataSelectionConfiguration;
import org.caleydo.view.differenceplot.dialogues.DataSelectionDialogue;
import org.caleydo.view.differenceplot.event.DifferenceplotDataSelectionEvent;
import org.caleydo.view.differenceplot.utils.EDataGenerationType;
import org.caleydo.view.differenceplot.utils.EVisualizationSpaceType;
import org.caleydo.view.differenceplot.utils.DifferenceplotRenderUtils;
import org.caleydo.view.differenceplot.utils.SelectionRectangle;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;

/**
 * @author turkay
 *
 */
public class DifferenceplotElement extends GLElement implements TablePerspectiveSelectionMixin.ITablePerspectiveMixinCallback 
{

	private final TablePerspective tablePerspective;

	

	@DeepScan
	private final TablePerspectiveSelectionMixin selection;

	
	private DataSelectionConfiguration dataSelectionConf;
	
	private int pickingId = -1;
	
	private SelectionRectangle selectionRect;
	
	private Point firstClickPoint;
	private Point lastClickPoint;
	
	private DifferenceplotRenderUtils renderUtil;
	
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
	
	/**
	 * An arraylist to hold if the differences in mean (median) between two samples are significant
	 * Currently only populated in remote rendering mode
	 */
	ArrayList<Boolean> significanceDiffFlagMean;
	
	/**
	 * An arraylist to hold if the differences in variance between two samples are significant
	 * Currently only populated in remote rendering mode
	 */
	ArrayList<Boolean> significanceDiffFlagVariance;
	
	private IPickingListener canvasPickingListener;

	public DifferenceplotElement(TablePerspective tablePerspective, DataSelectionConfiguration dataSelectionConfiguration) {
		this.tablePerspective = tablePerspective;
		this.selection = new TablePerspectiveSelectionMixin(tablePerspective, this);
		
		dataColumns = new ArrayList<>();
		significanceDiffFlagMean = null;
		significanceDiffFlagVariance = null;
		
		setVisibility(EVisibility.PICKABLE);
		
		this.dataSelectionConf = dataSelectionConfiguration;
		
		if (dataSelectionConfiguration != null)
		{
			renderRemote = true;
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
		if(!this.renderRemote)
		{
			this.prepareData(this.dataSelectionConf);
		}
		
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
				h = DifferencePlotVisInfo.MIN_HEIGHT_PIXELS;
				w = DifferencePlotVisInfo.MIN_WIDTH_PIXELS;
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
				h = DifferencePlotVisInfo.MIN_HEIGHT_PIXELS;
				w = DifferencePlotVisInfo.MIN_WIDTH_PIXELS;
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
		//System.out.println("Prepare Data called");
		dataColumns.clear();
		
		if (tablePerspective != null & dataSelectionConf != null) {
			if (tablePerspective.getDataDomain().getTable() instanceof NumericalTable) {
				//StatContainer statisticsContext = tablePerspective.getContainerStatistics().getHistogram();
				//statisticsFocus = 
				
				
				this.dataSelectionConf = dataSelectionConf;
				Table table = selection.getDataDomain().getTable();
				
				
				ArrayList<Float> stat1_UseAll = new ArrayList<Float>();
				ArrayList<Float> stat2_UseAll = new ArrayList<Float>();
				
				ArrayList<Float> stat1_UseSelected = new ArrayList<Float>();
				ArrayList<Float> stat2_UseSelected = new ArrayList<Float>();
				
				
				
				renderUtil = new DifferenceplotRenderUtils();
				
				/**
				 * Here depending on whether the view is rendered remotely, 
				 * the difference view data construction is computed differently.
				 */
				
				/** In the case of remote rendering, the difference view
				 *  shows the difference between the brick vs. the rest
				 */
				
				if (renderRemote)
				{
					// Use the defaultTablePerspective to compute statistics for the whole set (e.g., all the samples)
					TablePerspective defaultTablePerspective = tablePerspective.getParentTablePerspective();
					TablePerspective defaultTablePerspective2 = tablePerspective.getDataDomain().getDefaultTablePerspective();
					//tablePerspective.getParentTablePerspective()
					
					stat1_UseAll = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), defaultTablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					stat2_UseAll = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), defaultTablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
					// Use the tablePerspective to compute the statistics only for the brick (e.g., for the samples within the brick)
					// Pass the defaultTablePerspective as reference here, this is to ensure that gene IDs computed for all and the brick are exactly the same
					stat1_UseSelected = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, defaultTablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					stat2_UseSelected = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, defaultTablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
					
					int sampleSize1 = StatisticsUtils.computeSampleSize(dataSelectionConf.getVisSpaceType().ordinal(), defaultTablePerspective, false);
					int sampleSize2 = StatisticsUtils.computeSampleSize(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, false);
					
					significanceDiffFlagMean = StatisticsUtils.computeSignificanceOnTwoSampleTtest(false, stat1_UseAll, stat1_UseSelected, stat2_UseAll, stat2_UseSelected, sampleSize1, sampleSize2);
					significanceDiffFlagVariance = StatisticsUtils.computeSignificanceOnTwoSampleVarianceFTest(stat2_UseAll, stat2_UseSelected, sampleSize1, sampleSize2);
				}
				/**
				 * In the case of normal rendering. The statistics are computed for only 
				 * the selected records/dimensions. 
				 */
				else
				{
					ArrayList<Integer> selectedIDs = renderUtil.buildSelectedIDList(this);
					
					TablePerspective defaultTablePerspective = tablePerspective.getDataDomain().getDefaultTablePerspective();
					TablePerspective defaultTablePerspective2 = tablePerspective.getParentTablePerspective();
					
					stat1_UseAll = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					stat2_UseAll = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
					
					stat1_UseSelected = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), selectedIDs);
					stat2_UseSelected = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, null, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), selectedIDs);
					
					int sampleSize1 = StatisticsUtils.computeSampleSize(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, false);
					int sampleSize2 = selectedIDs.size(); //StatisticsUtils.computeSampleSize(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, false);
					
					significanceDiffFlagMean = StatisticsUtils.computeSignificanceOnTwoSampleTtest(false, stat1_UseAll, stat1_UseSelected, stat2_UseAll, stat2_UseSelected, sampleSize1, sampleSize2);
					significanceDiffFlagVariance = StatisticsUtils.computeSignificanceOnTwoSampleVarianceFTest(stat2_UseAll, stat2_UseSelected, sampleSize1, sampleSize2);
				}
			
				
				

				
				
				
				for (int i = 0; i < stat1_UseAll.size(); i++)
				{
					float diffVal = stat1_UseSelected.get(i) - stat1_UseAll.get(i);
					stat1_UseSelected.set(i, diffVal);
					
					diffVal = stat2_UseSelected.get(i) - stat2_UseAll.get(i);
					stat2_UseSelected.set(i, diffVal);
				}
				
				dataColumns.add(stat1_UseSelected);
				dataColumns.add(stat2_UseSelected);
				
				
				renderUtil.PerformDataLoadedOperations(this);
				
				// First time the dataset is loaded
				// also init listeners here
				if (!this.dataColumnsSet)
				{
					this.dataColumnsSet = true;
//					if(!renderRemote)
//					{
//						initListeners();
//					}
					initListeners();
				}
				else
				{
					
				}
				
				this.readyForRender = true;
				
				
			
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
		//System.out.println("!!!!! relased:  " + pick.getPickingMode());
		switch (pick.getPickingMode()) {
		case CLICKED:
			//System.out.println("!!!!! clicked:  " + pick.getPickedPoint());
			firstClickPoint = pick.getPickedPoint();
			
			
			rectanglePicked = renderUtil.pickedSelectionRectangle(firstClickPoint, selectionRect);
			break;
		case DRAGGED:
			//Enlarge the selection rectangle here
			
			//System.out.println("!!!!! dragged:  " + pick.getPickedPoint());
			if(firstClickPoint == null)
			{
				//System.out.println("!!!!! dragged:  " + this.toRelative(pick.getPickedPoint()));
				firstClickPoint = pick.getPickedPoint();
			}
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
			System.out.println("!!!!! relased:  " + pick.getPickedPoint());
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
				renderWidth = DifferencePlotVisInfo.MIN_WIDTH_PIXELS;
				renderHeight = DifferencePlotVisInfo.MIN_HEIGHT_PIXELS;
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

	public ArrayList<Boolean> getSignificanceDiffFlagMean() {
		return significanceDiffFlagMean;
	}

	public void setSignificanceDiffFlagMean(ArrayList<Boolean> significanceDiffFlagMean) {
		this.significanceDiffFlagMean = significanceDiffFlagMean;
	}

	public ArrayList<Boolean> getSignificanceDiffFlagVariance() {
		return significanceDiffFlagVariance;
	}

	public void setSignificanceDiffFlagVariance(
			ArrayList<Boolean> significanceDiffFlagVariance) {
		this.significanceDiffFlagVariance = significanceDiffFlagVariance;
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
