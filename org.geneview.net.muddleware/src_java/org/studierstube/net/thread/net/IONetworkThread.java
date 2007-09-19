/**
 * 
 */
package org.studierstube.net.thread.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;

import org.studierstube.util.StudierstubeException;

/**
 * @author java
 *
 */
public class IONetworkThread implements Runnable, Cloneable {

	protected Thread workerThread = null;
	
	protected ServerSocket serverSocket = null;
	
	protected Socket dataSocket = null;
	
	private boolean bRunServer = false;
	
	private int iPortNumber = -1;
	
	/**
	 * 
	 */
	public IONetworkThread() {
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if ( serverSocket != null ) {
			
			while(bRunServer) {
				
				
				try {
					Socket socket = serverSocket.accept();
					IONetworkThread newSocket = (IONetworkThread) clone();
					
					newSocket.serverSocket = null;
					newSocket.dataSocket = socket;
					newSocket.workerThread = new Thread(newSocket);
						
				} catch (CloneNotSupportedException cnse) {
					cnse.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				
				
			}
		}
		else {
			run(dataSocket);
		}

	}
	
	public void run(Socket useDataSocket) {
		DataOutputStream outStream = null;
		DataInputStream inStream = null;
		
		try {
			outStream = new DataOutputStream(useDataSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			inStream = new DataInputStream(useDataSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			while ( true ) {
				byte b = inStream.readByte();
				
				System.out.println("R: " + b);
				outStream.writeBytes("TEST_ME");
				outStream.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public final synchronized void stopServer() {
		this.bRunServer = false;
		
		if ( workerThread != null ) {
			workerThread.interrupt();
		} else {
			throw new StudierstubeException("stopServer() try to stop server, that is not running!");
		}
	}
	
	protected final synchronized boolean isServerRunning() {
		return this.bRunServer;
	}
	
	public final synchronized void setServerPort(final int portNumber) {
		iPortNumber = portNumber;
	}
	
	public final synchronized int getServerPort() {
		return iPortNumber;
	}
	
	public synchronized void startServer( int portNumber ) {
		if ( workerThread == null ) {
			setServerPort(portNumber);
			try {
				serverSocket = new ServerSocket(iPortNumber);
				
				workerThread = new Thread(this);
				workerThread.setName("IO-thread serverport=" + this.iPortNumber);
				bRunServer  = true;
				workerThread.start();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}else {
			
			if ( ! workerThread.isAlive() ) {
				workerThread.start();
			} else {
				throw new StudierstubeException("startServer() try to start server, that is already running!");
			}
		}
	}

}
