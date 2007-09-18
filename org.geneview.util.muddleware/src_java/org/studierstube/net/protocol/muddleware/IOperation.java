package org.studierstube.net.protocol.muddleware;

import java.io.EOFException;
import java.util.Iterator;

public interface IOperation {

	/**
	 * Identification for Operation Header.
	 * 
	 * Note: compare Muddleware::common\Operation.h  HEADER_KEY = 0x0123cafe
	 */
	public static final int OPERATION_HEADER_KEY = 19122942;

	/**
	 * Size of Operation header in bytes
	 */
	public static final int SIZE_OF_OPERATION_HEADER = 20; //20 = 5*4 Bytes

	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public abstract byte[] parseByteArray(byte[] buffer) throws EOFException;

	/**
	 * String of all nodes.
	 * 
	 * @return all nodes in on string separated by " "
	 */
	public abstract Iterator <String> getNodeIterator();

	
	/**
	 * Creates a String containing all node data.
	 * Note: uses an Iterator internally and is not quick.
	 * 
	 * @return all nodes
	 */
	public abstract String getNodeString();

	/**
	 * Get identifier set by client.
	 * 
	 * @return identifier set by client
	 */
	public abstract int getClientData();

	/**
	 * Get identifier that is set by client.
	 * 
	 * @param iClientData id set by client
	 */
	public abstract void setClientData(int iClientData);

	/**
	 * Type of operation.
	 * 
	 * @return type of operation
	 * 
	 * @see org.studierstube.net.protocol.muddleware.OperationEnum#OP_ADD_ATTRIBUTE
	 * @see org.studierstube.net.protocol.muddleware.IOperation#setOperation(int)
	 * 
	 */
	public abstract OperationEnum getOperation();


	/**
	 * Set type of operation
	 * 
	 * @see org.studierstube.net.protocol.muddleware.OperationEnum#OP_ADD_ATTRIBUTE
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getOperation()
	 * 
	 * @param operation type of operation
	 * 
	 * @return TRUE if operation is a valid parameter
	 */
	public abstract boolean setOperation(OperationEnum operation);

	/**
	 * Get the xPath.
	 * 
	 * @return xPath
	 */
	public abstract String getXPath();

	/**
	 * Set the xPath
	 * 
	 * @param xPath set new xPath
	 */
	public abstract void setXPath(String xPath);

	/**
	 * Get total length of this Operation, if this Operation is converted into a byte[]
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#getByteLength()
	 * @see org.studierstube.net.protocol.muddleware.Message#createMessageByteArray()
	 * 
	 * @return number of bytes needed to convert this operation into a byte[]
	 */
	public abstract int getByteLength();

	/**
	 * Adds a String to the list of nodes.
	 * 
	 * @param nodeString String to be added as a node.
	 */
	public abstract void addNodeString(String nodeString);

	/**
	 * Remove all nodes.
	 *
	 */
	public abstract void removeAllNodes();

	/**
	 * Converts this header into a byte array.
	 * 
	 * @param insertIntoByteArray byte array to write to
	 * @param iStartIndex index to start from inside the byteArray
	 * @param iTotalLength total length of message, used for calculating restSize
	 * 
	 * @return index in byte array after insertion
	 */
	public abstract int createMessageByteArray(final int iStartIndex,
			byte[] insertIntoByteArray, final int iTotalLength);

	/**
	 * Get detailed information on message using different modes.
	 *
	 * @see org.studierstube.net.protocol.muddleware.Message#MESSAGE_STYLE_BRIEF
	 * @see org.studierstube.net.protocol.muddleware.Message#MESSAGE_STYLE_FULL
	 * @see org.studierstube.net.protocol.muddleware.Message#MESSAGE_STYLE_DEBUG
	 * 	 
	 * @see org.studierstube.net.protocol.muddleware.Message#toString(int)
	 * 
	 * @return String with brief description
	 */
	public abstract String toString(int iStyle);

}