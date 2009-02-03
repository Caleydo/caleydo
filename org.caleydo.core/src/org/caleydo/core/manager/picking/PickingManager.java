package org.caleydo.core.manager.picking;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import com.sun.opengl.util.BufferUtil;

/**
 * <p>
 * Manages Picking IDs in a system-wide unique way and stores them locally Do
 * NOT store picking IDs
 * </p>
 * <p>
 * A picking ID is constructed from the type, which is the ordinal of values of
 * {@link EPickingType}. This value may be only up to two digits long thereby
 * limiting the number of possible picking types to 100. The rest Syntax for
 * Picking IDs 2* last digits: type rest: counter C*TT
 * </p>
 * <p>
 * The picking IDs are stored associated with a view
 * </p>
 * <p>
 * The {@link #handlePicking(int, GL, boolean)} method has to be called in every
 * render step and calculates the ray-tracing
 * </p>
 * 
 * @author Alexander Lex
 */
public class PickingManager
{

	private HashMap<Integer, HashMap<Integer, Integer>> hashSignatureToPickingIDHashMap;

	private HashMap<Integer, HashMap<Integer, Integer>> hashSignatureToExternalIDHashMap;

	private int iIDCounter = 0;

	private HashMap<Integer, ArrayList<Pick>> hashSignatureToHitList;

	private HashMap<Integer, Long> hashViewIDToLastMouseMovedTimeStamp;

	private HashMap<Integer, Boolean> hashViewIDToIsMouseOverPickingEvent;

	private boolean bEnablePicking = true;

	/**
	 * Constructor
	 */
	public PickingManager()
	{
		hashSignatureToHitList = new HashMap<Integer, ArrayList<Pick>>();
		hashSignatureToPickingIDHashMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		hashSignatureToExternalIDHashMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		hashViewIDToLastMouseMovedTimeStamp = new HashMap<Integer, Long>();
		hashViewIDToIsMouseOverPickingEvent = new HashMap<Integer, Boolean>();
	}

	/**
	 * Returns a unique picking ID based on the ViewID and a special type and
	 * stores it in a hash map with the external id DO NOT store picking id's
	 * locally
	 * 
	 * @param iViewID the ID of the calling view
	 * @param ePickingType the type determining what was picked
	 * @param iExternalID an arbitrary integer
	 * @return the picking id, use {@link #getExternalIDFromPickingID(int, int)}
	 *         to retrieve the corresponding external id
	 */
	public int getPickingID(int iViewID, EPickingType ePickingType, int iExternalID)
	{

		int iType = ePickingType.ordinal();
		checkType(iType);

		checkViewID(iViewID);

		int iSignature = getSignature(iViewID, iType);

		if (hashSignatureToPickingIDHashMap.get(iSignature) == null)
		{
			hashSignatureToPickingIDHashMap.put(iSignature, new HashMap<Integer, Integer>());
			hashSignatureToExternalIDHashMap.put(iSignature, new HashMap<Integer, Integer>());

		}
		else if (hashSignatureToExternalIDHashMap.get(iSignature).get(iExternalID) != null)
		{
			return hashSignatureToExternalIDHashMap.get(iSignature).get(iExternalID);
		}

		int iPickingID = calculateID(iType);
		hashSignatureToPickingIDHashMap.get(iSignature).put(iPickingID, iExternalID);
		hashSignatureToExternalIDHashMap.get(iSignature).put(iExternalID, iPickingID);

		return iPickingID;
	}

	/**
	 * This method has to be called in every display step. It is responsible for
	 * the picking. It needs the ID of the calling view and a gl context.
	 * 
	 * @param iViewID the id of the calling view
	 * @param gl the GL context
	 * @param bIsMaster TODO remove after some testing - not needed at the
	 *            moment. remove deprecated when done
	 */
	@Deprecated
	public void handlePicking(final int iViewID, final GL gl, final boolean bIsMaster)
	{

		if (bEnablePicking == false)
			return;

		AGLEventListener canvasUser = (GeneralManager.get().getViewGLCanvasManager()
				.getGLEventListener(iViewID));
		PickingJoglMouseListener pickingTriggerMouseAdapter = canvasUser.getParentGLCanvas()
				.getJoglMouseListener();

		Point pickPoint = null;

		// //FIXME: hack to conserve the mouse state - Discuss
		// boolean bMouseReleased =
		// pickingTriggerMouseAdapter.wasMouseReleased();

		EPickingMode ePickingMode = EPickingMode.CLICKED;

		if (pickingTriggerMouseAdapter.wasMouseDoubleClicked())
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			ePickingMode = EPickingMode.DOUBLE_CLICKED;
		}
		else if (pickingTriggerMouseAdapter.wasLeftMouseButtonPressed())
		// || bMouseReleased)
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			// bIsMouseOverPickingEvent = false;
			ePickingMode = EPickingMode.CLICKED;
		}
		else if (pickingTriggerMouseAdapter.wasMouseDragged())
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			ePickingMode = EPickingMode.DRAGGED;
		}
		else if (pickingTriggerMouseAdapter.wasMouseMoved())
		{
			// Restart timer
			hashViewIDToLastMouseMovedTimeStamp.put(iViewID, System.nanoTime());
			hashViewIDToIsMouseOverPickingEvent.put(iViewID, true);

		}
		else if (hashViewIDToIsMouseOverPickingEvent.get(iViewID) != null
				&& hashViewIDToLastMouseMovedTimeStamp.get(iViewID) != null
				&& hashViewIDToIsMouseOverPickingEvent.get(iViewID) == true
				&& System.nanoTime() - hashViewIDToLastMouseMovedTimeStamp.get(iViewID) >= 0)// 1e9
		// )
		{
			pickPoint = pickingTriggerMouseAdapter.getPickedPoint();
			hashViewIDToLastMouseMovedTimeStamp.put(iViewID, System.nanoTime());
			ePickingMode = EPickingMode.MOUSE_OVER;
		}

		if (pickPoint == null)
		{
			return;
		}

		hashViewIDToIsMouseOverPickingEvent.put(iViewID, false);

		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = BufferUtil.newIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		// gl.glPushName(99);

		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix(pickPoint.x, (viewport[3] - pickPoint.y),// 
				5.0, 5.0, viewport, 0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float fAspectRatio = (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]);

		IViewFrustum viewFrustum = canvasUser.getViewFrustum();

		viewFrustum.setProjectionMatrix(gl, fAspectRatio);

		gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		canvasUser.display(gl);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		// System.out.println("Picking Buffer: " + iArPickingBuffer[0]);
		// processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint,
		// ePickingMode);
		ArrayList<Integer> iAlPickedObjectId = processHits(iHitCount, iArPickingBuffer);

		if (iAlPickedObjectId.size() > 0)
		{
			processPicks(iAlPickedObjectId, iViewID, ePickingMode, bIsMaster, tmpPickPoint,
					pickingTriggerMouseAdapter.getPickedPointDragStart());
		}
	}

	/**
	 * Returns the hit for a particular view and type
	 * 
	 * @param iViewID
	 * @param iType
	 * @return null if no Hits, else the ArrayList<Integer> with the hits
	 */
	public ArrayList<Pick> getHits(int iViewID, EPickingType ePickingType)
	{

		int iType = ePickingType.ordinal();
		checkType(iType);
		checkViewID(iViewID);

		int iSignature = getSignature(iViewID, iType);

		if (hashSignatureToHitList.get(iSignature) == null)
			return null;
		else
			return hashSignatureToHitList.get(iSignature);
	}

	/**
	 * Returns the external ID (the id with which you initialized
	 * getPickingID()) when you provide the picking ID
	 * 
	 * @param uniqueManagedObject
	 * @param iPickingID the picking ID
	 * @return the ID, null if no entry for that pickingID
	 */
	public int getExternalIDFromPickingID(int iViewID, int iPickingID)
	{
		int iSignature = getSignatureFromPickingID(iPickingID, iViewID);
		HashMap<Integer, Integer> hashMap = hashSignatureToPickingIDHashMap.get(iSignature);
		if (hashMap == null)
			return -1;

		return hashMap.get(iPickingID);
	}

	/**
	 * Returns the external ID (the id with which you initialized
	 * getPickingID()) when you provide the hit count, meaning the n-th element
	 * in the hit list.
	 * 
	 * @param uniqueManagedObject
	 * @param iType the type, >= 0, <100
	 * @param iHitCount
	 * @return the ID, null if no entry for that hit count
	 */
	public int getExternalIDFromHitCount(int iViewID, int iType, int iHitCount)
	{

		// TODO: exceptions
		int iSignature = getSignature(iViewID, iType);
		int iPickingID = hashSignatureToHitList.get(iSignature).get(iHitCount).getPickingID();
		return hashSignatureToPickingIDHashMap.get(iSignature).get(iPickingID);

	}

	/**
	 * Removes the picking IDs form internal storage and from the hit list You
	 * should do that when you close a view, remember to do it for all types
	 * 
	 * @param iViewID the id of the calling view
	 * @param iType
	 */
	public void flushPickingIDs(int iViewID, int iType)
	{
		int iSignature = getSignature(iViewID, iType);
		hashSignatureToExternalIDHashMap.remove(iSignature);
		hashSignatureToHitList.remove(iSignature);
		hashSignatureToPickingIDHashMap.remove(iSignature);
	}

	/**
	 * Flush a particular hit list
	 * 
	 * @param iViewID the id of the calling view
	 * @param ePickingType the picking type determining which hits should be
	 *            flushed
	 */
	public void flushHits(int iViewID, EPickingType ePickingType)
	{

		int iType = ePickingType.ordinal();
		checkType(iType);
		checkViewID(iViewID);

		if (hashSignatureToHitList.get(getSignature(iViewID, iType)) != null)
		{
			hashSignatureToHitList.get(getSignature(iViewID, iType)).clear();
		}
	}

	/**
	 * Calculates the picking id, based on a type
	 * 
	 * @param iType the type
	 * @return a unique ID
	 */
	private int calculateID(int iType)
	{
		iIDCounter++;
		return (iIDCounter * 100 + iType);
	}

	/**
	 * Extracts the nearest hit from the provided iArPickingBuffer Stores it
	 * internally Can process only one hit at at time at the moment
	 * 
	 * @param iHitCount
	 * @param iArPickingBuffer
	 */
	private ArrayList<Integer> processHits(int iHitCount, int[] iArPickingBuffer)
	{

		int iPickingBufferCounter = 0;

		ArrayList<Integer> iAlPickedObjectId = new ArrayList<Integer>(4);

		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		int iNumberOfNames = 0;
		int iNearestObjectIndex = 0;
		for (int iCount = 0; iCount < iHitCount; iCount++)
		{
			// if first object is no hit skip z values
			if (iArPickingBuffer[iPickingBufferCounter] == 0)
			{
				iPickingBufferCounter += 3;
				continue;
			}
			// iPickingBufferCounter++;
			// Check if object is nearer than previous objects
			if (iArPickingBuffer[iPickingBufferCounter + 1] < iMinimumZValue)
			{
				// first element is number of names on name stack
				// second element is min Z Value
				iMinimumZValue = iArPickingBuffer[iPickingBufferCounter + 1];
				iNearestObjectIndex = iPickingBufferCounter;
				// third element is max Z Value
				// fourth element is name of lowest name on stack
				// iAlPickedObjectId.add(iArPickingBuffer[iPickingBufferCounter+3
				// ]);
			}
			iPickingBufferCounter = iPickingBufferCounter + 3
					+ iArPickingBuffer[iPickingBufferCounter];

		}

		iNumberOfNames = iArPickingBuffer[iNearestObjectIndex];

		for (int iNameCount = 0; iNameCount < iNumberOfNames; iNameCount++)
		{
			iAlPickedObjectId.add(iArPickingBuffer[iNearestObjectIndex + 3 + iNameCount]);
		}

		return iAlPickedObjectId;
		// iPickingBufferCounter += iNumberOfNames;

		// return iPickedObjectId;
	}

	private void processPicks(ArrayList<Integer> alPickingIDs, int iViewID,
			EPickingMode myMode, boolean bIsMaster, Point pickedPoint, Point dragStartPoint)
	{

		// we here have two cases: a view is rendered remote, than
		// eType.canContainOtherPicks() has to be true, or a view is rendered
		// locally, than (bIsMaster && iResultCounter == 0) has to be true.
		// Therefore, the first round in the loop always creates an iSignature
		// and an iMasterSignature
		// the iMasterSignature is needed to keep the signature of the first
		// level element (eg a view) in mind while other picks are processed

		int iPickingID = 0;
		int iSignature = 0;
		int iMasterSignature = 0;
		int iOrigianlPickingID = 0;
		for (int iResultCounter = 0; iResultCounter < alPickingIDs.size(); iResultCounter++)
		{
			iPickingID = alPickingIDs.get(iResultCounter);

			int iType = getTypeFromPickingID(iPickingID);

			EPickingType eType = EPickingType.values()[iType];
			if (eType.canContainOtherPicks())
			{
				iSignature = getSignatureFromPickingID(iPickingID, iViewID);
				iMasterSignature = iSignature;

				iOrigianlPickingID = iPickingID;
			}
			else
			{
				// if (bIsMaster && iResultCounter == 0)
				if (iResultCounter == 0)
				{
					iSignature = getSignatureFromPickingID(iPickingID, iViewID);

					iOrigianlPickingID = iPickingID;
					iMasterSignature = iSignature;
				}
				else
				{
					// int iValue = getSignatureFromPickingID(iPickingID,
					// iViewID);
					HashMap<Integer, Integer> signatureToPickingID = hashSignatureToPickingIDHashMap
							.get(iMasterSignature);

					if (signatureToPickingID == null)
						continue;

					Integer iViewUnderInteractionID = signatureToPickingID
							.get(iOrigianlPickingID);
					if (iViewUnderInteractionID == null)
						continue;
					iSignature = getSignatureFromPickingID(iPickingID, iViewUnderInteractionID);
				}
			}

			if (hashSignatureToHitList.get(iSignature) == null)
			{
				ArrayList<Pick> tempList = new ArrayList<Pick>();
				tempList.add(new Pick(iPickingID, myMode, pickedPoint, dragStartPoint));
				hashSignatureToHitList.put(iSignature, tempList);

			}
			else
			{
				hashSignatureToHitList.get(iSignature).clear();
				hashSignatureToHitList.get(iSignature).add(
						new Pick(iPickingID, myMode, pickedPoint, dragStartPoint));
			}
		}
	}

	/**
	 * A signature is a combination of the EPickingType ant the view id
	 * 
	 * @param iViewID
	 * @param iType
	 * @return
	 */
	private int getSignature(int iViewID, int iType)
	{

		return (iViewID * 100 + iType);
	}

	private int getSignatureFromPickingID(int iPickingID, int iViewID)
	{

		int iType = getTypeFromPickingID(iPickingID);

		return (getSignature(iViewID, iType));
	}

	private int getTypeFromPickingID(int iPickingID)
	{

		int iTemp = iPickingID / 100;
		return (iPickingID - iTemp * 100);
	}

	private void checkViewID(int iViewID)
	{

		if (iViewID > 9999999 || iViewID < 100)
		{
			throw new IllegalArgumentException(
					"PickingManager: The view id has to be in a range between "
							+ "9,999,999 and 100, but was: " + iViewID);
		}
	}

	private void checkType(int iType)
	{

		if (iType > 99 || iType < 0)
		{
			throw new IllegalArgumentException(
					"PickingManager: Type has to be larger then or exactly 0 and less than 100");
		}
	}

	/**
	 * Turn on/off picking
	 * 
	 * @param bEnablePicking
	 */
	public void enablePicking(final boolean bEnablePicking)
	{
		this.bEnablePicking = bEnablePicking;
	}

}
