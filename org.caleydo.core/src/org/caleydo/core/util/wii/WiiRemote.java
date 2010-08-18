package org.caleydo.core.util.wii;

import java.util.LinkedList;
import java.util.Queue;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.graphics.Point;

import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.values.IRSource;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;

public class WiiRemote {
	private static float SMOOTH_RANGE = 40;

	private Queue<float[]> posInputQueue = new LinkedList<float[]>();
	private Queue<Float> distanceInputQueue = new LinkedList<Float>();

	private boolean bInitOK = false;

	/**
	 * Constructor.
	 */
	public WiiRemote() {
		float[] point = new float[2];
		point[0] = 0;
		point[1] = 0;

		// Initialize IR position input queue
		for (int i = 0; i < SMOOTH_RANGE; i++) {
			posInputQueue.add(point);
			distanceInputQueue.add(2f);
		}
	}

	public void connect() {
		Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, true);

		// Return if no Wii remote was detected. In this case the input is simulated with fixed values for
		// testing
		// purposes.
		if (wiimotes.length == 0)
			return;

		bInitOK = true;

		wiimotes[0].activateIRTRacking();
		wiimotes[0].addWiiMoteEventListeners(new WiimoteListener() {
			@Override
			public void onIrEvent(IREvent arg0) {
				if (arg0.getIRPoints().length != 2)
					return;

				IRSource[] irSource = arg0.getIRPoints();

				boolean cameraIsAboveScreen = true;
				float cameraVerticaleAngle = 0;
				float relativeVerticalAngle = 0;
				float headX = 0;
				float headY = 0;

				int m_dwWidth = 1680;
				int m_dwHeight = 1050;

				float dotDistanceInMM = 8.5f * 25.4f;// width of the wii sensor bar
				float radiansPerPixel = (float) (Math.PI / 4) / 1024.0f; // 45
				// degree
				// field
				// of
				// view
				// with
				// a
				// 1024x768
				// camera
				float movementScaling = 1.0f;

				float screenHeightinMM = 150;
				// float screenHeightinMM = 20 * 25.4f;

				Point firstPoint = new Point(irSource[0].getX(), irSource[0].getY());
				Point secondPoint = new Point(irSource[1].getX(), irSource[1].getY());

				// here all the head parameters are calculated in
				// ParseWiimoteData()------------------------------
				float dx = firstPoint.x - secondPoint.x;
				float dy = firstPoint.y - secondPoint.y;
				float pointDist = (float) Math.sqrt(dx * dx + dy * dy);

				float angle = radiansPerPixel * pointDist / 2;
				// in units of screen height since the box is a unit cube and
				// box height is 1
				float fHeadDistance =
					movementScaling * (float) (dotDistanceInMM / 2 / Math.tan(angle)) / screenHeightinMM;

				float avgX = (firstPoint.x + secondPoint.x) / 2.0f;
				float avgY = (firstPoint.y + secondPoint.y) / 2.0f;

				headX =
					(float) (movementScaling * Math.sin(radiansPerPixel * (avgX - m_dwWidth / 2f)) * fHeadDistance);

				relativeVerticalAngle = (avgY - m_dwHeight / 2f) * radiansPerPixel;// relative

				if (cameraIsAboveScreen) {
					headY =
						.5f + (float) (movementScaling
							* Math.sin(relativeVerticalAngle + cameraVerticaleAngle) * fHeadDistance);
				}
				else {
					headY =
						-.5f
							+ (float) (movementScaling
								* Math.sin(relativeVerticalAngle + cameraVerticaleAngle) * fHeadDistance);
				}

				// System.out.println("Distance: " + fHeadDistance);
				// System.out.println("Head position: " + headX + "/" + headY);
				// System.out.println("Head distance: " + fHeadDistance + "\n");

				float[] point = new float[2];
				point[0] = headX;
				point[1] = headY;
				posInputQueue.add(point);
				distanceInputQueue.add(fHeadDistance);

				if (posInputQueue.size() > SMOOTH_RANGE) {
					posInputQueue.remove();
					distanceInputQueue.remove();
				}
			}

			@Override
			public void onButtonsEvent(WiimoteButtonsEvent arg0) {
			}

			@Override
			public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0) {
			}

			@Override
			public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0) {
			}

			@Override
			public void onDisconnectionEvent(DisconnectionEvent arg0) {
			}

			@Override
			public void onExpansionEvent(ExpansionEvent arg0) {
			}

			@Override
			public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0) {
			}

			@Override
			public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0) {
			}

			@Override
			public void onMotionSensingEvent(MotionSensingEvent arg0) {
			}

			@Override
			public void onNunchukInsertedEvent(NunchukInsertedEvent arg0) {
			}

			@Override
			public void onNunchukRemovedEvent(NunchukRemovedEvent arg0) {
			}

			@Override
			public void onStatusEvent(StatusEvent arg0) {
			}

		});
	}

	public float[] getCurrentSmoothHeadPosition() {
		float[] fArTmpPoint;
		float[] fArSmoothedPoint = new float[] { -1.3f, 0.1f };

		if (!GeneralManager.get().isWiiModeActive() || !bInitOK)
			return fArSmoothedPoint;

		for (int i = 0; i < SMOOTH_RANGE; i++) {
			if (posInputQueue.size() < SMOOTH_RANGE) {
				break;
			}

			fArTmpPoint = ((LinkedList<float[]>) posInputQueue).get(i);
			fArSmoothedPoint[0] += fArTmpPoint[0];
			fArSmoothedPoint[1] += fArTmpPoint[1];
		}

		fArSmoothedPoint[0] /= SMOOTH_RANGE;
		fArSmoothedPoint[1] /= SMOOTH_RANGE;

		// System.out.println("Head position: " +fArSmoothedPoint[0] + " / " + fArSmoothedPoint[1]);

		return fArSmoothedPoint;
	}

	public float getCurrentHeadDistance() {
		float fSmoothedHeadDistance = 8;

		if (!GeneralManager.get().isWiiModeActive() || !bInitOK)
			return fSmoothedHeadDistance;

		for (int i = 0; i < SMOOTH_RANGE; i++) {
			fSmoothedHeadDistance += ((LinkedList<Float>) distanceInputQueue).get(i);
		}

		fSmoothedHeadDistance /= SMOOTH_RANGE;

		return fSmoothedHeadDistance;
	}
}
