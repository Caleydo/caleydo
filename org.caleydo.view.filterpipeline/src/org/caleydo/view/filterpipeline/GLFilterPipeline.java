package org.caleydo.view.filterpipeline;

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.media.opengl.GL;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.ESelectionMode;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.filterpipeline.renderstyle.FilterPipelineRenderStyle;


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
	private List<Filter> filterList;

	private int totalGenes;

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
		if( filterList != null )
		{
			float left = 0.2f;
			
			for (Filter filter : filterList)
			{
				displayFilter(gl, filter, left, 0.8f);
				left += 1;
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
	
	private void displayFilter(GL gl, Filter filter, float left, float width)
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
		gl.glVertex3f(left + width, 1-(float)filter.getCountFilteredItems()/totalGenes, 0.001f);
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
			
			gl.glVertex3f(left, 0, 0);
			gl.glVertex3f(left, 1, 0);
			gl.glVertex3f(left + width, 1-(float)filter.getCountFilteredItems()/totalGenes, 0);
			gl.glVertex3f(left + width, 0, 0);
		
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
				filterList.get(iExternalID).showDetailsDialog();
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
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners()
	{
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners()
	{
		super.unregisterEventListeners();

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

	public void handleFilterUpdated(List<Filter> filterList, int total)
	{
		this.filterList = filterList;
		this.totalGenes = total;
	}
}
