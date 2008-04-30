package org.caleydo.muddleware.callback;

import org.caleydo.core.manager.IXmlParserManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.thread.INetworkHanlderClientManager;
import org.studierstube.net.thread.IOperationCallback;
import org.xml.sax.InputSource;

/**
 * Callback triggered by incoming message from Muddleware server.
 * 
 * @author Michael Kalkusch
 * 
 */
public class MuddlewareMessageCallback implements IOperationCallback {

	/**
	 * Send received messages to IXmlParserManager
	 */
	private IXmlParserManager refXmlParserManager;
	
	private INetworkHanlderClientManager muddlewareIO_handler;
	
	public MuddlewareMessageCallback(final IXmlParserManager refXmlParserManager ) {
		assert refXmlParserManager != null : "Can not handle null pointer";
		
		this.refXmlParserManager = refXmlParserManager;
	}
	

	/**
	 * Callback from Muddleware server.
	 * 
	 * @see org.studierstube.net.thread.IOperationCallback#callbackOperationOnReceive(org.studierstube.net.protocol.muddleware.IOperation, org.studierstube.net.protocol.muddleware.IOperation)
	 */
	public void callbackOperationOnReceive(IOperation sent, IOperation received) {

		try
		{
//			this.refXmlParserManager.getManager().logMsg("MuddlewareMessageCallback.callbackOperationOnReceive() receiver Xpath=[" + 
//					received.getXPath() + 
//					" ==> ["  +
//					received.getNodeString() + "]",
//					LoggerType.VERBOSE );
			
			InputSource inSource = new InputSource( received.getNodeString() );
			refXmlParserManager.parseXmlFileByInputStream(inSource, received.getNodeString() );
			
//			String configuration = receiveMsg.getOperation( 0 ).getNodeString();						
//			InputSource inStream = new InputSource(new StringReader(configuration));	
			
			System.out.println("PARSE using Muddleware done.");
			
		} catch ( CaleydoRuntimeException cre)
		{
			
			System.err.println("ERROR while parsing XML message from Muddleware-server");
			cre.printStackTrace();
		}
		
		if ( muddlewareIO_handler != null ) {
			/**
			 * Stop muddleware IO handler..
			 */
			muddlewareIO_handler.stopManager();
		}
	}


	
	protected final INetworkHanlderClientManager getMuddlewareIO_handler() {
	
		return muddlewareIO_handler;
	}


	
	protected final void setMuddlewareIO_handler(
			INetworkHanlderClientManager muddlewareIo_handler) {
	
		this.muddlewareIO_handler = muddlewareIo_handler;
	}

}
