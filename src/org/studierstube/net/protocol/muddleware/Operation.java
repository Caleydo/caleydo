/* ========================================================================
 * Copyright (C) 2004-2005  Graz University of Technology
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For further information please contact Dieter Schmalstieg under
 * <schmalstieg@icg.tu-graz.ac.at> or write to Dieter Schmalstieg,
 * Graz University of Technology, Institut für Maschinelles Sehen und Darstellen,
 * Inffeldgasse 16a, 8010 Graz, Austria.
 * ========================================================================
 * PROJECT: Muddleware
 * ======================================================================== */

package org.studierstube.net.protocol.muddleware;

import java.io.IOException;
import java.io.EOFException;

import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

import org.studierstube.net.protocol.DataByteInputStream;
import org.studierstube.net.protocol.GeneralByteReader;
import org.studierstube.net.protocol.muddleware.Message;
//import org.studierstube.net.protocol.muddleware.MessageBorders;
import org.studierstube.net.protocol.muddleware.OperationEnum;


/**
 * Handling of a Muddleware operation and wrapper for Muddleware::common\Operation.h
 * 
 * See enum Muddleware::common\Operation.h
 * 
 * @author Michael Kalkusch
 *
 */
public class Operation {

	/**
	 * Identification for Operation Header.
	 * 
	 * Note: compare Muddleware::common\Operation.h  HEADER_KEY = 0x0123cafe
	 */
	public static final int OPERATION_HEADER_KEY = 19122942;
	
	/**
	 * Size of Operation header in bytes
	 */
	public static final int SIZE_OF_OPERATION_HEADER = 4*5;
	
	/**
	 *  Message ID for MUDDLEWARE::getElementAsString()
	 */
	public static final int OP_GET_ELEMENT = 0;

	/**
	 *  Message ID for MUDDLEWARE::addElementAsString()
	 */
	public static final int OP_ADD_ELEMENT = 1;

	/**
	 *  Message ID for MUDDLEWARE::addElementsAsStrings()
	 */
	public static final int OP_ADD_ELEMENTS = 2;

	/**
	 * Message ID for MUDDLEWARE::updateElement()
	 */
	public static final int OP_UPDATE_ELEMENT = 4;

	/**
	 * Message ID for MUDDLEWARE::removeElements()
	 */
	public static final int OP_REMOVE_ELEMENT = 3;

	/**
	 * Message ID for MUDDLEWARE::getElementAsString()
	 */
	public static final int OP_ELEMENT_EXISTS = 5;

	/**
	 *  Message ID for MUDDLEWARE::getAttributeAsString()
	 */
	public static final int OP_GET_ATTRIBUTE = 6;

	/**
	 * Message ID for MUDDLEWARE::updateAttribute()
	 */
	public static final int OP_UPDATE_ATTRIBUTE = 7;

	/**
	 * Adds a new attribute
	 */
	public static final int OP_ADD_ATTRIBUTE = 8;

	/**
	 * Removes an attribute
	 */
	public static final int OP_REMOVE_ATTRIBUTE = 9;

	/**
	 * Message ID for requesting the connections client ID
	 */
	public static final int OP_REQUEST_CLIENTID = 10;

	/**
	 * Message ID for registering an (non-callback) update-messages on modified nodes
	 */
	public static final int OP_REGISTER_WATCHDOG = 11;

	/**
	 * Message ID for unregistering a watchdog
	 */
	public static final int OP_UNREGISTER_WATCHDOG = 12;

	/**
	 * Message ID for watchdog reply - only set from server to client
	 */
	public static final int OP_WATCHDOG = 13;
	
	/**
	 * Message ID for doing nothing (can be used for getting only update-replies)
	 */
	public static final int OP_EMPTY = 14;
	
	/*
	 * Operation Header:
	 */
	
	/**
	 * 'magic number' that identified our message packets
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */
	protected int key = OPERATION_HEADER_KEY;
	
	/**
	 * Operation ID of the operation (see above)
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */
	protected int operation = OP_EMPTY;
	
	/**
	 * Operation ID of the operation (see above)
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */
	protected int restSize;
	
	
	/**
	 * Number of node strings that follow after the xpath
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */
	protected int numNodes;
	
	/**
	 *  Lets clients store a custom data to be sent and replied
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */
	protected int clientData = -1;
	
	/**
	 * Address the xPath.
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */
	protected String xPath = " ";
	
	/**
	 * Content data to be used in xPath.
	 * 
	 * Note: see Muddleware::common\Operation.h
	 */	
	protected Vector <String> nodeVector;
	
	
	/**
	 * Creates empty operation with type OP_EMPTY
	 *
	 */
	public Operation() {
		initDatastructures();
	}
	
	/**
	 * Create a new operation and defines the operation type
	 * 
	 * @param operationType operation type used for new object
	 */
	public Operation( final int operationType ) {		
		initDatastructures();
		
		this.operation = operationType;		
	}
	
	/**
	 * Creates a new operation by cloning an existing one.
	 * 
	 * Note: does not copy key!
	 * 
	 * @param operation operation to clone
	 */
	public Operation( final Operation operation) {
		initDatastructures();
		
		this.operation = operation.getOperation();		
		this.setXPath( operation.getXPath() );
		
		Iterator <String> iter = operation.getNodeIterator();
		
		while ( iter.hasNext() ) {
			nodeVector.addElement( iter.next() );
		}
	}
	
	/**
	 * Create needed internal data structues.
	 *
	 */
	private void initDatastructures() {
		nodeVector = new Vector <String> ();
	}
	
//	private void showInt( String text, int data ) {
//		System.out.println(" " + text + ": " + data);
//	}
	
	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public byte[] parseByteArray( byte[] buffer ) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";
		
		DataByteInputStream contentDetails = new DataByteInputStream( buffer );
		
		int iBytesParesed = 0;
		
		try {
			
			this.key        = contentDetails.readInt();
			this.operation  = contentDetails.readInt();
			this.restSize   = contentDetails.readInt();
			this.numNodes   = contentDetails.readInt();
			this.clientData = contentDetails.readInt();
			
			iBytesParesed = 5*4 + restSize;		
			
			String info = contentDetails.readString( restSize );				
			parseStringXPathAndAttributes( info );
			
			contentDetails.close();
			
		} catch ( IOException ioe) {
			
			try {
				contentDetails.close();
			} catch ( IOException ioe2) { }
			
			throw new EOFException( "error while parsing byte array. " + ioe.toString() );
			
		} 
		
		/*
		 * copy remaining bytes to new array.. 
		 */
		int iRemainingLength = buffer.length - iBytesParesed;		
		
		if ( iRemainingLength > 0 ) {
			byte[] returnByteArray = new byte[ iRemainingLength ];
			
			for ( int i=0; i < iRemainingLength; i++ ) {
				returnByteArray[i] = buffer[i+iBytesParesed];
			}
			
			return returnByteArray;
		}
		
		return new byte[0];
	}
	
	private boolean parseStringXPathAndAttributes( String sParse ) {
		
		final String sDelimiter = String.valueOf( (char) 0);
//		final String sDelimiterText = ";";
		
		StringTokenizer tokenizer = new StringTokenizer( sParse, sDelimiter );
		//StringBuffer strBuffer = new StringBuffer();
		
		if ( tokenizer.hasMoreElements() ) {
			xPath = tokenizer.nextToken();
		}
		
		nodeVector.clear();
		
//		boolean bNotFirstItemInString = false;
		
		while ( tokenizer.hasMoreTokens() ) {
			String current = tokenizer.nextToken();
						
			addNodeString( current );
			
//			if ( bNotFirstItemInString ) {
//				strBuffer.append( sDelimiterText );
//			} else {
//				bNotFirstItemInString = true;
//			}
//			strBuffer.append( current );
		}
		
		
		return true;
	}
	
//	public boolean parseByteArray_test( byte[] buffer ) {
//		
//		assert buffer != null : "Can not handle null-pointer";
//		
//		DataByteInputStream contentDetails = new DataByteInputStream( buffer );
//		
//		try {
//			showInt("OP  key", contentDetails.readInt() );
//			showInt("OP   op", contentDetails.readInt() );
//			showInt("OP rest", contentDetails.readInt() );
//			showInt("OP  num", contentDetails.readInt() );
//			showInt("OP sess", contentDetails.readInt() );
//			
//			contentDetails.close();
//		} catch ( IOException ioe) {
//			
//			try {
//				contentDetails.close();
//			} catch ( IOException ioe2) { }
//			
//			return false;
//		} 
//		
//		return true;
//	}

	/**
	 * String of all nodes.
	 * 
	 * @return all nodes in on string seperated by " "
	 */
	public Iterator <String> getNodeIterator() {
		return (Iterator <String>) nodeVector.iterator();
	}
	
	/**
	 * Creats a String containing all node data.
	 * Note: uses an Iterator internally and is not quick.
	 * 
	 * @return all nodes
	 */
	public String getNodeString() {
		StringBuffer result = new StringBuffer();
		
		Iterator <String> iter = this.nodeVector.iterator();
		
		while ( iter.hasNext() ) {
			result.append( iter.next() );	
			if ( iter.hasNext() ) {
				result.append( " " );
			}
		}
		
		return result.toString();
	}
	
	/**
	 * Get identifier set by client.
	 * 
	 * @return identifier set by client
	 */
	public final int getClientData() {
		return this.clientData;
	}
	
	/**
	 * Get identifier that is set by client.
	 * 
	 * @param iClientData id set by client
	 */
	public final void setClientData( int iClientData ) {
		this.clientData = iClientData;
	}
	
	/**
	 * Type of operation.
	 * 
	 * @return type of operation
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#OP_ADD_ATTRIBUTE
	 * @see org.studierstube.net.protocol.muddleware.Operation#setOperation(int)
	 * 
	 */
	public final int getOperation() {
		return this.operation;
	}
	
	/**
	 * Set type of operation
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#OP_ADD_ATTRIBUTE
	 * @see org.studierstube.net.protocol.muddleware.Operation#getOperation()
	 * 
	 * @param operation type of operation
	 * 
	 * @return TRUE if operation is a valid parameter
	 */
	public final boolean setOperation( int operation ) {
		if (( operation >= OP_GET_ELEMENT )&&(operation <= OP_EMPTY)) {
			this.operation = operation;
			
			return true;
		}
		
		assert false : "Unsupported operation type [" + operation + "]";
		
		return false;
	}
	
	/**
	 * Get the xPath.
	 * 
	 * @return xPath
	 */
	public final String getXPath() {
		return this.xPath;
	}
	
	/**
	 * Set the xPath
	 * 
	 * @param xPath set new xPath
	 */
	public final void setXPath( String xPath ) {
		this.xPath = xPath;
	}
	
	/**
	 * Get total length of this Operation, if thsi Operation is converted into a byte[]
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#getByteLength()
	 * @see org.studierstube.net.protocol.muddleware.Message#createMessageByteArray()
	 * 
	 * @return number of bytes neede to convert this operation int a byte[]
	 */
	public int getByteLength() {
		return SIZE_OF_OPERATION_HEADER + 
			xPath.length() + 1 + 
			getNodeLength();
	}
	
	/**
	 * Adds a String to the list of nodes.
	 * 
	 * @param nodeString String to be added as a node.
	 */
	public final void addNodeString( String nodeString ) {
		if ( nodeString.length() > 0 ) {
			nodeVector.addElement( nodeString );
			numNodes++;
		}
	}
	
	/**
	 * Remove all nodes.
	 *
	 */
	public final void removeAllNodes() {
		nodeVector.clear();
	}
	
//	public int lengthByte() {
//		return 5*4 + nodeString.length() + xPath.length() + nodeString.length();
//	}
	
//	public byte[] build() {
//		byte[] resultArray = new byte[ lengthByte() ];
//		
//		byte buffer = Byte.parseByte( Integer.toString(this.key) );		
//		resultArray[0] = buffer;
//		
//		buffer = Byte.parseByte( Integer.toString(this.operation) );
//		resultArray[1] = buffer;
//		
//		buffer = Byte.parseByte( Integer.toString(this.restSize) );
//		resultArray[2] = buffer;
//		
//		buffer = Byte.parseByte( Integer.toString(this.numNodes) );
//		resultArray[3] = buffer;
//		
//		buffer = Byte.parseByte( Integer.toString(this.clientData) );
//		resultArray[4] = buffer;
//		
//		buffer = Byte.parseByte( this.xPath );
//		resultArray[5] = buffer;
//		
//		buffer = Byte.parseByte( this.nodeString );
//		resultArray[5+ this.xPath.length() ] = buffer;
//		
//		return resultArray;
//	}
	
	/**
	 * calculate length of all node data.
	 * 
	 * @return length of node data
	 */
	private int getNodeLength() {
		Iterator <String> iter = nodeVector.iterator();
		int iSum = 0;
		
		while ( iter.hasNext() ) {			
			iSum += iter.next().length() + 1;
		}
		
		return iSum;
	}
	
	/**
	 * Convertes this header into a byte array.
	 * 
	 * @param insertIntoByteArray byte array to write to
	 * @param iStartIndex index to start from inside the byteArray
	 * @param iTotalLength total length of message, used for calculating restSize
	 * 
	 * @return index in byte array after insertion
	 */
	public int createMessageByteArray(  final int iStartIndex, byte[] insertIntoByteArray, final int iTotalLength ) {

		 int iIndex = iStartIndex;
		
		//System.out.print("  RESET restSize=[" + this.restSize + "] ==> [");
		
//		this.restSize = iTotalLength - (iStartIndex + 5*4); 
		
		restSize = xPath.length() + 1 + getNodeLength();
		
//		this.restSize = this.xPath.length()+1;
//		
//		System.out.println( this.restSize + "]");
		
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, key       );				
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, operation );
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, restSize  );
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, numNodes  );
		
		if ( clientData  < 0 ) {
			iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, Header.INVALID_CLIENTID );
		} else {
			iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, clientData);
		}
		
		iIndex = GeneralByteReader.insertString( iIndex, insertIntoByteArray, xPath+"\0" );
		
		if ( ! nodeVector.isEmpty() ) {
			Iterator <String> iter = this.nodeVector.iterator();
			
			while ( iter.hasNext() ) {
				iIndex = GeneralByteReader.insertString( iIndex, 
						insertIntoByteArray, 
						iter.next()+"\0" );				
			}
		}
//		if ( this.nodeString.length() > 0 ) {
//			iIndex = MessageBorders.insertString( iIndex, insertIntoByteArray, nodeString+"\0" );
//		}
		//iIndex = MessageBorders.fillRest( iIndex, insertIntoByteArray );
		
		return iIndex;			
	}
	
	public String toString() {
		
		StringBuffer result = new StringBuffer();
		
		result.append("(OP");
		result.append(" key=");
		
		if ( key == OPERATION_HEADER_KEY ) {
			result.append(" key=OP_HEADER");
		} else {
			result.append( key );
		}
		
		result.append(" op=");
		result.append( OperationEnum.getNameFromIndex( operation ) );
		
		result.append(" rest=");
		result.append( restSize );
		
		result.append(" num=");
		result.append(numNodes);
		
		result.append(" client=");
		result.append(clientData);
		
		result.append(" xPath=\"");
		result.append( xPath );
		result.append("\"");	
		
		Iterator <String> iter = this.nodeVector.iterator();
		
		for (int i=0; iter.hasNext(); i++ ) {
			result.append(" item");
			result.append(i);
			result.append("=");
			result.append( iter.next() );
			result.append( "\n" );
		}
			
//		result.append(" nodes=");
//		result.append( nodeString );
		
		result.append(")");
		
		return result.toString();
	}
	
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
	public String toString( int iStyle) {
		
		StringBuffer result = new StringBuffer();	
		
		switch (iStyle) {
		
		case Message.MESSAGE_STYLE_DEBUG:
			return toString();
			
		case Message.MESSAGE_STYLE_FULL:
			result.append("[OP");
			result.append(" k:");
			result.append(key);
			result.append(" o:");
			result.append(operation);
			result.append(" r:");
			result.append(restSize);
			result.append(" n:");
			result.append(numNodes);
			result.append(" c:");
			result.append(clientData);
			result.append(" x=");
			result.append(xPath);
			result.append(" nodes=");
			result.append( nodeVector.toString() );
			result.append("]");
			break;
			
		case Message.MESSAGE_STYLE_BRIEF:
			result.append("[");
			result.append( OperationEnum.getNameFromIndex(operation) );
			result.append(" n:");
			result.append(numNodes);
			result.append(" c:");
			result.append(clientData);
			result.append(" xPath=\"");
			result.append(xPath);
			
			if ( nodeVector.isEmpty() ) {
				result.append("\" nodes=empty ]");
			}
			else
			{
				result.append("\"\n");
				
				Iterator <String> iter = this.nodeVector.iterator();
				
				for (int i=0; iter.hasNext(); i++ ) {
					result.append(" item");
					result.append(i);
					result.append("=");
					result.append( iter.next() );
					if ( iter.hasNext() ) {
						result.append( " \n" );
					}
				}
				result.append( "] \n" );
			}
			
			break;
			
			default:
				return "NOSTYLE " + this.toString();
		}
		
		return result.toString();
	}
}
