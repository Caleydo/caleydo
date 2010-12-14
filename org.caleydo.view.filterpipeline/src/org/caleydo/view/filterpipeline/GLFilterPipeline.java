package org.caleydo.view.filterpipeline;

import gleem.linalg.Vec2f;
import java.awt.Font;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.MetaFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.ReEvaluateContentFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListEvent;
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
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.filterpipeline.listener.FilterUpdateListener;
import org.caleydo.view.filterpipeline.listener.ReEvaluateFilterListener;
import org.caleydo.view.filterpipeline.listener.SetFilterTypeListener;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;


/**
 * TODO
 * 
 * @author Thomas Geymayer
 */

public class GLFilterPipeline
	extends AGLView
	implements IViewCommandHandler, ISelectionUpdateHandler, IRadialMenuListener
{

	public final static String VIEW_ID = "org.caleydo.view.filterpipeline";

	private FilterPipelineRenderStyle renderStyle;
	private SelectionManager selectionManager;	
	private List<FilterItem> filterList = new LinkedList<FilterItem>();
	
	private FilterUpdateListener filterUpdateListener;
	private SetFilterTypeListener setFilterTypeListener;
	private ReEvaluateFilterListener reEvaluateFilterListener;
	
	private ASetBasedDataDomain dataDomain;
	private FilterType filterType = FilterType.CONTENT;
	private int numTotalElements;
	
	/**
	 * First filter to be displayed. All filters before are hidden and the
	 * height of the first filter shall fill the whole view.
	 */
	private int firstFilter = 0;
	
	/**
	 * The filtered items of this filter will be ignored, so that we can
	 * see what the filter pipeline would look like without this filter.
	 * 
	 * Set to -1 if no filter should be ignored.
	 */
	private int ignoredFilter = -1;
	
	/**
	 * The filter which should be showed in full size, which showing all
	 * filtered items, even those which don't arrive as input because they
	 * have been filtered before.
	 * 
	 * Set to -1 if no filter should be showed full sized.
	 */
	private int fullSizedFilter = -1;
	
	private boolean pipelineNeedsUpdate = true;
	private Vec2f mousePosition = new Vec2f();
	private RadialMenu filterMenu = null;

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
		
		filterMenu = new RadialMenu
		(
			this,
			textureManager.getIconTexture(gl, EIconTextures.FILTER_PIPELINE_MENU_ITEM)
		);
		filterMenu.addEntry( null );
		filterMenu.addEntry( null );
		filterMenu.addEntry( textureManager.getIconTexture(gl, EIconTextures.FILTER_PIPELINE_DELETE) );
		filterMenu.addEntry( textureManager.getIconTexture(gl, EIconTextures.FILTER_PIPELINE_EDIT) );
		
		if( textRenderer != null )
			textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 20), true);
		textRenderer.setColor(0, 0, 0, 1);
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
		//glMouseListener = getParentGLCanvas().getGLMouseListener();

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
		// ---------------------------------------------------------------------
		// move...
		// ---------------------------------------------------------------------
		
		if( pipelineNeedsUpdate )
			updateFilterPipeline();
		
		if( glMouseListener.wasMouseReleased() )
			filterMenu.handleMouseReleased();
		
		updateMousePosition(gl);
		
		filterMenu.handleDragging(mousePosition);
		
		// ---------------------------------------------------------------------
		// render...
		// ---------------------------------------------------------------------		
		
		displayBackground(gl);
		
		// filter
		if( !filterList.isEmpty() )
		{
			// ensure at least on filter is shown
			if( firstFilter >= filterList.size() )
				firstFilter = filterList.size() - 1;
			
			int numFilters = filterList.size() - firstFilter;
			
			float filterWidth  = (viewFrustum.getWidth() - 0.2f) / numFilters;
			float filterHeight = viewFrustum.getHeight() - 0.5f;
			float left = 0.1f;
			
			// display an arrow to show hidden filters
			if( firstFilter > 0 )
				displayCollapseArrow(gl, firstFilter - 1, left);

			// The usage of a Set ensures that multiple filtered items
			// just count once
			Set<Integer> filteredItems = new HashSet<Integer>();
				
			for (FilterItem filter : filterList)
			{
				int numItemsIn = numTotalElements - filteredItems.size();
				
				if( filter.getId() != ignoredFilter )
					filteredItems.addAll(filter.getFilteredItems());

				int numItemsOut = numTotalElements - filteredItems.size();
				int numItemsFiltered = numItemsIn - numItemsOut;

				if( filter.getId() < firstFilter )
					// skip hidden filters
					continue;
				
				if( filter.getId() == firstFilter )
					filterHeight *= (float)numTotalElements/numItemsIn;
				else
					displayCollapseArrow(gl, filter.getId(), left - 0.2f);
				
				if( filter.getId() == fullSizedFilter )
					numItemsIn = numItemsOut + filter.getFilteredItems().size(); 

				displayFilter
				(
					gl,
					filter.getId(),
					numItemsFiltered,
					filter.getFilteredItems().size(),
					numItemsOut,
					left,
					filterWidth,
					(float)numItemsIn/numTotalElements * filterHeight,
					(float)numItemsOut/numTotalElements * filterHeight
				);
				left += filterWidth;
			}
			
			filterMenu.render(gl);
		}
	}
	
	/**
	 * 
	 * @param gl
	 * @param id
	 * @param left
	 * @param width
	 * @param heightLeft
	 * @param heightRight
	 */
	private void displayFilter( GL gl,
								int id,
								int numFilteredNew,
								int numFilteredTotal,
								int numPassedNew,
			                    float left,
			                    float width,
			                    float heightLeft,
			                    float heightRight )
	{
		float bottom = 0.3f;
		int iPickingID =
			pickingManager.getPickingID
			(
				iUniqueID,
				EPickingType.FILTERPIPE_FILTER,
				id
			);
		
		// filter
		gl.glPushName(iPickingID);
		
		gl.glBegin(GL.GL_QUADS);
		{		
			if( id % 2 == 0 )
				gl.glColor3f(1.f,0.6f,0.5f);
			else
				gl.glColor3f(0.8f,0.6f,1.f);
	
			gl.glVertex3f(left, bottom, 0.001f);
			gl.glVertex3f(left, bottom + heightLeft, 0.001f);
	
			gl.glVertex3f(left + width, bottom + heightRight, 0.001f);
			gl.glVertex3f(left + width, bottom, 0.001f);
		}
		gl.glEnd();
		gl.glPopName();
		
		// label
		textRenderer.renderText
		(
			gl,
			"-"+numFilteredNew+" (-"+numFilteredTotal+")",
			left   + 0.05f,
			bottom + 0.05f,
			0.007f,
			0.007f,
			20
		);
		
		// currently not filtered elements
		textRenderer.renderText
		(
			gl,
			""+numPassedNew,
			left   + width - 0.4f,
			bottom + heightRight + 0.05f,
			0.007f,
			0.007f,
			20
		);
		
		// show input for first filter
		if( id == firstFilter )
			textRenderer.renderText
			(
				gl,
				""+(numPassedNew + numFilteredNew),
				left,
				bottom + heightLeft + 0.05f,
				0.007f,
				0.007f,
				20
			);
		
		
		if( selectionManager.getElements(SelectionType.MOUSE_OVER)
				            .contains(id) )
		{
			gl.glLineWidth(SelectionType.MOUSE_OVER.getLineWidth());
			
			gl.glBegin(GL.GL_LINE_LOOP);
			{
				gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
				
				gl.glVertex3f(left, bottom, 0.9f);
				gl.glVertex3f(left, bottom + heightLeft, 0.9f);
				gl.glVertex3f(left + width, bottom + heightRight, 0.9f);
				gl.glVertex3f(left + width, bottom, 0.9f);
			}
			gl.glEnd();
		}		
	}
	
	private void displayCollapseArrow(GL gl, int id, float left)
	{
		int iPickingID =
			pickingManager.getPickingID
			(
				iUniqueID,
				EPickingType.FILTERPIPE_START_ARROW,
				id
			);
		float bottom = 0.025f;
		float halfSize = 0.1f;

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		Texture arrowTexture =
			textureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_ARROW);
		arrowTexture.enable();
		arrowTexture.bind();
		TextureCoords texCoords = arrowTexture.getImageTexCoords();
		
		gl.glPushName(iPickingID);
		
		gl.glMatrixMode(GL.GL_MODELVIEW_MATRIX);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glTranslatef(left + halfSize, bottom + halfSize, 0.001f);
		gl.glRotatef(id <= firstFilter ? -90 : 90, 0, 0, 1);
		
		gl.glBegin(GL.GL_QUADS);
		{
			gl.glColor3f(0.9f,1f,0.9f);
	
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex2f(-halfSize, -halfSize);
			
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex2f(-halfSize, halfSize);
	
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex2f(halfSize, halfSize);
			
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex2f(halfSize, -halfSize);
		}
		gl.glEnd();
		
		gl.glPopMatrix();
		gl.glPopName();

		arrowTexture.disable();
		
		gl.glPopAttrib();
	}
	
	private void displayBackground(GL gl)
	{
		int iPickingID =
			pickingManager.getPickingID
			(
				iUniqueID,
				EPickingType.FILTERPIPE_BACKGROUND,
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

	@Override
	public String getShortInfo()
	{
		return "Filterpipeline "+filterType;
	}

	@Override
	public String getDetailedInfo()
	{
		return "Filterpipeline "+filterType;
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		switch(pickingMode)
		{
			case MOUSE_OVER:
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				
				if( pickingType == EPickingType.FILTERPIPE_FILTER )
				{
					selectionManager.addToType
					(
						SelectionType.MOUSE_OVER, iExternalID
					);
				}
				break;
			case CLICKED:
				switch(pickingType)
				{
					case FILTERPIPE_FILTER:
						filterMenu.show(iExternalID, mousePosition);
						break;
					case FILTERPIPE_START_ARROW:
						firstFilter = iExternalID;
						break;
				}
				break;
		}
	}
	
	@Override
	public void handleRadialMenuSelection(int externalId, int selection)
	{
		fullSizedFilter = -1;
		ignoredFilter = -1;

		if( externalId >= filterList.size() || externalId < 0 )
			return;
		
		FilterItem filter = filterList.get(externalId);
		
		switch(selection)
		{
			case 2: // left
				filter.triggerRemove();
				break;
			case 3: // down
				try
				{
					filter.showDetailsDialog();
				}
				catch (Exception e)
				{
					System.out.println("Failed to show details dialog: "+e);
				}
				break;
		}
	}
	
	@Override
	public void handleRadialMenuHover(int externalId, int selection)
	{
		ignoredFilter = -1;
		fullSizedFilter = -1;

		switch(selection)
		{
			case 0:
				fullSizedFilter = externalId;
				break;
			case 2: // remove
				ignoredFilter = externalId;
				break;
		}
	}
	
	private void updateMousePosition(GL gl)
	{
		try
		{
			float windowCoords[] =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates
				(
					gl,
					glMouseListener.getPickedPoint().x,
					glMouseListener.getPickedPoint().y
				);

			mousePosition.set(windowCoords[0], windowCoords[1]);
		}
		catch(Exception e)
		{
			//System.out.println("Failed to get mouse position: "+e);
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
		
		reEvaluateFilterListener = new ReEvaluateFilterListener();
		reEvaluateFilterListener.setHandler(this);
		eventPublisher.addListener(ReEvaluateContentFilterListEvent.class, reEvaluateFilterListener);
		eventPublisher.addListener(ReEvaluateStorageFilterListEvent.class, reEvaluateFilterListener);
		
		setFilterTypeListener = new SetFilterTypeListener();
		setFilterTypeListener.setHandler(this);
		eventPublisher.addListener(SetFilterTypeEvent.class, setFilterTypeListener);
	}

	@Override
	public void unregisterEventListeners()
	{
		if( filterUpdateListener != null )
		{
			eventPublisher.removeListener(filterUpdateListener);
			filterUpdateListener = null;
		}
		
		if( reEvaluateFilterListener != null )
		{
			eventPublisher.removeListener(reEvaluateFilterListener);
			reEvaluateFilterListener = null;
		}
		
		if( setFilterTypeListener != null )
		{
			eventPublisher.removeListener(setFilterTypeListener);
			setFilterTypeListener = null;
		}
	}

	public void updateFilterPipeline()
	{
		pipelineNeedsUpdate = false;

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
			Vector<Integer> filteredIds = new Vector<Integer>();
			
			
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
						filteredIds.add(deltaItem.getPrimaryID());
					else
						System.out.println(deltaItem);
				}
			}
			
			System.out.println(filter.getLabel()+" (filtered "+filteredIds.size()+" items)");
			
			filterList.add
			(
				new FilterItem
				(
					filterID++,
					filteredIds,
					filter
				)
			);
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
	
	public void handleReEvaluateFilter(FilterType type)
	{
		if( filterType == type )
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
