/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.vislink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.ClearSelectionsEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.execution.ADisplayLoopEventHandler;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Werner Puff
 * @author Marc Streit
 * @author Alexander Lex
 */
public class VisLinkManager
	extends ADisplayLoopEventHandler
	implements IViewCommandHandler, ISelectionUpdateHandler {

	private static VisLinkManager visLinkManager = null;

	// FIXME
	ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByType(
		"org.caleydo.datadomain.genetic");

	private Display display;

	private String appName = null;

	private String caleydoSelectionId;

	private SelectionRetriever selectionRetriever = null;
	private Thread selectionRetrieverThread = null;

	private EventPublisher eventPublisher;
	private VisLinkSelectionListener visLinkSelectionListener;
	private SelectionUpdateListener selectionUpdateListener;
	private ClearSelectionsListener clearSelectionsListener;

	public static VisLinkManager get() {
		if (visLinkManager == null) {
			visLinkManager = new VisLinkManager();
			visLinkManager.init();
		}
		return visLinkManager;
	}

	private void init() {
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();

		selectionRetriever = new SelectionRetriever();
		selectionRetriever.setVisLinkManager(this);
		selectionRetrieverThread = new Thread(selectionRetriever);
		selectionRetrieverThread.start();
	}

	public void dispose() {
		unregisterEventListeners();
		doVisdaemonRequest("unregister?name=" + appName);
		selectionRetriever.setStopped(true);
		visLinkManager = null;
	}

	public void register(int x, int y, int w, int h, Display display) {
		this.display = display;
		String bbs = createBoundingBoxXML(x, y, w, h);
		bbs = urlEncode(bbs);

		if (appName == null) {
			appName = "caleydo-" + (new Date()).getTime();
		}

		String register = "register?name=" + appName + "&xml=" + bbs;
		doVisdaemonRequest(register);
	}

	private String createBoundingBoxXML(int x, int y, int w, int h) {
		return createBoundingBoxXML(x, y, w, h, null);
	}

	private String createBoundingBoxXML(int x, int y, int w, int h, Boolean source) {
		StringBuffer bb = new StringBuffer();
		bb.append("<boundingBox ");
		bb.append("x=\"" + x + "\" ");
		bb.append("y=\"" + y + "\" ");
		bb.append("width=\"" + w + "\" ");
		bb.append("height=\"" + h + "\" ");
		if (source != null) {
			bb.append("source=\"" + source.booleanValue() + "\" ");
		}
		bb.append("/>");
		return bb.toString();
	}

	public void handleVisLinkSelection(String selectionId) {
		System.out.println(this.getClass() + " got selectionId: " + selectionId);
		ClearSelectionsEvent cse = new ClearSelectionsEvent();
		cse.setSender(this);
		eventPublisher.triggerEvent(cse);

		GeneralManager.get().getViewManager().getConnectedElementRepresentationManager()
			.clearTransformedConnections();

		caleydoSelectionId = null;

		IDMappingManager idmm = dataDomain.getRecordIDMappingManager();
		int destId = 0;

		try {
			destId = idmm.getID(IDType.getIDType("UNSPECIFIED"), dataDomain.getRecordIDType(), selectionId);
		}
		catch (NullPointerException e) {
			HashSet<Integer> table =
				idmm.getID(IDType.getIDType("GENE_SYMBOL"), dataDomain.getRecordIDType(), selectionId);
			destId = (Integer) table.iterator().next();
		}
		SelectionDelta sd = new SelectionDelta(dataDomain.getRecordIDType());
		SelectionDeltaItem sdi = sd.addSelection(destId, SelectionType.MOUSE_OVER);
		sdi.addConnectionID(885);
		SelectionUpdateEvent sue = new SelectionUpdateEvent();
		sue.setDataDomainID(dataDomain.getDataDomainID());
		sue.setSelectionDelta(sd);
		sue.setSender(this);
		eventPublisher.triggerEvent(sue);

		// String bbl = "<boundingBoxList>";
		// bbl += createBoundingBoxXML(200, 200, 20, 20, false);
		// bbl += "</boundingBoxList>";
		// bbl = urlEncode(bbl);
		// doVisdaemonRequest("reportVisualLinks?name=" + appName + "&xml=" + bbl);
	}

	@Override
	public void handleClearSelections() {
		System.out.println("VisLinkManager: handleClearSelections");
		// doVisdaemonRequest("clearVisualLinks?name=" + appName);
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		System.out.println("VisLinkManager: handleSelectionUpdate");

		for (SelectionDeltaItem deltaItem : selectionDelta.getAllItems()) {
			if (deltaItem.getSelectionType() != GeneralRenderStyle.VISLINK_SELECTION_TYPE)
				continue;

			// caleydoSelectionId = deltaItem.getPrimaryID();

			IDMappingManager idmm = dataDomain.getRecordIDMappingManager();
			//
			caleydoSelectionId =
				idmm.getID(selectionDelta.getIDType(), IDType.getIDType("UNSPECIFIED"), deltaItem.getID());
			if (caleydoSelectionId == null) {
				java.util.Set<String> geneSymbols =
					idmm.getIDAsSet(selectionDelta.getIDType(), IDType.getIDType("GENE_SYMBOL"),
						deltaItem.getID());

				if (geneSymbols == null)
					continue;

				// FIXME: not safe to just take the first one
				caleydoSelectionId = (String) geneSymbols.toArray()[0];
			}
		}
	}

	private String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String doVisdaemonRequest(String urlCommand) {

		String urlString = "http://localhost:8080/visdaemon/" + urlCommand;
		StringBuffer responseBuf;

		try {
			// System.out.println("visdaemon request: " + urlString);
			URL registerURL = new URL(urlString);
			URLConnection registerCon = registerURL.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(registerCon.getInputStream()));

			responseBuf = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				responseBuf.append(line);
			}
			in.close();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return responseBuf.toString();
	}

	@Override
	public void run() {
		ConnectedElementRepresentationManager cerm =
			GeneralManager.get().getViewManager().getConnectedElementRepresentationManager();
		if (cerm.isNewCanvasVertices()) {
			final CanvasConnectionMap canvasConnectionMap =
				cerm.getCanvasConnectionsByType().get(dataDomain.getRecordIDType());
			if (canvasConnectionMap != null) {
				cerm.setNewCanvasVertices(false);
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						SelectionPoint2DList screenPoints = new SelectionPoint2DList();
						for (SelectionPoint2DList canvasPoints : canvasConnectionMap.values()) {
							screenPoints.addAll(canvasPointsToDisplay(canvasPoints));
						}
						String boundingBoxList = "<boundingBoxList>";
						for (SelectionPoint2D point : screenPoints) {
							boundingBoxList +=
								createBoundingBoxXML(point.getX(), point.getY(), 0, 0,
									caleydoSelectionId != null);
						}
						boundingBoxList += "</boundingBoxList>";
						System.out.println("new bounding box list = " + boundingBoxList);

						String requrl = null;
						if (caleydoSelectionId != null) {
							requrl = "selection?name=" + appName;
							requrl += "&id=" + urlEncode(caleydoSelectionId);
							requrl += "&xml=" + urlEncode(boundingBoxList);
						}
						else {
							requrl = "reportVisualLinks?name=" + appName;
							requrl += "&xml=" + urlEncode(boundingBoxList);
						}
						doVisdaemonRequest(requrl);
					}
				});
			}
		}
		processEvents();
	}

	/**
	 * Transforms the given list of selection vertices from canvas coordinates to display coordinates
	 * 
	 * @param canvasPoints
	 *            list of canvas-vertices of connection lines
	 * @return list of selection vertices in display coordinates
	 */
	private SelectionPoint2DList canvasPointsToDisplay(SelectionPoint2DList canvasPoints) {
		SelectionPoint2DList displayPoints = new SelectionPoint2DList();
		for (SelectionPoint2D p : canvasPoints) {
			ViewManager vm = GeneralManager.get().getViewManager();
			AGLView view = vm.getGLView(p.getViewID());
			Composite composite = view.getParentComposite();
			Point dp = composite.toDisplay(p.getPoint());
			SelectionPoint2D displayPoint = new SelectionPoint2D(appName, p.getViewID(), dp);
			displayPoints.add(displayPoint);
		}
		return displayPoints;
	}

	@Override
	public void registerEventListeners() {
		visLinkSelectionListener = new VisLinkSelectionListener();
		visLinkSelectionListener.setHandler(this);
		eventPublisher.addListener(VisLinkSelectionEvent.class, visLinkSelectionListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (visLinkSelectionListener != null) {
			eventPublisher.removeListener(visLinkSelectionListener);
			visLinkSelectionListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	public String getAppName() {
		return appName;
	}

}
