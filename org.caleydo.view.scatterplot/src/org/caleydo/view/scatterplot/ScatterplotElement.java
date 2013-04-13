/**
 * 
 */
package org.caleydo.view.scatterplot;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
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
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.scatterplot.dialogues.DataSelectionConfiguration;
import org.caleydo.view.scatterplot.dialogues.DataSelectionDialogue;
import org.caleydo.view.scatterplot.event.ShowDataSelectionDialogEvent;
import org.caleydo.view.scatterplot.utils.EDataGenerationType;
import org.caleydo.view.scatterplot.utils.EVisualizationSpaceType;
import org.caleydo.view.scatterplot.utils.ScatterplotRenderUtils;
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
	
	/**
	 * Flag to check whether data for the view is loaded
	 * Data is loaded after proper selections are made in the initial {@link DataSelectionDialogue}
	 */
	private boolean areDataColumnsSet = false;
	
	private ArrayList<ArrayList<Float>> dataColumns;

	public ScatterplotElement(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		this.selection = new TablePerspectiveSelectionMixin(tablePerspective, this);
		
		dataColumns = new ArrayList<>();
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
		g.pushResourceLocator(Activator.getResourceLocator());
		//super.renderImpl(g, w, h);
		if (!areDataColumnsSet)
			return;	
		
		ScatterplotRenderUtils.render(g.gl, this, w, h);
		
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderPickImpl(g, w, h);
		g.popResourceLocator();
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
				}
				
				dataColumns.add(col1);
				dataColumns.add(col2);
				this.areDataColumnsSet = true;
			}

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
