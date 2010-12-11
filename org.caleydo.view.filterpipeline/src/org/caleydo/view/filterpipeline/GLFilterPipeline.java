package org.caleydo.view.filterpipeline;

import java.awt.Font;
import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.MetaFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.view.filterpipeline.SetFilterTypeEvent;
import org.caleydo.core.manager.event.view.filterpipeline.SetFilterTypeEvent.FilterType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.listener.FilterUpdateListener;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeListener;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * TODO
 * 
 * @author Thomas Geymayer
 */

public class GLFilterPipeline
	extends AGLView
	implements IViewCommandHandler, ISelectionUpdateHandler
{

	public final static String VIEW_ID = "org.caleydo.view.filterpipeline";

	private FilterPipelineRenderStyle renderStyle;
	private SelectionManager selectionManager;	
	private List<FilterItem> filterList = new LinkedList<FilterItem>();
	
	private FilterUpdateListener filterUpdateListener;
	private SetFilterTypeListener setFilterTypeListener;
	
	private ASetBasedDataDomain dataDomain;
	private FilterType filterType = FilterType.CONTENT;
	private int numTotalElements;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLFilterPipeline(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum)
	{
		super(glCanvas, viewFrustum, true);

		viewType = GLFilterPipeline.VIEW_ID;
		dataDomain =
			(ASetBasedDataDomain) DataDomainManager.get().getDataDomain
			(
				"org.caleydo.datadomain.genetic"
			);
	}

	@Override
	public void init(GL gl)
	{
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new FilterPipelineRenderStyle(viewFrustum);
		selectionManager =
			new SelectionManager
			(
				IDType.registerType
				(
					"filter_" + hashCode(),
					IDCategory.registerCategory("filter"),
					EStorageType.INT
				)
			);

		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;
	}

	@Override
	public void initLocal(GL gl)
	{
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager)
	{

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void displayLocal(GL gl)
	{
		pickingManager.handlePicking(this, gl);
		glMouseListener = getParentGLCanvas().getGLMouseListener();

		display(gl);

		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl)
	{

		display(gl);
	}

	@Override
	public void display(GL gl)
	{
		displayBackground(gl);
		
		// filter
		if( !filterList.isEmpty() )
		{
			float filterWidth  = viewFrustum.getWidth() / filterList.size();
			float left = 0;
				
			for (FilterItem filter : filterList)
			{
				displayFilter(gl, filter, left, filterWidth);
				left += filterWidth;
			}
		}
	}
	
	private void displayBackground(GL gl)
	{
		int iPickingID =
			pickingManager.getPickingID
			(
				iUniqueID,
				EPickingType.FILTERPIPE_FILTER,
				100
			);
		
		float height = viewFrustum.getHeight(),
		      width  = viewFrustum.getWidth();
	
		gl.glPushName(iPickingID);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glColor3d(0.9, 0.8, 0.7);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, height, 0);
		gl.glVertex3f(width, height, 0);
		gl.glVertex3f(width, 0, 0);
		
		gl.glEnd();
		
		gl.glPopName();		
	}
	
	private void displayFilter(GL gl, FilterItem filter, float left, float width)
	{
		textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24), true);
		textRenderer.setColor(0, 0, 0, 1);
		
		int iPickingID =
			pickingManager.getPickingID
			(
				iUniqueID,
				EPickingType.FILTERPIPE_FILTER,
				filter.getId()
			);
		
		// filter
		gl.glPushName(iPickingID);
		
		gl.glBegin(GL.GL_QUADS);
		
		if( filter.getId() % 2 == 0 )
			gl.glColor3f(1.f,0.6f,0.5f);
		else
			gl.glColor3f(0.8f,0.6f,1.f);

		gl.glVertex3f(left, 0, 0.001f);
		gl.glVertex3f(left, 1, 0.001f);

		gl.glVertex3f(left + width, 1-(float)filter.getCountFilteredItems()/numTotalElements , 0.001f);
		gl.glVertex3f(left + width, 0, 0.001f);
	
		gl.glEnd();
		gl.glPopName();
		
		// label
		textRenderer.renderText(gl,"-"+filter.getCountFilteredItems(),left+0.05f,0.05f,0.007f,0.007f,40);
		
		
		if( selectionManager.getElements(SelectionType.MOUSE_OVER)
				            .contains(filter.getId()) )
		{
			//textRenderer.renderText(gl,"Test",left+0.01f,0.1f,0.007f,0.008f,40);
			
			gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());
			
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
			
			gl.glVertex3f(left, 0, 0.9f);
			gl.glVertex3f(left, 1, 0.9f);
			gl.glVertex3f(left + width, 1-(float)filter.getCountFilteredItems()/numTotalElements, 0.9f);
			gl.glVertex3f(left + width, 0, 0.9f);
		
			gl.glEnd();
		}		
	}

	@Override
	public String getShortInfo()
	{
		return "Template Caleydo View";
	}

	@Override
	public String getDetailedInfo()
	{
		return "Template Caleydo View";
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		switch(pickingMode)
		{
			case MOUSE_OVER:
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				
				if(pickingType == EPickingType.FILTERPIPE_BACKGROUND)
					return;
				
				selectionManager.addToType(SelectionType.MOUSE_OVER, iExternalID);
				break;
			case CLICKED:
				try
				{
					filterList.get(iExternalID).showDetailsDialog();
				}
				catch (Exception e)
				{
					System.out.println("Failed to show details diaolg: "+e);
				}
				break;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation()
	{
		SerializedFilterPipelineView serializedForm = new SerializedFilterPipelineView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString()
	{
		return getClass().getCanonicalName();
	}

	@Override
	public void registerEventListeners()
	{
		filterUpdateListener = new FilterUpdateListener();
		filterUpdateListener.setHandler(this);
		eventPublisher.addListener(FilterUpdatedEvent.class, filterUpdateListener);
		
		setFilterTypeListener = new SetFilterTypeListener();
		setFilterTypeListener.setHandler(this);
		eventPublisher.addListener(SetFilterTypeEvent.class, setFilterTypeListener);
	}

	@Override
	public void unregisterEventListeners()
	{
		if (filterUpdateListener != null)
		{
			eventPublisher.removeListener(filterUpdateListener);
			filterUpdateListener = null;
		}
		
		if( setFilterTypeListener != null )
		{
			eventPublisher.removeListener(setFilterTypeListener);
			setFilterTypeListener = null;
		}
	}

	public void updateFilterPipeline()
	{
		Logger.log
		(
			new Status(IStatus.INFO, this.toString(),
			"Filterupdate: filterType="+filterType)
		);
		
		filterList.clear();
		int filterID = 0;
		
		for( Filter<?> filter :
				 filterType == FilterType.CONTENT
				   ? dataDomain.getContentFilterManager().getFilterPipe()
				   : dataDomain.getStorageFilterManager().getFilterPipe()
		   )
		{
			int num_removed_items = 0;
			
			if (filter instanceof MetaFilter<?>)
			{
				System.out.println("MetaFilter");

//				for (StorageFilter subFilter : ((StorageMetaFilter) filter).getFilterList())
//				{
//					// TODO
//				}
			}
			else
			{
				for (VADeltaItem deltaItem : filter.getVADelta().getAllItems())
				{
					if( deltaItem.getType() == EVAOperation.REMOVE_ELEMENT )
					{
						++num_removed_items;
					}
					else
						System.out.println(deltaItem);
					
					//deltaItem.getIndex()
				}
			}
			
			System.out.println(filter.getLabel()+" (filtered "+num_removed_items+" items)");
			
			filterList.add(new FilterItem(filterID++, filter.getLabel(), num_removed_items, filter.getFilterRep()));
		}

		numTotalElements =
			filterType == FilterType.CONTENT ? dataDomain.getSet().depth()
					                         : dataDomain.getSet().size();
	}

	public void handleSetFilterTypeEvent(FilterType type)
	{
		if( filterType == type )
			return;
		
		filterType = type;
		updateFilterPipeline();
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRedrawView()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastElements(EVAOperation type)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
