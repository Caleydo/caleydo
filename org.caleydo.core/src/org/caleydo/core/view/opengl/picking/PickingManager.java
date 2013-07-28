/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;
import gleem.linalg.Vec2f;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

import com.jogamp.common.nio.Buffers;

/**
 * <p>
 * Handles picking for instances of {@link AGLView}. When drawing objects which should later be picked, use the
 * {@link #getPickingID(int, String, int)} (which returns what is henceforth called <b>pickingID</b>) method to get an
 * ID to use in the glPushName() function. This function is provided with an <b>pickedObjectID</b> which is intended for
 * use in the calling instance to identify the picked element.
 * </p>
 * <p>
 * To perform the actual picking the {@link #handlePicking(AGLView, GL)} method has to be called in every render step.
 * </p>
 * <p>
 * The results of the operation can later be retrieved by first calling {@link #getHitTypes(int)} to get all the types
 * that have been hit and then calling {@link #getHits(int, String)} which returns the actual hits
 * </p>
 *
 * @author Alexander Lex
 * @author Christian Partl
 */
public class PickingManager {

	/**
	 * Container for all {@link Pick}s associated with a view. *
	 */
	private class ViewSpecificHitListContainer {
		HashMap<String, ArrayList<Pick>> hashPickingTypeToPicks;

		private ViewSpecificHitListContainer() {
			hashPickingTypeToPicks = new HashMap<String, ArrayList<Pick>>();
		}

		/**
		 * Returns all picks for a specific type
		 *
		 * @param pickingType
		 * @return
		 */
		public ArrayList<Pick> getPicksForPickingType(String pickingType) {
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
		private void addPicksForPickingType(String pickingType, Pick pick, boolean clearPicksBeforeAdding) {
			ArrayList<Pick> picks = hashPickingTypeToPicks.get(pickingType);
			if (picks == null) {
				picks = new ArrayList<Pick>();
				hashPickingTypeToPicks.put(pickingType, picks);
			} else {
				if (clearPicksBeforeAdding) {
					picks.clear();
				}
			}
			picks.add(pick);
		}

		/**
		 * Returns all picking types that have been added to the container.
		 *
		 * @return
		 */
		private Set<String> getPickingTypes() {
			return hashPickingTypeToPicks.keySet();
		}

		/**
		 * Creates a deep copy of the container.
		 *
		 * @return
		 */
		private ViewSpecificHitListContainer copy() {
			ViewSpecificHitListContainer copy = new ViewSpecificHitListContainer();
			for (String pickingType : hashPickingTypeToPicks.keySet()) {
				ArrayList<Pick> picks = new ArrayList<Pick>(hashPickingTypeToPicks.get(pickingType));
				copy.hashPickingTypeToPicks.put(pickingType, picks);
			}

			return copy;
		}

	}

	/**
	 * An instance of this class stores all picking IDs for a view and maps them to the pickedObjectID (which is for
	 * internal use in the views which use the picking manager)
	 */
	private class ViewSpecificPickingIDContainer {

		HashMap<String, HashMap<Integer, Integer>> hashTypeToPickingIDToPickedObjectID;
		HashMap<String, HashMap<Integer, Integer>> hashTypeToPickedObjectIDToPickingID;

		public ViewSpecificPickingIDContainer() {
			hashTypeToPickingIDToPickedObjectID = new HashMap<String, HashMap<Integer, Integer>>();
			hashTypeToPickedObjectIDToPickingID = new HashMap<String, HashMap<Integer, Integer>>();
		}

		/**
		 * Add a new pickingID and its corresponding pickedObjectID
		 *
		 * @param pickingID
		 * @param pickedObjectID
		 */
		public void put(String pickingType, Integer pickingID, Integer pickedObjectID) {
			HashMap<Integer, Integer> hashPickingIDToPickedObjectID = hashTypeToPickingIDToPickedObjectID
					.get(pickingType);
			if (hashPickingIDToPickedObjectID == null) {
				hashPickingIDToPickedObjectID = new HashMap<Integer, Integer>();
				hashTypeToPickingIDToPickedObjectID.put(pickingType, hashPickingIDToPickedObjectID);
			}
			hashPickingIDToPickedObjectID.put(pickingID, pickedObjectID);

			HashMap<Integer, Integer> hashPickedObjectIDToPickingID = hashTypeToPickedObjectIDToPickingID
					.get(pickingType);
			if (hashPickedObjectIDToPickingID == null) {
				hashPickedObjectIDToPickingID = new HashMap<Integer, Integer>();
				hashTypeToPickedObjectIDToPickingID.put(pickingType, hashPickedObjectIDToPickingID);
			}
			hashPickedObjectIDToPickingID.put(pickedObjectID, pickingID);
		}

		/**
		 * Returns the pickedObjectID associated with the provided pickingID and pickingType or null if no such mapping
		 * exists
		 *
		 * @param pickingID
		 * @param pickingType
		 * @return the picedObjectID or null if no id was found
		 */
		public Integer getPickedObjectID(String pickingType, Integer pickingID) {
			HashMap<Integer, Integer> hashMap = hashTypeToPickingIDToPickedObjectID.get(pickingType);
			if (hashMap == null)
				return null;

			return hashMap.get(pickingID);
		}

		/**
		 * Returns the picking ID associated with the provided pickedObjectID or null if no such mapping exists
		 *
		 * @param pickingType
		 * @param pickedObjectID
		 * @return the pickingID or null
		 */
		public Integer getPickingID(String pickingType, Integer pickedObjectID) {
			HashMap<Integer, Integer> hashMap = hashTypeToPickedObjectIDToPickingID.get(pickingType);
			if (hashMap == null)
				return null;

			return hashMap.get(pickedObjectID);
		}

		/**
		 * Returns all pickingIDs stored in the container
		 *
		 * @return all picking IDs
		 */
		public Set<Integer> getAllPickingIDs() {
			Set<Integer> table = new HashSet<Integer>();
			for (String type : hashTypeToPickingIDToPickedObjectID.keySet()) {
				table.addAll(hashTypeToPickingIDToPickedObjectID.get(type).keySet());
			}

			return table;
		}
	}

	/**
	 * HashMap that has a view ID as key and a {@link ViewSpecificPickingIDContainer} as value. The container stores all
	 * picking IDs for all elements of a specific view.
	 */
	private HashMap<Integer, ViewSpecificPickingIDContainer> hashViewIDToViewSpecificPickingIDContainer;
	/**
	 * HashMap with the view ID as key and a {@link ViewSpecificHitListContainer} as value. The Container stores list of
	 * current hits for one view.
	 */
	private HashMap<Integer, ViewSpecificHitListContainer> hashViewIDToViewSpecificHitListContainer;

	/**
	 * HashMap with the view ID as key and a {@link ViewSpecificHitListContainer} as value. The Container stores list of
	 * hits from the previous picking procedure for one view.
	 */
	private HashMap<Integer, ViewSpecificHitListContainer> hashViewIDToPreviousViewSpecificHitListContainer;
	/**
	 * HashMap with the view ID as key and a flag as value which helps determine whether a mouse was newly moved over an
	 * element (in contrast to resting on the element)
	 */
	private HashMap<Integer, Boolean> hashViewIDToIsMouseOverPickingEvent;
	/**
	 * HashMap with the pickingID as key and a Pair containing the associated viewID and the picking type. Needed for
	 * back-referencing pickingID -> viewID and pickingID -> pickingType
	 */
	private HashMap<Integer, Pair<Integer, String>> hashPickingIDToViewID;

	private int iIDCounter = 0;

	private boolean enablePicking = true;

	/** The smallest z value of a pick */
	private float fMinimumZValue;

	/**
	 * Constructor
	 */
	public PickingManager() {
		hashViewIDToViewSpecificHitListContainer = new HashMap<Integer, ViewSpecificHitListContainer>();
		hashViewIDToPreviousViewSpecificHitListContainer = new HashMap<Integer, ViewSpecificHitListContainer>();
		hashViewIDToViewSpecificPickingIDContainer = new HashMap<Integer, ViewSpecificPickingIDContainer>();
		hashViewIDToIsMouseOverPickingEvent = new HashMap<Integer, Boolean>();
		hashPickingIDToViewID = new HashMap<Integer, Pair<Integer, String>>();
	}

	/**
	 * Turn on/off picking
	 *
	 * @param enablePicking
	 */
	public void enablePicking(final boolean enablePicking) {
		this.enablePicking = enablePicking;
	}

	/**
	 * Returns a unique picking ID which can be used for the glPushName() commands. The returned id is mapped to the
	 * provided pickedObjectID which is intended to be used by the caller internally.
	 *
	 * @param viewID
	 *            the ID of the calling view
	 * @param ePickingType
	 *            the type of the pick
	 * @param pickedObjectID
	 *            an arbitrary integer which helps the client of the manager to determine which element was picked
	 * @return the picking id
	 */
	public int getPickingID(Integer viewID, String ePickingType, Integer pickedObjectID) {

		ViewSpecificPickingIDContainer pickingIDContainer = hashViewIDToViewSpecificPickingIDContainer.get(viewID);
		if (pickingIDContainer == null) {
			pickingIDContainer = new ViewSpecificPickingIDContainer();
			hashViewIDToViewSpecificPickingIDContainer.put(viewID, pickingIDContainer);
		}
		Integer pickingID = pickingIDContainer.getPickingID(ePickingType, pickedObjectID);
		if (pickingID == null)
			pickingID = calculateID();

		pickingIDContainer.put(ePickingType, pickingID, pickedObjectID);
		hashPickingIDToViewID.put(pickingID, new Pair<Integer, String>(viewID, ePickingType));
		return pickingID;
	}

	/**
	 * Returns a unique picking ID which can be used for the glPushName() commands. The returned id is mapped to the
	 * provided pickedObjectID which is intended to be used by the caller internally. external id
	 *
	 * @param viewID
	 *            the ID of the calling view
	 * @param ePickingType
	 *            the type of the pick
	 * @param pickedObjectID
	 *            an arbitrary integer which helps the client of the manager to determine which element was picked
	 * @return the picking id
	 * @deprecated use {@link #getPickingID(Integer, String, Integer)} instead
	 */
	@Deprecated
	public int getPickingID(int viewID, PickingType ePickingType, int pickedObjectID) {

		return getPickingID(viewID, ePickingType.name(), pickedObjectID);
	}

	/**
	 * This method has to be called in every display step. It is responsible for the ray tracing which does the actual
	 * picking. It needs the ID of the calling view and a gl context. It calls the display() method of the calling view,
	 * therefore only elements rendered in the display() can be picked.
	 *
	 * @param glView
	 *            a reference to the calling view
	 * @param gl
	 *            the GL2 context
	 */
	public void handlePicking(final AGLView glView, final GL2 gl) {

		if (enablePicking == false)
			return;

		GLMouseListener glMouseListener = glView.getGLMouseListener();

		PickingMode ePickingMode = PickingMode.CLICKED;

		if (glMouseListener.wasMouseDoubleClicked()) {
			ePickingMode = PickingMode.DOUBLE_CLICKED;
		} else if (glMouseListener.wasMouseDragged()) {
			ePickingMode = PickingMode.DRAGGED;
		} else if (glMouseListener.wasLeftMouseButtonPressed()) {
			ePickingMode = PickingMode.CLICKED;
		} else if (glMouseListener.wasRightMouseButtonPressed()) {
			ePickingMode = PickingMode.RIGHT_CLICKED;
		} else if (glMouseListener.wasMouseMoved()) {
			// Restart timer
			// hashViewIDToLastMouseMovedTimeStamp.put(viewID,
			// System.nanoTime());
			hashViewIDToIsMouseOverPickingEvent.put(glView.getID(), true);
			ePickingMode = PickingMode.MOUSE_OVER;
		} else if (hashViewIDToIsMouseOverPickingEvent.get(glView.getID()) != null
				&& hashViewIDToIsMouseOverPickingEvent.get(glView.getID()) == true) {

			// pickPoint = glMouseListener.getPickedPoint();
			// hashViewIDToLastMouseMovedTimeStamp.put(viewID,
			// System.nanoTime());
			// ePickingMode = PickingMode.MOUSE_OVER;
			return;
		} else {
			return;
		}
		Point pickRealPoint = glMouseListener.getRAWPickedPoint();
		if (pickRealPoint == null)
			return;

		hashViewIDToIsMouseOverPickingEvent.put(glView.getID(), false);

		int PICKING_BUFSIZE = 1024;

		int iArPickingBuffer[] = new int[PICKING_BUFSIZE];
		IntBuffer pickingBuffer = Buffers.newDirectIntBuffer(PICKING_BUFSIZE);
		int iHitCount = -1;
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		gl.glSelectBuffer(PICKING_BUFSIZE, pickingBuffer);
		gl.glRenderMode(GL2.GL_SELECT);

		gl.glInitNames();

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		// gl.glPushName(99);

		/* create 5x5 pixel picking region near cursor location */
		GLU glu = new GLU();
		glu.gluPickMatrix(pickRealPoint.x, (viewport[3] - pickRealPoint.y),//
				5.0, 5.0, viewport, 0); // pick width and height is set to 5

		// (i.e. picking tolerance)

		float fAspectRatio = (float) (viewport[3] - viewport[1]) / (float) (viewport[2] - viewport[0]);

		ViewFrustum viewFrustum = glView.getViewFrustum();
		viewFrustum.setProjectionMatrix(gl, fAspectRatio);

		// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		// Store picked point
		Vec2f pickDIPPoint = glMouseListener.getDIPPickedPoint();

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glView.display(gl);
		gl.glPopMatrix();

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		iHitCount = gl.glRenderMode(GL2.GL_RENDER);
		pickingBuffer.get(iArPickingBuffer);
		// System.out.println("Picking Buffer: " + iArPickingBuffer[0]);
		// processHits(gl, iHitCount, iArPickingBuffer, tmpPickPoint,
		// ePickingMode);
		ArrayList<Integer> iAlPickedObjectId = processHits(iHitCount, iArPickingBuffer);

		// We have to check the parent canvas to identify common remote rendered
		// views
		IGLCanvas parentCanvas = glView.getParentGLCanvas();

		if (iAlPickedObjectId.size() > 0) {
			processPicks(iAlPickedObjectId, ePickingMode, pickDIPPoint, glMouseListener.getDIPPickedPointDragStart());

			Set<Integer> processedViews = new HashSet<Integer>();

			// We have to add a MOUSE_OUT on other remote rendered views, if a
			// picking event occurred
			for (int pickingID : iAlPickedObjectId) {
				Pair<Integer, String> pickAssociatedValues = hashPickingIDToViewID.get(pickingID);
				int pickedViewID = pickAssociatedValues.getFirst();

				// Check real current bricks (here previous, because they don't
				// contain MOUSE_OUT) for performance increase, i.e. a new
				// MOUSE_OVER is only considered, if it just happened.
				// ViewSpecificHitListContainer pickedHitListContainer =
				// hashViewIDToViewSpecificHitListContainer
				// .get(pickedViewID);
				// boolean picksAreToBeConsidered = false;
				// for (String pickingType :
				// pickedHitListContainer.getPickingTypes()) {
				// ArrayList<Pick> picks = pickedHitListContainer
				// .getPicksForPickingType(pickingType);
				// for (Pick pick : picks) {
				// if (pick.getPickingMode() != PickingMode.MOUSE_OUT) {
				// picksAreToBeConsidered = true;
				// break;
				// }
				// }
				// }
				// if (!picksAreToBeConsidered)
				// continue;
				//
				// System.out.println("Current view: " + glView.getViewName());
				// System.out.println("Picking Type: " +
				// pickAssociatedValues.getSecond());

				for (int viewID : hashViewIDToPreviousViewSpecificHitListContainer.keySet()) {

					if (viewID != pickedViewID && !processedViews.contains(viewID)) {

						AGLView currentView = GeneralManager.get().getViewManager().getGLView(viewID);

						// Check if the views have the common gl canvas, i.e. if
						// they are rendered remote in the same SWT view frame.

						// if (currentView != null && currentView.getParentGLCanvas() == parentCanvas
						// && !isParentViewOfView(pickedViewID, viewID)) {
						// // System.out.println("Mouseout view: "
						// // + currentView.getCustomLabel());
						//
						// ViewSpecificHitListContainer previousHitContainer =
						// hashViewIDToPreviousViewSpecificHitListContainer
						// .get(viewID);
						//
						// if (previousHitContainer != null) {
						//
						// for (String pickingType : previousHitContainer.getPickingTypes()) {
						// for (Pick previousPick : previousHitContainer.getPicksForPickingType(pickingType)) {
						//
						// // Do not add a MOUSE_OUT to the same
						// // object, which was rendered by a
						// // different view (multiple glPushName
						// // calls with same Picking Type and ID,
						// // but different view ID).
						// if (!pickingType.equals(pickAssociatedValues.getSecond())
						// && previousPick.getObjectID() != getPickedObjectIDFromPickingID(
						// pickedViewID, pickAssociatedValues.getSecond(), pickingID)) {
						//
						// ViewSpecificHitListContainer hitContainer = hashViewIDToViewSpecificHitListContainer
						// .get(viewID);
						// if (hitContainer == null) {
						// hitContainer = new ViewSpecificHitListContainer();
						// hashViewIDToViewSpecificHitListContainer.put(viewID, hitContainer);
						// }
						// // if (hitContainer.getPickingTypes().size() <= 0) {
						// Pick pick = new Pick(previousPick.getObjectID(), PickingMode.MOUSE_OUT,
						// tmpPickPoint, glMouseListener.getPickedPointDragStart(),
						// fMinimumZValue);
						// hitContainer.addPicksForPickingType(pickingType, pick, true);
						//
						// processedViews.add(viewID);
						// // }
						// }
						// }
						// }
						// }
						// hashViewIDToPreviousViewSpecificHitListContainer.put(viewID,
						// new ViewSpecificHitListContainer());
						// }

						if (currentView != null && currentView.getParentGLCanvas() == parentCanvas) {
							// System.out.println("Mouseout view: " + currentView.getLabel());

							ViewSpecificHitListContainer previousHitContainer = hashViewIDToPreviousViewSpecificHitListContainer
									.get(viewID);

							if (previousHitContainer != null) {

								for (String pickingType : previousHitContainer.getPickingTypes()) {
									for (Pick previousPick : previousHitContainer.getPicksForPickingType(pickingType)) {

										// Do not add a MOUSE_OUT to the same
										// object, which was rendered by a
										// different view (multiple glPushName
										// calls with same Picking Type and ID,
										// but different view ID).
										// FIXME: THIS IS PROBABLY ONE OF THE WORST HACKS EVER! FIX BY USING NEW
										// PICKINGMANGER
										if (!(glView.getViewType().equals("org.caleydo.view.stratomex"))
												|| (!pickingType.equals(pickAssociatedValues.getSecond()) && previousPick
														.getObjectID() != getPickedObjectIDFromPickingID(pickedViewID,
														pickAssociatedValues.getSecond(), pickingID))) {

											ViewSpecificHitListContainer hitContainer = hashViewIDToViewSpecificHitListContainer
													.get(viewID);
											if (hitContainer == null) {
												hitContainer = new ViewSpecificHitListContainer();
												hashViewIDToViewSpecificHitListContainer.put(viewID, hitContainer);
											}
											// if (hitContainer.getPickingTypes().size() <= 0) {
											Pick pick = new Pick(previousPick.getObjectID(), PickingMode.MOUSE_OUT,
													pickDIPPoint, glMouseListener.getDIPPickedPointDragStart(),
													fMinimumZValue);
											hitContainer.addPicksForPickingType(pickingType, pick, true);

											processedViews.add(viewID);
											// }
										}
									}
								}
							}
							hashViewIDToPreviousViewSpecificHitListContainer.put(viewID,
									new ViewSpecificHitListContainer());
						}

					}
				}
			}

			// addMouseOutForPicksOfSpecifiedViews(viewIDsOfOtherRemoteRenderedViews,
			// glMouseListener, tmpPickPoint);

		} else {

			Set<Integer> remoteRenderedViewIDs = new HashSet<Integer>();
			for (int viewID : hashViewIDToPreviousViewSpecificHitListContainer.keySet()) {

				AGLView currentView = GeneralManager.get().getViewManager().getGLView(viewID);
				if (currentView != null && currentView.getParentGLCanvas() == parentCanvas) {
					remoteRenderedViewIDs.add(viewID);
				}

			}

			addMouseOutForPicksOfSpecifiedViews(remoteRenderedViewIDs, glMouseListener, pickDIPPoint);

		}

		// FOR DEBUGGING:
		// StringBuilder builder = new StringBuilder();
		// boolean show = false;
		// for (Entry<Integer, ViewSpecificHitListContainer> entry :
		// hashViewIDToViewSpecificHitListContainer.entrySet()) {
		// AGLView view = ViewManager.get().getGLView(entry.getKey());
		// if (view == null)
		// continue;
		//
		// StringBuilder tempBuilder = new StringBuilder();
		// boolean showView = false;
		// tempBuilder.append("=== View " + view.getLabel() + " ===\n");
		// for (Entry<String, ArrayList<Pick>> pickEntry : entry.getValue().hashPickingTypeToPicks.entrySet()) {
		// if (!pickEntry.getValue().isEmpty()) {
		// tempBuilder.append(pickEntry.getKey() + ":\n");
		// for (Pick pick : pickEntry.getValue()) {
		// tempBuilder.append(pick.getPickingMode() + "\n");
		// }
		// show = true;
		// showView = true;
		// }
		// }
		// if (showView) {
		// builder.append(tempBuilder.toString());
		// }
		// }
		// if (show) {
		// System.out.println("===================================");
		// System.out.println(builder.toString());
		// System.out.println("===================================");
		// }

	}

	static boolean isParentViewOfView(int viewID, int parentViewID) {
		AGLView view = GeneralManager.get().getViewManager().getGLView(viewID);
		AGLView parentView = GeneralManager.get().getViewManager().getGLView(parentViewID);
		if (view == null || parentView == null)
			return false;
		AGLView realParentView = (AGLView) view.getRemoteRenderingGLView();
		if (realParentView == null)
			return false;
		if (parentView == realParentView)
			return true;

		return isParentViewOfView(realParentView.getID(), parentViewID);

	}

	/**
	 * Adds a {@link PickingMode#MOUSE_OUT} for all Objects that have previously picked in the specified views.
	 *
	 * @param viewIDs
	 * @param glMouseListener
	 * @param pickedPoint
	 */
	private void addMouseOutForPicksOfSpecifiedViews(Set<Integer> viewIDs, GLMouseListener glMouseListener,
			Vec2f pickedDIPPoint) {

		for (Integer viewID : viewIDs) {

			ViewSpecificHitListContainer previousHitContainer = hashViewIDToPreviousViewSpecificHitListContainer
					.get(viewID);

			if (previousHitContainer != null) {

				for (String pickingType : previousHitContainer.getPickingTypes()) {
					for (Pick previousPick : previousHitContainer.getPicksForPickingType(pickingType)) {

						ViewSpecificHitListContainer hitContainer = hashViewIDToViewSpecificHitListContainer
								.get(viewID);
						if (hitContainer == null) {
							hitContainer = new ViewSpecificHitListContainer();
							hashViewIDToViewSpecificHitListContainer.put(viewID, hitContainer);
						}
						Pick pick = new Pick(previousPick.getObjectID(), PickingMode.MOUSE_OUT, pickedDIPPoint,
								glMouseListener.getDIPPickedPointDragStart(), fMinimumZValue);
						hitContainer.addPicksForPickingType(pickingType, pick, true);
					}
				}
			}
			hashViewIDToPreviousViewSpecificHitListContainer.put(viewID, new ViewSpecificHitListContainer());
		}
	}

	/**
	 * Returns all picking types for a view that have been hit in the last cycle
	 *
	 * @param viewID
	 *            the ID of the view
	 * @return A set of als picking types hit in the previous cycle
	 */
	public Set<String> getHitTypes(int viewID) {
		if (hashViewIDToViewSpecificHitListContainer.get(viewID) == null)
			return null;
		return hashViewIDToViewSpecificHitListContainer.get(viewID).hashPickingTypeToPicks.keySet();
	}

	/**
	 * Returns the hits of the last cycle for a particular view and type
	 *
	 * @param viewID
	 *            the id of the view
	 * @param ePickingType
	 *            the type of the hits
	 * @return null if no Hits, else the ArrayList<Integer> with the hits
	 */
	public ArrayList<Pick> getHits(int viewID, String ePickingType) {

		if (hashViewIDToViewSpecificHitListContainer.get(viewID) == null)
			return null;
		// else
		return hashViewIDToViewSpecificHitListContainer.get(viewID).getPicksForPickingType(ePickingType);
	}

	/**
	 * Flush a particular hit list. This has to be done after every cycle.
	 *
	 * @param viewID
	 *            the id of the calling view
	 * @param ePickingType
	 *            the picking type determining which hits should be flushed
	 */
	public void flushHits(int viewID, String ePickingType) {

		if (hashViewIDToViewSpecificHitListContainer.get(viewID) != null) {
			hashViewIDToViewSpecificHitListContainer.get(viewID).getPicksForPickingType(ePickingType).clear();
		}
	}

	/**
	 * Removes all data associated with a view. You should do that when you close a view
	 *
	 * @param viewID
	 *            the id of the calling view
	 */
	public void removeViewSpecificData(int viewID) {
		hashViewIDToIsMouseOverPickingEvent.remove(viewID);
		hashViewIDToViewSpecificHitListContainer.remove(viewID);
		hashViewIDToPreviousViewSpecificHitListContainer.remove(viewID);
		ViewSpecificPickingIDContainer container = hashViewIDToViewSpecificPickingIDContainer.get(viewID);

		if (container != null && container.getAllPickingIDs() != null) {
			for (Integer pickingID : container.getAllPickingIDs()) {
				hashPickingIDToViewID.remove(pickingID);
			}
		}

		hashViewIDToViewSpecificPickingIDContainer.remove(viewID);
	}

	/**
	 * Returns the pickedObjectID (the id with which you initialized getPickingID()) when you provide the picking ID and
	 * the id of the view.
	 *
	 * @param viewID
	 *            the id of the view which has to be the same which was used when the picking id was created
	 * @param pickingID
	 *            the picking ID of which the pickedObjectID mapping is desired
	 * @return the pickedObjectID, null if no entry for that pickingID
	 */
	private Integer getPickedObjectIDFromPickingID(int viewID, String pickingType, int pickingID) {
		return hashViewIDToViewSpecificPickingIDContainer.get(viewID).getPickedObjectID(pickingType, pickingID);
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
	 * Extracts the nearest hit from the provided iArPickingBuffer Stores it internally Can process only one hit at at
	 * time at the moment
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
	 * Given the list of picking IDs which where hit it maps the picking ID to the view and stores it in the hit list
	 *
	 * @param alPickingIDs
	 * @param myMode
	 * @param pickedPoint
	 * @param dragStartPoint
	 */
	private void processPicks(ArrayList<Integer> alPickingIDs, PickingMode myMode, Vec2f pickedDIPPoint,
			Vec2f dragStartPoint) {

		HashMap<Integer, ViewSpecificHitListContainer> currentHitListContainers = new HashMap<Integer, ViewSpecificHitListContainer>();
		HashMap<Integer, HashMap<String, ArrayList<Pick>>> picksToAddToPreviousHitListContainers = new HashMap<Integer, HashMap<String, ArrayList<Pick>>>();

		for (int pickingID : alPickingIDs) {
			Pair<Integer, String> pickAssociatedValues = hashPickingIDToViewID.get(pickingID);
			String eType = pickAssociatedValues.getSecond();
			int viewIDToUse = pickAssociatedValues.getFirst();

			Pick pick = new Pick(getPickedObjectIDFromPickingID(viewIDToUse, eType, pickingID), myMode, pickedDIPPoint,
					dragStartPoint, fMinimumZValue);

			ViewSpecificHitListContainer hitContainer = hashViewIDToViewSpecificHitListContainer.get(viewIDToUse);
			if (hitContainer == null) {
				hitContainer = new ViewSpecificHitListContainer();
				hashViewIDToViewSpecificHitListContainer.put(viewIDToUse, hitContainer);
			}
			ViewSpecificHitListContainer previousHitContainer = hashViewIDToPreviousViewSpecificHitListContainer
					.get(viewIDToUse);

			boolean addPick = true;

			if (previousHitContainer != null) {
				ArrayList<Pick> previousPicks = previousHitContainer.getPicksForPickingType(eType);

				if (previousPicks != null) {
					for (Pick previousPick : previousPicks) {
						// Do not add the same Mouse_Over pick again (Mouse_Over
						// should only be called once)
						if (previousPick.getObjectID() == pick.getObjectID()
								&& pick.getPickingMode() == PickingMode.MOUSE_OVER) {
							addPick = false;
						}
					}
				}
			}
			if (addPick) {
				hitContainer.addPicksForPickingType(eType, pick, true);
			} else {
				// This Mouse_over pick must be remembered, but not called for
				// the next time if a mouse out
				// happens.
				HashMap<String, ArrayList<Pick>> typeSpecificPicks = picksToAddToPreviousHitListContainers
						.get(viewIDToUse);
				if (typeSpecificPicks == null) {
					typeSpecificPicks = new HashMap<String, ArrayList<Pick>>();
				}

				ArrayList<Pick> picks = typeSpecificPicks.get(eType);

				if (picks == null) {
					picks = new ArrayList<Pick>();
				}

				picks.add(pick);
				typeSpecificPicks.put(eType, picks);
				picksToAddToPreviousHitListContainers.put(viewIDToUse, typeSpecificPicks);

			}

			currentHitListContainers.put(viewIDToUse, hitContainer);
		}

		for (Integer viewID : currentHitListContainers.keySet()) {
			ViewSpecificHitListContainer hitContainer = currentHitListContainers.get(viewID);
			ViewSpecificHitListContainer copyWithoutMouseOut = hitContainer.copy();
			ViewSpecificHitListContainer previousHitContainer = hashViewIDToPreviousViewSpecificHitListContainer
					.get(viewID);
			if (previousHitContainer != null) {
				for (String pickingType : previousHitContainer.getPickingTypes()) {

					for (Pick previousPick : previousHitContainer.getPicksForPickingType(pickingType)) {

						ArrayList<Pick> currentPicks = hitContainer.getPicksForPickingType(pickingType);

						boolean isMouseOutOnPreviousPick = true;

						if (currentPicks != null) {
							for (Pick currentPick : currentPicks) {
								if (currentPick.getObjectID() == previousPick.getObjectID()) {
									isMouseOutOnPreviousPick = false;
									break;
								}
							}
						}

						HashMap<String, ArrayList<Pick>> typeSpecificPicks = picksToAddToPreviousHitListContainers
								.get(viewID);
						if (typeSpecificPicks != null) {

							ArrayList<Pick> picks = typeSpecificPicks.get(pickingType);

							if (picks != null) {
								for (Pick pick : picks) {
									// Some Mouse_over picks were not added to
									// the hitListContainer but
									// occurred, so no Mouse_Out should be
									// called.
									if (previousPick.getObjectID() == pick.getObjectID()) {

										isMouseOutOnPreviousPick = false;
										break;
									}
								}
							}
						}

						if (isMouseOutOnPreviousPick) {
							hitContainer.addPicksForPickingType(pickingType, new Pick(previousPick.getObjectID(),
									PickingMode.MOUSE_OUT, pickedDIPPoint, dragStartPoint, fMinimumZValue), false);
						}
					}

				}
			}

			// Add mouse over picks for the next run
			HashMap<String, ArrayList<Pick>> typeSpecificPicks = picksToAddToPreviousHitListContainers.get(viewID);
			if (typeSpecificPicks != null) {
				for (String pickingType : typeSpecificPicks.keySet()) {
					ArrayList<Pick> picks = typeSpecificPicks.get(pickingType);
					for (Pick pick : picks) {
						copyWithoutMouseOut.addPicksForPickingType(pickingType, pick, true);
					}
				}
			}

			hashViewIDToPreviousViewSpecificHitListContainer.put(viewID, copyWithoutMouseOut);
		}
	}
}
