package org.caleydo.core.view.opengl.canvas.cell;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.ESelectionMode;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.genome.IPathwayItemManager;
import org.caleydo.core.manager.specialized.genome.IPathwayManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.renderstyle.PathwayRenderStyle;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.hierarchy.EHierarchyLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 */
public class GLCell
	extends AGLEventListener
	implements IMediatorReceiver, IMediatorSender
{
	private ConnectedElementRepresentationManager connectedElementRepresentationManager;

	private GenericSelectionManager selectionManager;

	/**
	 * Constructor.
	 */
	public GLCell(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, false);
		viewType = EManagedObjectType.GL_CELL_LOCALIZATION;

		connectedElementRepresentationManager = generalManager.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();

		// initialize internal gene selection manager
		ArrayList<ESelectionType> alSelectionType = new ArrayList<ESelectionType>();
		for (ESelectionType selectionType : ESelectionType.values())
		{
			alSelectionType.add(selectionType);
		}

		selectionManager = new GenericSelectionManager.Builder(EIDType.PATHWAY_VERTEX).build();
	}

	@Override
	public void initLocal(final GL gl)
	{
		init(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLevel layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		init(gl);
	}

	@Override
	public void init(final GL gl)
	{
	}

	@Override
	public synchronized void displayLocal(final GL gl)
	{
		pickingManager.handlePicking(iUniqueID, gl, false);
		if (bIsDisplayListDirtyLocal)
		{
//			rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyLocal = false;
		}
		display(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public synchronized void displayRemote(final GL gl)
	{
		if (bIsDisplayListDirtyRemote)
		{
//			rebuildPathwayDisplayList(gl);
			bIsDisplayListDirtyRemote = false;
		}

		display(gl);
	}

	@Override
	public synchronized void display(final GL gl)
	{
		checkForHits(gl);
		renderScene(gl);
	}

	private void renderScene(final GL gl)
	{
		GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
	}
	
	@Override
	public synchronized void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{
		generalManager.getLogger().log(Level.FINE,
				"Update called by " + eventTrigger.getClass().getSimpleName());

//		if (selectionDelta.getIDType() != EIDType.DAVID)
//			return;	
//
//		for (SelectionItem item : selectionDelta)
//		{
//			if (item.getSelectionType() == ESelectionType.ADD
//					|| item.getSelectionType() == ESelectionType.REMOVE)
//			{
//				break;
//			}
//			else
//			{
//				selectionManager.clearSelections();
//				break;				
//			}
//		}	
//
//		resolveExternalSelectionDelta(selectionDelta);
//		
//		setDisplayListDirty();
	}


	private ISelectionDelta resolveExternalSelectionDelta(ISelectionDelta selectionDelta)
	{
		ISelectionDelta newSelectionDelta = new SelectionDelta(EIDType.PATHWAY_VERTEX,
				EIDType.DAVID);
		
		int iDavidID = 0;

		for (SelectionItem item : selectionDelta)
		{
			iDavidID = item.getSelectionID();
			System.out.println("Cell component: " 
					+GeneralManager.get().getIDMappingManager().getMapping(
							EMappingType.DAVID_2_CELL_COMPONENT));
		}
//
//			iPathwayVertexGraphItemID = generalManager.getPathwayItemManager()
//					.getPathwayVertexGraphItemIdByDavidId(iDavidID);
//
//			// Ignore David IDs that do not exist in any pathway
//			if (iPathwayVertexGraphItemID == -1)
//			{
//				continue;
//			}
//
//			// Convert DAVID ID to pathway graph item representation ID
//			for (IGraphItem tmpGraphItemRep : generalManager.getPathwayItemManager().getItem(
//					iPathwayVertexGraphItemID).getAllItemsByProp(
//					EGraphItemProperty.ALIAS_CHILD))
//			{
//				if (!pathwayManager.getItem(iPathwayID).containsItem(tmpGraphItemRep))
//					continue;
//				
//				newSelectionDelta.addSelection(tmpGraphItemRep.getId(), item
//						.getSelectionType(), iDavidID);
//			}
//		}
//
		return newSelectionDelta;
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		if (detailLevel == EDetailLevel.VERY_LOW)
		{
			pickingManager.flushHits(iUniqueID, ePickingType);
			return;
		}
		
		switch (ePickingType)
		{
	
		}
	}

	@Override
	public synchronized String getShortInfo()
	{
//		PathwayGraph pathway = (generalManager.getPathwayManager().getItem(iPathwayID));
//		
//		return pathway.getTitle() + " (" +pathway.getType().getName() + ")";

		return null;
	}
	
	@Override
	public synchronized String getDetailedInfo()
	{
//		StringBuffer sInfoText = new StringBuffer();
//		PathwayGraph pathway = (generalManager.getPathwayManager().getItem(iPathwayID));
//
//		sInfoText.append("<b>Pathway</b>\n\n<b>Name:</b> "+ pathway.getTitle()
//			+ "\n<b>Type:</b> "+pathway.getType().getName());
//
//		// generalManager.getSWTGUIManager().setExternalRCPStatusLineMessage(
//		// pathway.getType().getName() + " Pathway: " + sPathwayTitle);
//
//		return sInfoText.toString();

		return null;
	}

	@Override
	public synchronized void triggerUpdate(ISelectionDelta selectionDelta)
	{
		generalManager.getEventPublisher().handleUpdate(this, selectionDelta);
	}

	@Override
	public void broadcastElements(ESelectionType type)
	{
		// TODO Auto-generated method stub
		
	}
}