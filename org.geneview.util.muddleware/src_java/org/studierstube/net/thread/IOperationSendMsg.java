package org.studierstube.net.thread;

import org.studierstube.net.protocol.muddleware.IMessage;
import org.studierstube.net.protocol.muddleware.IOperation;
import org.studierstube.net.thread.IOperationCallback;

public interface IOperationSendMsg {

	public void sendMessage(IMessage sendMsg, IOperationCallback callbackObject);
	
	public void sendOperation(IOperation sendOp, IOperationCallback callbackObject);

	

	
}
