package daemon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class VisLinkRenderMockup implements Runnable {

	Display display = null;
	
	Shell shell = null;
	
	int fminx, fminy;
	
	Region region;
	
	public VisLinkRenderMockup() {
		display = new Display();
	}

	public void drawVisualLinks(BoundingBoxList bbl) {
		if (bbl.getList().size() == 0) {
			return;
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

		region = new Region();
		for (BoundingBox bb : bbl.getList()) {
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
		
		System.out.println("init syncExec");
		Display.getCurrent().syncExec(this);
	}
	
	public void run() {
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
/*	
	public static void main(String[] args) {
		VisLinkRenderMockup dummy = new VisLinkRenderMockup();
		System.out.println("starting deskotheque dummy ...");
		dummy.run();
	}

	public void run() {
		Communicator communicator = Ice.Util.initialize();

		ServerInfo serverInfo = getServerInfo();

		// Ice.ObjectPrx proxy =
		// communicator.stringToProxy(serverInfo.serverName + ":" +
		// serverInfo.endPoint);
		ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
				"desko_dummy", serverInfo.endPoint);

		ServerApplication serverApplication = new ServerApplication();
		serverApplication.setCommunicator(communicator);
		serverApplication.setAdapter(adapter);

		adapter.add(serverApplication, communicator
				.stringToIdentity(serverInfo.serverName));
		adapter.activate();

		System.out.println("deskotheque dummy running");
		communicator.waitForShutdown();
	}

	public ServerInfo getServerInfo() {
		String displayNum = "1";

		ServerInfo info = new ServerInfo();

		try {
			InetAddress addr = InetAddress.getLocalHost();
			info.hostName = addr.getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException("could not get hostname", e);
		}

		info.serverName = "ServerAppI-" + info.hostName + "-" + displayNum;
		info.endPoint = "tcp -h " + info.hostName + " -p 8011";
		return info;
	}

	public class ServerInfo {
		public String hostName;
		public String serverName;
		public String endPoint;
	}
*/
}
