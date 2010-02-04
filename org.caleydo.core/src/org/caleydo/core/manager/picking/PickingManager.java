package org.caleydo.core.manager.picking;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

import com.sun.opengl.util.BufferUtil;

/**
 * <p>
 * Handles picking for instances of {@link AGLView}. When drawing objects which should later be picked, use
 * the {@link #getPickingID(int, EPickingType, int)} method to get an ID to use in the glPushName() function.
 * This function is provided with an externalID which is intended for use in the calling instance to identify
 * the picked element.
 * </p>
 * <p>
 * To perform the actual picking the {@link #handlePicking(AGLView, GL)} method has to be called in every
 * render step.
 * </p>
 * <p>
 * The results of the operation can later be retrieved by first calling {@link #getHitTypes(int)} to get all
 * the types that have been hit and then calling {@link #getHits(int, EPickingType)} which returns the actual
 * hits
 * </p>
 * 
 * @author Alexander Lex
 */
public class PickingManager {

	/**
	 * Container for all {@link Pick}s associated with a view. *
	 */
	private class ViewSpecificHitListContainer {
		EnumMap<EPickingType, ArrayList<Pick>> hashPickingTypeToPicks;

		private ViewSpecificHitListContainer() {
			hashPickingTypeToPicks = new EnumMap<EPickingType, ArrayList<Pick>>(EPickingType.class);
		}

		/**
		 * Returns all picks for a specific type
		 * 
		 * @param pickingType
		 * @return
		 */
		public ArrayList<Pick> getPicksForPickingType(EPickingType pickingType) {
			return hashPickingTypeToPicks.get(pickingType);
		}

		/**
		 * Add a pick to a type
		 * 
		 * @param pickingType
		 *            the type of the pick
		 * @param pick
		 *            the pick itself
		 */
		private void addPicksForPickingType(EPickingType pickingType, Pick pick) {
			ArrayList<Pick> picks = hashPickingTypeToPicks.get(pickingType);
			if (picks == null) {
				picks = new ArrayList<Pick>();
				hashPickingTypeToPicks.put(pickingType, picks);
			}
			else {
				picks.clear();
			}
			picks.add(pick);
		}
	}

	/**
	 * An instance of this class stores all picking IDs for a view and maps them to the external ID (which is
	 * for internal use in the views which use the picking manager)
	 */
	private class ViewSpecificPickingIDContainer {

		EnumMap<EPickingType, HashMap<Integer, Integer>> hashTypeToPickingIDToExternalID;
		EnumMap<EPickingType, HashMap<Integer, Integer>> hashTypeToExternaldIDToPickingID;

		public ViewSpecificPickingIDContainer() {
			hashTypeToPickingIDToExternalID =
				new EnumMap<EPickingType, HashMap<Integer, Integer>>(EPickingType.class);
			hashTypeToExternaldIDToPickingID =
				new EnumMap<EPickingType, HashMap<Integer, Integer>>(EPickingType.class);
		}

		/**
		 * Add a new pickingID and its corresponding externalID
		 * 
		 * @param pickingID
		 * @param externalID
		 */
		public void put(EPickingType pickingType, Integer pickingID, Integer externalID) {
			HashMap<Integer, Integer> hashPickingIDToExternalID =
				hashTypeToPickingIDToExternalID.get(pickingType);
			if (hashPickingIDToExternalID == null) {
				hashPickingIDToExternalID = new HashMap<Integer, Integer>();
				hashTypeToPickingIDToExternalID.put(pickingType, hashPickingIDToExternalID);
			}
			hashPickingIDToExternalID.put(pickingID, externalID);

			HashMap<Integer, Integer> hashExternaldIDToPickingID =
				hashTypeToExternaldIDToPickingID.get(pickingType);
			if (hashExternaldIDToPickingID == null) {
				hashExternaldIDToPickingID = new HashMap<Integer, Integer>();
				hashTypeToExternaldIDToPickingID.put(pickingType, hashExternaldIDToPickingID);
			}
			hashExternaldIDToPickingID.put(externalID, pickingID);
		}

		/**
		 * Returns the external ID associated with the provided pickingID and pickingType or null if no such
		 * mapping exists
		 * 
		 * @param pickingID
		 * @param pickingType
		 * @return the externalID or null
		 */
		public Integer getExternalID(EPickingType pickingType, Integer pickingID) {
			HashMap<Integer, Integer> hashMap = hashTypeToPickingIDToExternalID.get(pickingType);
			if (hashMap == null)
				return null;

			return hashMap.get(pickingID);
		}

		/**
		 * Returns the picking ID associated with the provided external ID or null if no such mapping exists
		 * 
		 * @param pickingType
		 * @param externalID
		 * @return the pickingID or null
		 */
		public Integer getPickingID(EPickingType pickingType, Integer externalID) {
			HashMap<Integer, Integer> hashMap = hashTypeToExternaldIDToPickingID.get(pickingType);
			if (hashMap == null)
				return null;

			return hashMap.get(externalID);
		}

		/**
		 * Returns all pickingIDs stored in the container
		 * 
		 * @return all picking IDs
		 */
		public Set<Integer> getAllPickingIDs() {
			Set<Integer> set = new HashSet<Integer>();
			for (EPickingType type : hashTypeToPickingIDToExternalID.keySet()) {
				set.addAll(hashTypeToPickingIDToExternalID.get(type).keySet());
			}

			return set;
		}
	}

	/**
	 * HashMap that has a view ID as key and a {@link ViewSpecificPickingIDContainer} as value. The container
	 * stores all picking IDs for all elements of a specific view.
	 */
	private HashMap<Integer, ViewSpecificPickingIDContainer> hashViewIDToViewSpecificPickingIDContainer;
	/**
	 * HashMap with the view ID as key and a {@link ViewSpecificHitListContainer} as value. The Container
	 * stores list of current hits for one view.
	 */
	private HashMap<Integer, ViewSpecificHitListContainer> hashViewIDToViewSpecificHitListContainer;
	/**
	 * HashMap with the view ID as key and a flag as value which helps determine whether a mouse was newly
	 * moved over an element (in contrast to resting on the element)
	 */
	private HashMap<Integer, Boolean> hashViewIDToIsMouseOverPickingEvent;
	/**
	 * HashMap with the pickingID as key and a Pair containing the associated viewID and the picking type.
	 * Needed for back-referencing pickingID -> viewID and pickingID -> pickingType
	 */
	private HashMap<Integer, Pair<Integer, EPickingType>> hashPickingIDToViewID;

	private int iIDCounter = 0;

	private boolean bEnablePicking = true;

	/** The smallest z value of a pick */
	private float fMinimumZValue;

	/**
	 * Constructor
	 */
	public PickingManager() {
		hashViewIDToViewSpecificHitListContainer = new HashMap<Integer, ViewSpecificHitListContainer>();
		hashViewIDToViewSpecificPickingIDContainer = new HashMap<Integer, ViewSpecificPickingIDContainer>();
		hashViewIDToIsMouseOverPickingEvent = new HashMap<Integer, Boolean>();
		hashPickingIDToViewID = new HashMap<Integer, Pair<Integer, EPickingType>>();
	}

	/**
	 * Turn on/off picking
	 * 
	 * @param bEnablePicking
	 */
	public void enablePicking(final boolean bEnablePicking) {
		this.bEnablePicking = bEnablePicking;
	}

	/**
	 * Returns a unique picking ID which can be used for the glPushName() commands. The returned id is mapped
	 * to the provided externalID which is intended to be used by the caller internally. external id
	 * 
	 * @param iViewID
	 *            the ID of the calling view
	 * @param ePickingType
	 *            the type of the pick
	 * @param iExternalID
	 *            an arbitrary integer which helps the client of the manager to determine which element was
	 *            picked
	 * @return the picking id, use {@link #getExternalIDFromPickingID(int, int)} to retrieve the corresponding
	 *         external id
	 */
	public int getPickingID(int iViewID, EPickingType ePickingType, int iExternalID) {

		ViewSpecificPickingIDContainer pickingIDContainer =
			hashViewIDToViewSpecificPickingIDContainer.get(iViewID);
		if (pickingIDContainer == null) {
			pickingIDContainer = new ViewSpecificPickingIDContainer();
			hashViewIDToViewSpecificPickingIDContainer.put(iViewID, pickingIDContainer);
		}
		Integer pickingID = pickingIDContainer.getPickingID(ePickingType, iExternalID);
		if (pickingID == null)
			pickingID = calculateID();

		pickingIDContainer.put(ePickingType, pickingID, iExternalID);
		hashPickingIDToViewID.put(pickingID, new Pair<Integer, EPickingType>(iViewID, ePickingType));
		return pickingID;
	}

	/**
	 * This method has to be called in every display step. It is responsible for the ray tracing which does
	 * the actual picking. It needs the ID of the calling view and a gl context. It calls the display() method
	 * of the calling view, therefore only elements rendered in the display() can be picked.
	 * 
	 * @param glView
	 *            a reference to the calling view
	 * @param gl
	 *            the GL context
	 */
	public void handlePicking(final AGLView glView, final GL gl) {

		if (bEnablePicking == false)
			return;

		GLMouseListener glMouseListener = glView.getParentGLCanvas().getGLMouseListener();

		Point pickPoint = null;

		EPickingMode ePickingMode = EPickingMode.CLICKED;

		if (glMouseListener.wasMouseDoubleClicked()) {
			pickPoint = glMouseListener.getPickedPoint();
			ePickingMode = EPickingMode.DOUBLE_CLICKED;
		}
		else if (glMouseListener.wasMouseDragged()) {
			pickPoint = glMouseListener.getPickedPoint();
			ePickingMode = EPickingMode.DRAGGED;
		}
		else if (glMouseListener.wasLeftMouseButtonPressed()) {
			pickPoint = glMouseListener.getPickedPoint();
			ePickingMode = EPickingMode.CLICKED;
			// ContextMenu.get().flush();
		}
		else if (glMouseListener.wasRightMouseButtonPressed()) {
			pickPoint = glMouseListener.getPickedPoint();
			ePickingMode = EPickingMode.RIGHT_CLICKED;
		}
		else if (glMouseListener.wasMouseMoved()) {
			// Restart timer
			// hashViewIDToLastMouseMovedTimeStamp.put(iViewID,
			// System.nanoTime());
			hashViewIDToIsMouseOverPickingEvent.put(glView.getID(), true);
		}
		else if (hashViewIDToIsMouseOverPickingEvent.get(glView.getID()) != null
			&& hashViewIDToIsMouseOverPickingEvent.get(glView.getID()) == true) {
			pickPoint = glMouseListener.getPickedPoint();
			// hashViewIDToLastMouseMovedTimeStamp.put(iViewID,
			// System.nanoTime());
			ePickingMode = EPickingMode.MOUSE_OVER;
		}

		if (pickPoint == null)
			return;

		hashViewIDToIsMouseOverPickingEvent.put(glView.getID(), false);

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

		float fAspectRatio = (float) (viewport[3] - viewport[1]) / (float) (viewport[2] - viewport[0]);

		IViewFrustum viewFrustum = glView.getViewFrustum();

		viewFrustum.setProjectionMatrix(gl, fAspectRatio);

		// gl.glMatrixMode(GL.GL_MODELVIEW);

		// Store picked point
		Point tmpPickPoint = (Point) pickPoint.clone();
		// Reset picked point
		pickPoint = null;

		glView.display(gl);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		// System.out.println("Picking Buffer: " + iArPickingBuffer[0]);
		// processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint,
		// ePickingMode);
		ArrayList<Integer> iAlPickedObjectId = processHits(iHitCount, iArPickingBuffer);

		if (iAlPickedObjectId.size() > 0) {
			processPicks(iAlPickedObjectId, ePickingMode, tmpPickPoint, glMouseListener
				.getPickedPointDragStart());
		}
	}

	/**
	 * Returns all picking types for a view that have been hit in the last cycle
	 * 
	 * @param iViewID
	 *            the ID of the view
	 * @return A set of als picking types hit in the previous cycle
	 */
	public Set<EPickingType> getHitTypes(int iViewID) {
		if (hashViewIDToViewSpecificHitListContainer.get(iViewID) == null)
			return null;
		return hashViewIDToViewSpecificHitListContainer.get(iViewID).hashPickingTypeToPicks.keySet();
	}

	/**
	 * Returns the hits of the last cycle for a particular view and type
	 * 
	 * @param iViewID
	 *            the id of the view
	 * @param ePickingType
	 *            the type of the hits
	 * @return null if no Hits, else the ArrayList<Integer> with the hits
	 */
	public ArrayList<Pick> getHits(int iViewID, EPickingType ePickingType) {

		if (hashViewIDToViewSpecificHitListContainer.get(iViewID) == null)
			return null;
		// else
		return hashViewIDToViewSpecificHitListContainer.get(iViewID).getPicksForPickingType(ePickingType);
	}

	/**
	 * Flush a particular hit list. This has to be done after every cycle.
	 * 
	 * @param iViewID
	 *            the id of the calling view
	 * @param ePickingType
	 *            the picking type determining which hits should be flushed
	 */
	public void flushHits(int iViewID, EPickingType ePickingType) {

		if (hashViewIDToViewSpecificHitListContainer.get(iViewID) != null) {
			hashViewIDToViewSpecificHitListContainer.get(iViewID).getPicksForPickingType(ePickingType)
				.clear();
		}
	}

	/**
	 * Removes all data associated with a view. You should do that when you close a view
	 * 
	 * @param iViewID
	 *            the id of the calling view
	 */
	public void removeViewSpecificData(int iViewID) {
		hashViewIDToIsMouseOverPickingEvent.remove(iViewID);
		hashViewIDToViewSpecificHitListContainer.remove(iViewID);
		ViewSpecificPickingIDContainer container = hashViewIDToViewSpecificPickingIDContainer.get(iViewID);

		if (container != null && container.getAllPickingIDs() != null) {
			for (Integer pickingID : container.getAllPickingIDs()) {
				hashPickingIDToViewID.remove(pickingID);
			}
		}

		hashViewIDToViewSpecificPickingIDContainer.remove(iViewID);
	}

	/**
	 * Returns the external ID (the id with which you initialized getPickingID()) when you provide the picking
	 * ID and the id of the view.
	 * 
	 * @param viewID
	 *            the id of the view which has to be the same which was used when the picking id was created
	 * @param pickingID
	 *            the picking ID of which the external id mapping is desired
	 * @return the external ID, null if no entry for that pickingID
	 */
	private Integer getExternalIDFromPickingID(int viewID, EPickingType pickingType, int pickingID) {
		return hashViewIDToViewSpecificPickingIDContainer.get(viewID).getExternalID(pickingType, pickingID);
	}

	/**
	 * Calculates the picking id, based on a type
	 * 
	 * @param iType
	 *            the type
	 * @return a unique ID
	 */
	private int calculateID() {
		return iIDCounter++;
	}

	/**
	 * Extracts the nearest hit from the provided iArPickingBuffer Stores it internally Can process only one
	 * hit at at time at the moment
	 * 
	 * @param iHitCount
	 * @param iArPickingBuffer
	 */
	private ArrayList<Integer> processHits(int iHitCount, int[] iArPickingBuffer) {

		int iPickingBufferCounter = 0;

		ArrayList<Integer> iAlPickedObjectId = new ArrayList<Integer>(4);

		// Only pick object that is nearest
		int iMinimumZValue = Integer.MAX_VALUE;
		int iNumberOfNames = 0;
		int iNearestObjectIndex = 0;
		for (int iCount = 0; iCount < iHitCount; iCount++) {
			// if first object is no hit skip z values
			if (iArPickingBuffer[iPickingBufferCounter] == 0) {
				iPickingBufferCounter += 3;
				continue;
			}
			// iPickingBufferCounter++;
			// Check if object is nearer than previous objects
			if (iArPickingBuffer[iPickingBufferCounter + 1] < iMinimumZValue) {
				// first element is number of names on name stack
				// second element is min Z Value
				iMinimumZValue = iArPickingBuffer[iPickingBufferCounter + 1];

				iNearestObjectIndex = iPickingBufferCounter;

				// third element is max Z Value
				// fourth element is name of lowest name on stack
				// iAlPickedObjectId.add(iArPickingBuffer[iPickingBufferCounter+3
				// ]);
			}
			fMinimumZValue = getDepth(iMinimumZValue);
			// System.out.println("Z Value: " + getDepth(iMinimumZValue));
			iPickingBufferCounter = iPickingBufferCounter + 3 + iArPickingBuffer[iPickingBufferCounter];

		}

		iNumberOfNames = iArPickingBuffer[iNearestObjectIndex];

		for (int iNameCount = 0; iNameCount < iNumberOfNames; iNameCount++) {
			iAlPickedObjectId.add(iArPickingBuffer[iNearestObjectIndex + 3 + iNameCount]);
		}

		return iAlPickedObjectId;
		// iPickingBufferCounter += iNumberOfNames;

		// return iPickedObjectId;
	}

	private float getDepth(int iZValue) {
		long depth = iZValue; // large -ve number
		return (1.0f + ((float) depth / 0x7fffffff));
		// return as a float between 0 and 1
	}

	/**
	 * Given the list of picking IDs which where hit it maps the picking ID to the view and stores it in the
	 * hit list
	 * 
	 * @param alPickingIDs
	 * @param myMode
	 * @param pickedPoint
	 * @param dragStartPoint
	 */
	private void processPicks(ArrayList<Integer> alPickingIDs, EPickingMode myMode, Point pickedPoint,
		Point dragStartPoint) {

		for (int pickingID : alPickingIDs) {
			Pair<Integer, EPickingType> pickAssociatedValues = hashPickingIDToViewID.get(pickingID);
			EPickingType eType = pickAssociatedValues.getSecond();
			int viewIDToUse = pickAssociatedValues.getFirst();

			Pick pick =
				new Pick(getExternalIDFromPickingID(viewIDToUse, eType, pickingID), myMode, pickedPoint,
					dragStartPoint, fMinimumZValue);

			ViewSpecificHitListContainer hitContainer =
				hashViewIDToViewSpecificHitListContainer.get(viewIDToUse);
			if (hitContainer == null) {
				hitContainer = new ViewSpecificHitListContainer();
				hashViewIDToViewSpecificHitListContainer.put(viewIDToUse, hitContainer);
			}
			hitContainer.addPicksForPickingType(eType, pick);
		}
	}
}
