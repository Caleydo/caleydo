package org.caleydo.core.manager.view;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.camera.ViewFrustumBase.ProjectionMode;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.view.opengl.canvas.AGLCanvasUser;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import com.sun.opengl.util.BufferUtil;

/**
 * Manages Picking IDs in a system-wide unique way and stores them locally Do
 * NOT store picking IDs in classes that use this class Syntax for Picking IDs 2
 * last digits: type rest: counter C*TT
 * 
 * @author Alexander Lex
 */
public class PickingManager
	extends AManager
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
	 * 
	 * @param setGeneralManager
	 */
	public PickingManager(IGeneralManager generalManager)
	{

		super(generalManager, IGeneralManager.iUniqueID_TypeOffset_PickingID,
				EManagerType.PICKING_MANAGER);

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
	 * @param iViewID the ID of the calling view, has to have 5 digits max
	 * @param iType a type which is part of the picking ID, has to be between 0
	 *            and 99
	 * @return
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

		int iPickingID = calculateID(iViewID, iType);
		hashSignatureToPickingIDHashMap.get(iSignature).put(iPickingID, iExternalID);
		hashSignatureToExternalIDHashMap.get(iSignature).put(iExternalID, iPickingID);

		return iPickingID;
	}

	/**
	 * TODO: Documentation
	 * 
	 * @param uniqueManagedObject
	 * @param gl
	 * @param pickingTriggerMouseAdapter
	 * @param bIsMaster
	 */
	public void handlePicking(final int iViewID, final GL gl, final boolean bIsMaster)
	{

		if (bEnablePicking == false)
			return;

		AGLCanvasUser canvasUser = (AGLCanvasUser) (generalManager.getViewGLCanvasManager().getEventListener(iViewID));
		PickingJoglMouseListener pickingTriggerMouseAdapter = canvasUser.getParentGLCanvas()
				.getJoglMouseListener();

		Point pickPoint = null;

		// //FIXME: hack to conserve the mouse state - Discuss
		// boolean bMouseReleased =
		// pickingTriggerMouseAdapter.wasMouseReleased();

		EPickingMode ePickingMode = EPickingMode.CLICKED;

		if (pickingTriggerMouseAdapter.wasLeftMouseButtonPressed())
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

		// // Just for testing
		// float[] test =
		// GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(
		// gl, pickPoint.x, pickPoint.y);
		// System.out.println("Object space coordinates: " +test[0] + "," +
		// test[1] + "," + test[2]);

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
		glu.gluPickMatrix((double) pickPoint.x, (double) (viewport[3] - pickPoint.y),// 
				5.0, 5.0, viewport, 0); // pick width and height is set to 5
		// (i.e. picking tolerance)

		float fAspectRatio = (float) (float) (viewport[3] - viewport[1])
				/ (float) (viewport[2] - viewport[0]);

		IViewFrustum viewFrustum = canvasUser.getViewFrustum();

		if (fAspectRatio < 1.0)
		{
			fAspectRatio = 1.0f / fAspectRatio;

			if (viewFrustum.getProjectionMode().equals(ProjectionMode.ORTHOGRAPHIC))
			{
				gl.glOrtho(viewFrustum.getLeft() * fAspectRatio, viewFrustum.getRight()
						* fAspectRatio, viewFrustum.getBottom(), viewFrustum.getTop(),
						viewFrustum.getNear(), viewFrustum.getFar());
			}
			else
			{
				gl.glFrustum(viewFrustum.getLeft() * fAspectRatio, viewFrustum.getRight()
						* fAspectRatio, viewFrustum.getBottom(), viewFrustum.getTop(),
						viewFrustum.getNear(), viewFrustum.getFar());
			}
		}
		else
		{
			if (viewFrustum.getProjectionMode().equals(ProjectionMode.ORTHOGRAPHIC))
			{
				gl.glOrtho(viewFrustum.getLeft(), viewFrustum.getRight(), viewFrustum
						.getBottom()
						* fAspectRatio, viewFrustum.getTop() * fAspectRatio, viewFrustum
						.getNear(), viewFrustum.getFar());
			}
			else
			{
				gl.glFrustum(viewFrustum.getLeft(), viewFrustum.getRight(), viewFrustum
						.getBottom()
						* fAspectRatio, viewFrustum.getTop() * fAspectRatio, viewFrustum
						.getNear(), viewFrustum.getFar());
			}
		}

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

		// TODO: exceptions
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
	 * @param uniqueManagedObject
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
	 * @param iViewID
	 * @param iType
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

	private int calculateID(int iViewID, int iType)
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

		ArrayList<Integer> iAlPickedObjectId = new ArrayList<Integer>(2);

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
				//iAlPickedObjectId.add(iArPickingBuffer[iPickingBufferCounter+3
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

		int iPickingID = 0;
		int iSignature = 0;
		int iOrigianlPickingID = 0;
		for (int iResultCounter = 0; iResultCounter < alPickingIDs.size(); iResultCounter++)
		{
			iPickingID = alPickingIDs.get(iResultCounter);

			int iType = getTypeFromPickingID(iPickingID);

			// // check here for all icons in the toolbox that the bucket should
			// handle
			// // FIXME: longterm: not the nicest thing, removes generality from
			// picking manager
			if (iType == EPickingType.BUCKET_MOVE_IN_ICON_SELECTION.ordinal()
					|| iType == EPickingType.BUCKET_MOVE_OUT_ICON_SELECTION.ordinal()
					|| iType == EPickingType.BUCKET_MOVE_LEFT_ICON_SELECTION.ordinal()
					|| iType == EPickingType.BUCKET_MOVE_RIGHT_ICON_SELECTION.ordinal()
					|| iType == EPickingType.BUCKET_LOCK_ICON_SELECTION.ordinal()
					|| iType == EPickingType.VIEW_SELECTION.ordinal())
			// || iType == EPickingType.BUCKET_REMOVE_ICON_SELECTION.ordinal()
			// || iType == EPickingType.BUCKET_SWITCH_ICON_SELECTION.ordinal())
			{

				iSignature = getSignatureFromPickingID(iPickingID, iViewID);

				iOrigianlPickingID = iPickingID;
			}
			else
			{
				if (bIsMaster && iResultCounter == 0)
				{
					iSignature = getSignatureFromPickingID(iPickingID, iViewID);

					iOrigianlPickingID = iPickingID;
				}
				else
				{
					int iViewUnderInteractionID = hashSignatureToPickingIDHashMap.get(
							iSignature).get(iOrigianlPickingID);
					iSignature = getSignatureFromPickingID(iPickingID, iViewUnderInteractionID);

				}
			}

			if (!bIsMaster)
			{

			}
			else
			{
				// System.out.println("Should be the name of a view: " +
				// iViewUnderInteractionID);
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

	private int getSignature(int iViewID, int iType)
	{

		return (iViewID * 100 + iType);
	}

	private int getSignatureFromPickingID(int iPickingID, int iViewID)
	{

		int iTemp = iPickingID / 100;
		int iType = iPickingID - iTemp * 100;

		return (getSignature(iViewID, iType));
	}

	private int getTypeFromPickingID(int iPickingID)
	{

		int iTemp = iPickingID / 100;
		return (iPickingID - iTemp * 100);
	}

	private void checkViewID(int iViewID)
	{

		if (iViewID > 999999 || iViewID < 1000)
		{
			throw new CaleydoRuntimeException(
					"PickingManager: The view id has to have exactly 5 digits",
					CaleydoRuntimeExceptionType.MANAGER);
		}
	}

	private void checkType(int iType)
	{

		if (iType > 99 || iType < 0)
		{
			throw new CaleydoRuntimeException(
					"PickingManager: Type has to be larger then or exactly 0 and less than 100",
					CaleydoRuntimeExceptionType.MANAGER);
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

	/*
	 * Ü (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#getItem(int)
	 */
	public Object getItem(int itemId)
	{

		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#hasItem(int)
	 */
	public boolean hasItem(int itemId)
	{

		// TODO Auto-generated method stub
		return false;
	}
}
