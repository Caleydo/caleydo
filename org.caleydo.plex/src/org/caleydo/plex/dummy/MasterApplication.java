package org.caleydo.plex.dummy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import DKT.ConnectionLineVertex;
import DKT.GroupwareClientAppIPrx;
import DKT.GroupwareInformation;
import DKT.ResourceManagerIPrx;
import DKT.ResourceManagerIPrxHelper;
import DKT.ServerApplicationIPrx;
import DKT._MasterApplicationIDisp;
import Ice.Communicator;
import Ice.Current;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;

public class MasterApplication extends _MasterApplicationIDisp {

	private static final long serialVersionUID = 5144216540429096784L;

	public static final int LINE_MODE_UP = 1;
	public static final int LINE_MODE_DOWN = 2;

	ResourceManager resourceManager = null;
	ResourceManagerIPrx resourceManagerPrx = null;

	ObjectAdapter adapter;

	Communicator communicator;

	Shell connectionLineShell = null;

	Display display = null;

	public MasterApplication() {
		display = new Display();
	}

	@Override
	public ResourceManagerIPrx getResourceManagerProxy(Current __current) {
		System.out
				.println("MasterApplication.getResourceManagerProxy() called");
		return resourceManagerPrx;
	}

	@Override
	public GroupwareInformation registerGroupwareClient(
			GroupwareClientAppIPrx client, String id,
			ServerApplicationIPrx serverApp, int x, int y, int w, int h,
			Current __current) {

		if (resourceManagerPrx == null) {
			resourceManager = new ResourceManager();
			resourceManager.setAdapter(adapter);
			resourceManager.setCommunicator(communicator);

			ObjectPrx objPrx = adapter.add(resourceManager, communicator
					.stringToIdentity("resourceManager"));
			resourceManagerPrx = ResourceManagerIPrxHelper.checkedCast(objPrx);
		}

		System.out
				.println("MasterApplication.registerGroupwareClient() called");
		client.dummy("hello, here is dummy_desko speaking");

		GroupwareInformation info = new GroupwareInformation();
		info.displayID = 0;
		info.isPrivate = true;
		info.deskoXID = resourceManager.createClientID();
		info.groupwareID = "groupwareID-123";

		return info;
	}

	@Override
	public void drawConnectionLine(ConnectionLineVertex[] vertices,
			int connectionID, Current __current) {

		Region region = new Region();

		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		for (ConnectionLineVertex v : vertices) {
			if (minx > v.x)
				minx = v.x;
			if (miny > v.y)
				miny = v.y;
		}

		ConnectionLineVertex s = null;
		for (ConnectionLineVertex v : vertices) {
			if (s == null) {
				s = v;
			} else {
				int dx = v.x - s.x;
				if (dx < 0)
					dx = -dx;
				int dy = v.y - s.y;
				if (dy < 0)
					dy = -dy;
				int ox = 4;
				int oy = 0;
				if (dx > dy) {
					ox = 0;
					oy = 4;
				}
				region.add(new int[]{s.x - minx, s.y - miny, v.x - minx,
						v.y - miny, v.x + ox - minx, v.y + oy - miny,
						s.x + ox - minx, s.y + oy - miny});
			}
		}

		if (connectionLineShell != null) {
			connectionLineShell.dispose();
			connectionLineShell = null;
		}
		connectionLineShell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
		connectionLineShell
				.setBackground(display.getSystemColor(SWT.COLOR_RED));
		connectionLineShell.setLocation(minx, miny);
		connectionLineShell.setRegion(region);

		Rectangle size = region.getBounds();
		connectionLineShell.setSize(size.width, size.height);

		connectionLineShell.open();
	}

	public ObjectAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ObjectAdapter adapter) {
		this.adapter = adapter;
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

}
