package org.caleydo.core.manager.view;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.selection.NewConnectionsEvent;
import org.caleydo.core.manager.view.listener.NewConnectionsListener;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.graphics.Point;

/**
 * Transforms and projects selection of standard planar views. The transformation is done by copying the
 * coordinates.
 * 
 * @author Werner Puff
 */
public class StandardTransformer
	implements ISelectionTransformer, IListenerOwner {

	/** reference for common usage */
	private EventPublisher eventPublisher;

	/** viewID of the view related to this {@link ISelectionTransformer} implementation */
	protected int viewID;

	/** <code>true</code> if all source points are transformed, <code>false</code> otherwise */
	protected boolean transformationFinished = false;

	private NewConnectionsListener newConnectionsListener;

	/**
	 * Creates a new instance for a related {@link AGLView} (view).
	 * 
	 * @param viewID
	 *            the viewID of the {@link AGLView} (view) to do transformations for
	 */
	public StandardTransformer(int viewID) {
		eventPublisher = GeneralManager.get().getEventPublisher();
		this.viewID = viewID;
		registerEventListeners();
	}

	@Override
	public void registerEventListeners() {
		newConnectionsListener = new NewConnectionsListener();
		newConnectionsListener.setHandler(this);
		eventPublisher.addListener(NewConnectionsEvent.class, newConnectionsListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (newConnectionsListener != null) {
			eventPublisher.removeListener(newConnectionsListener);
			newConnectionsListener = null;
		}
	}

	@Override
	public void project(GL gl, String deskoXID, HashMap<IDType, ConnectionMap> source,
		HashMap<IDType, CanvasConnectionMap> target) {
		final double mvmatrix[] = new double[16];
		final double projmatrix[] = new double[16];
		final int viewport[] = new int[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);

		final double[] wc = new double[4];
		final GLU glu = new GLU();

		ViewManager vm = GeneralManager.get().getViewGLCanvasManager();
		AGLView view = vm.getGLView(viewID);
		int canvasHeight = view.getParentGLCanvas().getHeight();

		for (Entry<IDType, ConnectionMap> typeConnections : source.entrySet()) {
			CanvasConnectionMap canvasConnectionMap = target.get(typeConnections.getKey());
			if (canvasConnectionMap == null) {
				canvasConnectionMap = new CanvasConnectionMap();
				target.put(typeConnections.getKey(), canvasConnectionMap);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections.getValue().entrySet()) {

				SelectionPoint2DList points2D = canvasConnectionMap.get(connections.getKey());
				if (points2D == null) {
					points2D = new SelectionPoint2DList();
					canvasConnectionMap.put(connections.getKey(), points2D);
				}

				for (SelectedElementRep sel : connections.getValue()) {
					if (sel.getRemoteViewID() == viewID) {
						for (Vec3f vec : sel.getPoints()) {
							glu.gluProject(vec.x(), vec.y(), vec.z(), mvmatrix, 0, projmatrix, 0, viewport,
								0, wc, 0);
							Point p = new Point((int) wc[0], canvasHeight - (int) wc[1]);
							SelectionPoint2D sp = new SelectionPoint2D(deskoXID, viewID, p);
							points2D.add(sp);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean transform(HashMap<IDType, ConnectionMap> source, HashMap<IDType, ConnectionMap> target) {

		if (transformationFinished) {
			return false;
		}
		transformationFinished = true;

		for (Entry<IDType, ConnectionMap> typeConnections : source.entrySet()) {

			ConnectionMap connectionMap = target.get(typeConnections.getKey());
			if (connectionMap == null) {
				connectionMap = new ConnectionMap();
				target.put(typeConnections.getKey(), connectionMap);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections.getValue().entrySet()) {

				SelectedElementRepList repList = connectionMap.get(connections.getKey());
				if (repList == null) {
					repList = new SelectedElementRepList();
					connectionMap.put(connections.getKey(), repList);
				}

				for (SelectedElementRep sel : connections.getValue()) {
					if (viewID == sel.getSourceViewID()) {
						ArrayList<Vec3f> transformedPoints = new ArrayList<Vec3f>();
						for (Vec3f vec : sel.getPoints()) {
							transformedPoints.add(vec);
						}
						SelectedElementRep trans =
							new SelectedElementRep(sel.getIDType(), sel.getSourceViewID(), viewID,
								transformedPoints);
						repList.add(trans);
					}
				}
			}
		}
		return true;
	}

	@Override
	public void destroy() {
		unregisterEventListeners();
	}

	@Override
	public void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	@Override
	public void handleNewConnections() {
		transformationFinished = false;
	}

}
