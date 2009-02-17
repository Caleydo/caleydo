package org.caleydo.core.view.opengl.canvas.panel;

import java.awt.Font;
import java.util.ArrayList;
import javax.media.opengl.GL;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Single OpenGL pathway view
 * 
 * @author Marc Streit
 */
public class GLSelectionPanel
	extends AGLEventListener
	implements IMediatorReceiver, IMediatorSender
{
	private GenericSelectionManager selectionManager;

	private TextRenderer textRenderer;

	private GeneralRenderStyle renderStyle;

	private ArrayList<Integer> iAlElements;

	/**
	 * Constructor.
	 */
	public GLSelectionPanel(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum, false);
		viewType = EManagedObjectType.GL_SELECTION_PANEL;

		selectionManager = new GenericSelectionManager.Builder(EIDType.DAVID).build();

		iAlElements = new ArrayList<Integer>();

		GeneralManager.get().getEventPublisher().addReceiver(
				EMediatorType.PROPAGATION_MEDIATOR, (IMediatorReceiver) this);
		// GeneralManager.get().getEventPublisher().addSender(
		// EMediatorType.BUCKET_INTERNAL_INCOMING_MEDIATOR, (IMediatorSender)
		// this);
		GeneralManager.get().getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorSender) this);
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorReceiver) this);
		// GeneralManager.get().getEventPublisher().addReceiver(
		// EMediatorType.BUCKET_INTERNAL_OUTGOING_MEDIATOR,
		// (IMediatorReceiver)this);

		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);

		renderStyle = new GeneralRenderStyle(viewFrustum);
	}

	@Override
	public void initLocal(final GL gl)
	{
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering remoteRenderingGLCanvas)
	{
		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

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
		pickingManager.handlePicking(iUniqueID, gl);

		if (bIsDisplayListDirtyLocal)
		{
			rebuildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}

		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
	}

	@Override
	public synchronized void displayRemote(final GL gl)
	{
		if (bIsDisplayListDirtyRemote)
		{
			rebuildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}

		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
	}

	@Override
	public synchronized void display(final GL gl)
	{
		checkForHits(gl);

		gl.glCallList(iGLDisplayListToCall);
	}

	// @Override
	// public synchronized void handleSelectionUpdate(IUniqueObject
	// eventTrigger,
	// ISelectionDelta selectionDelta, Collection<SelectionCommand>
	// colSelectionCommand,
	// EMediatorType eMediatorType)
	// {

	// generalManager.getLogger().log(Level.FINE,
	// "Update called by " + eventTrigger.getClass().getSimpleName());
	//
	// if (eMediatorType == EMediatorType.PROPAGATION_MEDIATOR)
	// {
	// selectionManager.resetSelectionManager();
	// selectionManager.setDelta(selectionDelta);
	//
	// iAlElements.clear();
	// for (SelectionDeltaItem item : selectionDelta)
	// {
	// iAlElements.add(item.getPrimaryID());
	// }
	// }
	// else if (eMediatorType == EMediatorType.SELECTION_MEDIATOR)
	// {
	// for (SelectionDeltaItem item : selectionDelta)
	// {
	// if (iAlElements.contains(item.getPrimaryID()))
	// {
	// if (item.getSelectionType() == ESelectionType.MOUSE_OVER)
	// {
	// selectionManager.clearSelection(ESelectionType.MOUSE_OVER);
	//
	// }
	// else if (item.getSelectionType() == ESelectionType.SELECTION)
	// {
	// selectionManager.clearSelection(ESelectionType.SELECTION);
	//
	// }
	// selectionManager.addToType(item.getSelectionType(),
	// item.getPrimaryID());
	// for (Integer iConnectionID : item.getConnectionID())
	// {
	// selectionManager.addConnectionID(iConnectionID, item.getPrimaryID());
	// }
	//
	// }
	// }
	// }
	// else
	// throw new IllegalStateException("Cannot handle updates of type " +
	// eMediatorType);
	//
	// setDisplayListDirty();
	//
	// }

	private void rebuildDisplayList(GL gl, int iGLDisplayList)
	{
		float fXOrigin = 0.05f;
		float fYOrigin = -0.2f + viewFrustum.getTop() - viewFrustum.getBottom();

		gl.glNewList(iGLDisplayListToCall, GL.GL_COMPILE);

		renderBackground(gl);

		// textRenderer.setSmoothing(true);

		String sOutput;
		for (Integer iDavidID : iAlElements)
		{
			if (selectionManager.checkStatus(ESelectionType.MOUSE_OVER, iDavidID))
			{
				renderSelectionHighlight(gl, fXOrigin, fYOrigin);
				textRenderer.setColor(1, 1, 1, 1);
			}
			else if (selectionManager.checkStatus(ESelectionType.SELECTION, iDavidID))
			{
				textRenderer.setColor(0, 1, 0, 1);
				renderSelectionHighlight(gl, fXOrigin, fYOrigin);
			}
			else if (selectionManager.checkStatus(ESelectionType.NORMAL, iDavidID))
			{
				textRenderer.setColor(0, 0, 0, 1);
			}
			else
				throw new IllegalStateException(
						"Cannot determine color for panel selection element " + iDavidID);

			sOutput = generalManager.getIDMappingManager().getID(
					EMappingType.DAVID_2_GENE_SYMBOL, iDavidID)
					+ " ("
					+ generalManager.getIDMappingManager().getID(
							EMappingType.DAVID_2_REFSEQ_MRNA, iDavidID) + ")";

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.SELECTION_PANEL_ITEM, iDavidID));
			gl.glTranslatef(fXOrigin, fYOrigin, 0);
			textRenderer.begin3DRendering();
			textRenderer.draw3D(sOutput.toString(), 0, 0, 0.1f, renderStyle
					.getHeadingFontScalingFactor());
			textRenderer.end3DRendering();
			gl.glPopName();
			gl.glTranslatef(-fXOrigin, -fYOrigin, 0);

			fYOrigin -= 0.12f;
		}

		gl.glEndList();
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		switch (ePickingType)
		{
			case SELECTION_PANEL_ITEM:
				ESelectionType selectionType;
				switch (pickingMode)
				{
					case DOUBLE_CLICKED:
					case CLICKED:
						selectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:
						selectionType = ESelectionType.MOUSE_OVER;
						break;
					default:
						pickingManager.flushHits(this.getID(),
								EPickingType.SELECTION_PANEL_ITEM);
						return;
				}
				selectionManager.clearSelection(selectionType);

				// if (selectionManager.checkStatus(selectionType, iExternalID))
				// {
				// selectionManager.clearSelection(ESelectionType.SELECTION);
				// selectionManager.addToType(ESelectionType.SELECTION,
				// iExternalID);
				// }
				// else
				// {
				selectionManager.addToType(selectionType, iExternalID);
				// }

				triggerEvent(EMediatorType.SELECTION_MEDIATOR,
						new DeltaEventContainer<ISelectionDelta>(selectionManager.getDelta()));

				setDisplayListDirty();
				break;
		}

		pickingManager.flushHits(this.getID(), EPickingType.SELECTION_PANEL_ITEM);
	}

	@Override
	public synchronized String getShortInfo()
	{
		return "no info for panel";
	}

	@Override
	public synchronized String getDetailedInfo()
	{
		return "no info for panel";
	}

	@Override
	public void broadcastElements(EVAOperation type)
	{
		// nothing to do here
	}

	private void renderBackground(final GL gl)
	{
		// Render panel background
		gl.glColor4f(0.85f, 0.85f, 0.85f, 1f);
		gl.glLineWidth(1);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), 0);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), 0);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), 0);
		gl.glEnd();

		gl.glColor4f(0.4f, 0.4f, 0.4f, 1);
		gl.glLineWidth(1);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), 0);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), 0);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), 0);
		gl.glEnd();
	}

	private void renderSelectionHighlight(final GL gl, float fXOrigin, float fYOrigin)
	{
		Texture tempTexture = iconTextureManager.getIconTexture(gl,
				EIconTextures.PANEL_SELECTION);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1);

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(-0.04f, fYOrigin - 0.04f, 0.05f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(-0.04f, fYOrigin + 0.11f, 0.05f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(-0.04f + 0.8f, fYOrigin + 0.11f, 0.05f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(-0.04f + 0.8f, fYOrigin - 0.04f, 0.05f);
		gl.glEnd();

		tempTexture.disable();
	}

	@Override
	public int getNumberOfSelections(ESelectionType eSelectionType)
	{
		return 0;
	}

	@Override
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer)
	{
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
	}

}