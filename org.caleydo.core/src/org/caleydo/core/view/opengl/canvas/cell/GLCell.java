package org.caleydo.core.view.opengl.canvas.cell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

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
	public void initRemote(GL gl, int remoteViewID,
			PickingJoglMouseListener pickingTriggerMouseAdapter,
			IGLCanvasRemoteRendering remoteRenderingGLCanvas)
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
			// rebuildPathwayDisplayList(gl);
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
			// rebuildPathwayDisplayList(gl);
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

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.CELL_MODEL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), -0.025f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop() - viewFrustum.getBottom(),
				-0.025f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getTop()
				- viewFrustum.getBottom(), -0.025f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(viewFrustum.getRight() - viewFrustum.getLeft(), viewFrustum.getBottom(),
				-0.025f);
		gl.glEnd();

		tempTexture.disable();

	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType mediatorType)
	{
		generalManager.getLogger().log(Level.FINE,
				"Update called by " + eventTrigger.getClass().getSimpleName());

		if (selectionDelta.getIDType() != EIDType.DAVID)
			return;

		// TODO review this, seems not to do anything??? ADD and REMOVE are no
		// longer supported
		// for (SelectionDeltaItem item : selectionDelta)
		// {
		// if (item.getSelectionType() == ESelectionType.ADD
		// || item.getSelectionType() == ESelectionType.REMOVE)
		// {
		// break;
		// }
		// else
		// {
		// selectionManager.clearSelections();
		// break;
		// }
		// }
		//
		// resolveExternalSelectionDelta(selectionDelta);
		//
		// setDisplayListDirty();
	}

	@Override
	public void handleVAUpdate(IUniqueObject eventTrigger, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand, EMediatorType mediatorType)
	{
		// TODO Auto-generated method stub

	}

	private ISelectionDelta resolveExternalSelectionDelta(ISelectionDelta selectionDelta)
	{
		ISelectionDelta newSelectionDelta = new SelectionDelta(EIDType.PATHWAY_VERTEX,
				EIDType.DAVID);

		int iDavidID = 0;

		for (SelectionDeltaItem item : selectionDelta)
		{
			if (item.getSelectionType() != ESelectionType.MOUSE_OVER
					&& item.getSelectionType() != ESelectionType.SELECTION)
			{
				continue;
			}

			iDavidID = item.getPrimaryID();

			System.out.println("Cell component: "
					+ GeneralManager.get().getIDMappingManager().getID(
							EMappingType.DAVID_2_CELL_COMPONENT, iDavidID));
		}
		//
		// iPathwayVertexGraphItemID = generalManager.getPathwayItemManager()
		// .getPathwayVertexGraphItemIdByDavidId(iDavidID);
		//
		// // Ignore David IDs that do not exist in any pathway
		// if (iPathwayVertexGraphItemID == -1)
		// {
		// continue;
		// }
		//
		// // Convert DAVID ID to pathway graph item representation ID
		// for (IGraphItem tmpGraphItemRep :
		// generalManager.getPathwayItemManager().getItem(
		// iPathwayVertexGraphItemID).getAllItemsByProp(
		// EGraphItemProperty.ALIAS_CHILD))
		// {
		// if
		// (!pathwayManager.getItem(iPathwayID).containsItem(tmpGraphItemRep))
		// continue;
		//				
		// newSelectionDelta.addSelection(tmpGraphItemRep.getId(), item
		// .getSelectionType(), iDavidID);
		// }
		// }
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
		// PathwayGraph pathway =
		// (generalManager.getPathwayManager().getItem(iPathwayID));
		//		
		// return pathway.getTitle() + " (" +pathway.getType().getName() + ")";

		return null;
	}

	@Override
	public synchronized String getDetailedInfo()
	{
		// StringBuffer sInfoText = new StringBuffer();
		// PathwayGraph pathway =
		// (generalManager.getPathwayManager().getItem(iPathwayID));
		//
		// sInfoText.append("<b>Pathway</b>\n\n<b>Name:</b> "+
		// pathway.getTitle()
		// + "\n<b>Type:</b> "+pathway.getType().getName());
		//
		// // generalManager.getSWTGUIManager().setExternalRCPStatusLineMessage(
		// // pathway.getType().getName() + " Pathway: " + sPathwayTitle);
		//
		// return sInfoText.toString();

		return null;
	}

	@Override
	public void triggerSelectionUpdate(EMediatorType mediatorType,
			ISelectionDelta selectionDelta, Collection<SelectionCommand> colSelectionCommand)
	{
		generalManager.getEventPublisher().triggerSelectionUpdate(
				EMediatorType.SELECTION_MEDIATOR, this, selectionDelta, null);
	}

	@Override
	public void broadcastElements(EVAOperation type)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(ESelectionType selectionType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void triggerVAUpdate(EMediatorType mediatorType, IVirtualArrayDelta delta,
			Collection<SelectionCommand> colSelectionCommand)
	{
		// TODO Auto-generated method stub

	}

}