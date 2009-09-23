package org.caleydo.plex.dummy;

import java.net.InetAddress;
import java.net.UnknownHostException;

import Ice.Communicator;
import Ice.ObjectAdapter;

public class DeskothequeDummy {

	public static void main(String[] args) {
		DeskothequeDummy dummy = new DeskothequeDummy();
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
		String displayNum = "0";

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
}
