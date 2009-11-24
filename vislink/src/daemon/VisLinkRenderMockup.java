package daemon;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Ice.Communicator;
import Ice.Current;
import Ice.ObjectAdapter;
import VIS.Selection;
import VIS.SelectionContainer;
import VIS.SelectionGroup;
import VIS._VisRendererIDisp;
import VIS.adapterName;
import VIS.adapterPort;

public class VisLinkRenderMockup extends _VisRendererIDisp implements Runnable {

	/**	generated serial version id */
	private static final long serialVersionUID = -4091627767767620876L;

	Display display = null;
	
	Shell shell = null;
	
	int fminx, fminy;
	
	Region region;
	
	public VisLinkRenderMockup() {
		// display = new Display();
	}

	public void drawVisualLinks(BoundingBoxList bbl) {
		region = calcRegion(bbl);
		drawRegion();
	}
	
	private void drawRegion() {
		// Display.getDefault().syncExec(this);
		run();
	}
	
	private Region calcRegion(BoundingBoxList bbl) {
		if (bbl.getList().size() == 0) {
			return null;
		}

		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		int centerx = 0;
		int centery = 0;
		for (BoundingBox bb : bbl.getList()) {
			centerx += bb.getX();
			centery += bb.getY();
			if (minx > bb.getX())
				minx = bb.getX();
			if (miny > bb.getY())
				miny = bb.getY();
		}
		fminx = minx;
		fminy = miny;
		centerx = centerx / bbl.getList().size();
		centery = centery / bbl.getList().size();

		System.out.println("vislink center: (" + centerx + ", " + centery + ")");
		
		Region region = new Region();
		for (BoundingBox bb : bbl.getList()) {
			System.out.println(" -> vislink to: (" + bb.getX() + ", " + bb.getY() + ")");
			int dx = bb.getX() - centerx;
			if (dx < 0)
				dx = -dx;
			int dy = bb.getY() - centery;
			if (dy < 0)
				dy = -dy;
			int ox = 4;
			int oy = 0;
			if (dx > dy) {
				ox = 0;
				oy = 4;
			}
			int[] coords = new int[] {
				centerx - minx, centery - miny,
				bb.getX() - minx, bb.getY() - miny, 
				bb.getX() + ox - minx, bb.getY() + oy - miny,
				centerx + ox - minx, centery + oy - miny
			};
			region.add(coords);
		}
		
		return region;
	}
	
	public void run() {
//		if (display == null) {
//			display = new Display();
//		}
		System.out.println("**** syncExec()");
		if (shell != null) {
			shell.dispose();
		}
		shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setBackground(display.getSystemColor(SWT.COLOR_RED));
		shell.setLocation(fminx, fminy);
		shell.setRegion(region);

		Rectangle size = region.getBounds();
		shell.setSize(size.width, size.height);
		shell.open();
	}

	public static void main(String[] args) {
		VisLinkRenderMockup dummy = new VisLinkRenderMockup();
		System.out.println("starting VisLinkRenderMockup dummy ...");
		dummy.execute();
	}

	public void execute() {
		Communicator communicator = Ice.Util.initialize();

		ServerInfo serverInfo = getServerInfo();

		// Ice.ObjectPrx proxy =
		// communicator.stringToProxy(serverInfo.serverName + ":" +
		// serverInfo.endPoint);
		ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
				adapterName.value, serverInfo.endPoint);

//		ServerApplication serverApplication = new ServerApplication();
//		serverApplication.setCommunicator(communicator);
//		serverApplication.setAdapter(adapter);

		adapter.add(this, communicator.stringToIdentity(serverInfo.serverName));
		adapter.activate();

		System.out.println("VisLinkRenderMockup running");
		communicator.waitForShutdown();
	}

	public void clearAll(Current current) {
		if (shell != null) {
			shell.dispose();
			shell = null;
		}
	}

	public void clearSelections(Current current) {
		clearAll(current);
	}

	public boolean registerSelectionContainer(SelectionContainer selectionContainer,
			Current current) {
		return true;
	}

	public void renderAllLinks(SelectionGroup[] selectionGroups, Current current) {
		BoundingBoxList bbl = new BoundingBoxList();
		for (SelectionGroup selectionGroup : selectionGroups) {
			for (Selection selection : selectionGroup.selections) {
				BoundingBox bb = new BoundingBox(selection.x, selection.y, selection.w, selection.h);
				bbl.add(bb);
			}
		}
		if (Display.getDefault() != null) {
			display = Display.getDefault();
		} else if (Display.getCurrent() != null) {
			display = Display.getCurrent();
		} else {
			display = new Display();
		}
		region = calcRegion(bbl);
		drawRegion();
	}

	public void renderLinks(SelectionGroup selectionGroup, Current current) {
		// TODO Auto-generated method stub
		System.out.println("renderLinks not implemented");
	}

	public void unregisterSelectionContainer(int selectionContainer, Current current) {
		// TODO Auto-generated method stub
		System.out.println("unregisterSelectionContainer not implemented");
	}

	public boolean updateSelectionContainer(SelectionContainer arg0,
			Current arg1) {
		System.out.println("updateSelectionContainer not implemented");
		return false;
	}

	public ServerInfo getServerInfo() {
		ServerInfo info = new ServerInfo();

		try {
			InetAddress addr = InetAddress.getLocalHost();
			info.hostName = addr.getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException("could not get hostname", e);
		}

		info.serverName = adapterName.value;
		info.endPoint = "tcp -h " + info.hostName + " -p " + adapterPort.value;
		return info;
	}

	public class ServerInfo {
		public String hostName;
		public String serverName;
		public String endPoint;
	}

}
