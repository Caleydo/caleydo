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
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
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
	
	private IPickingListener canvasPickingListener;

	public DifferenceplotElement(TablePerspective tablePerspective, DataSelectionConfiguration dataSelectionConfiguration) {
		this.tablePerspective = tablePerspective;
		this.selection = new TablePerspectiveSelectionMixin(tablePerspective, this);
		
		dataColumns = new ArrayList<>();
		
		setVisibility(EVisibility.PICKABLE);
		
		if (dataSelectionConfiguration != null)
		{
			renderRemote = true;
			this.prepareData(dataSelectionConfiguration);
			
		}
		
		
		
	}

	@Override
	public void onSelectionUpdate(SelectionManager manager) {
		this.prepareData(this.dataSelectionConf);
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
		
		g.pushResourceLocator(Activator.getResourceLocator());
		//super.renderImpl(g, w, h);
		
		
		renderUtil.render(g.gl, this, w, h);
		
		renderUtil.renderSelectionRectangle(g.gl, selectionRect, w, h);
		
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (!dataColumnsSet | !readyForRender| renderRemote)
			return;	
		
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
				
				
				ArrayList<Float> col1_v1 = new ArrayList<Float>();
				ArrayList<Float> col2_v1 = new ArrayList<Float>();
				
				ArrayList<Float> col1_v2 = new ArrayList<Float>();
				ArrayList<Float> col2_v2 = new ArrayList<Float>();
				
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
					TablePerspective defaultTablePerspective = tablePerspective.getDataDomain().getDefaultTablePerspective();
					
					col1_v1 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), defaultTablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					col2_v1 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), defaultTablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
					// Use the tablePerspective to compute the statistics only for the brick (e.g., for the samples within the brick)
					col1_v2 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					col2_v2 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
				}
				/**
				 * In the case of normal rendering. The statistics are computed for only 
				 * the selected records/dimensions. 
				 */
				else
				{
					ArrayList<Integer> selectedIDs = renderUtil.buildSelectedIDList(this);
					
					col1_v1 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), null);
					col2_v1 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), null);
					
					col1_v2 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(0)), selectedIDs);
					col2_v2 = StatisticsUtils.computeStatistics(dataSelectionConf.getVisSpaceType().ordinal(), tablePerspective, EStatisticsType.valueOf(dataSelectionConf.getAxisLabels().get(1)), selectedIDs);				
				}
			
				
				

				
				
				
				for (int i = 0; i < col1_v1.size(); i++)
				{
					float diffVal = col1_v2.get(i) - col1_v1.get(i);
					col1_v2.set(i, diffVal);
					
					diffVal = col2_v2.get(i) - col2_v1.get(i);
					col2_v2.set(i, diffVal);
				}
				
				dataColumns.add(col1_v2);
				dataColumns.add(col2_v2);
				
				
				renderUtil.PerformDataLoadedOperations(this);
				
				// First time the dataset is loaded
				// also init listeners here
				if (!this.dataColumnsSet)
				{
					this.dataColumnsSet = true;
					if(!renderRemote)
					{
						initListeners();
					}
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
		canvasPickingListener = new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				//onRecordPick(pick.getObjectID(), pick);
				handleMouseEvents(pick);
			}
		};
		
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
