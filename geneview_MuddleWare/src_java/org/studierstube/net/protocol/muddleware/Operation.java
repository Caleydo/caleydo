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
public class Operation implements IOperation {

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
	protected OperationEnum operation = OperationEnum.OP_EMPTY;
	
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
	Vector <String> nodeVector;
	
	
	/**
	 * Creates empty operation with type OP_EMPTY
	 *
	 */
	public Operation() {
		nodeVector = new Vector <String>();
	}
	
	/**
	 * Create a new operation and defines the operation type
	 * 
	 * @param operationType operation type used for new object
	 */
	public Operation( final int operationType ) {		
		this();
		
		this.operation = OperationEnum.valueOfInt(operationType);	
	}
	
	/**
	 * Create a new operation and defines the operation type
	 * 
	 * @param operationType operation type used for new object
	 */
	public Operation( final OperationEnum operationType ) {		
		this();
		
		this.operation = operationType;	
	}
	
	/**
	 * Creates a new operation by cloning an existing one.
	 * 
	 * Note: does not copy key!
	 * 
	 * @param operation operation to clone
	 */
	public Operation( final IOperation operation) {
		this();
		
		this.operation = operation.getOperation();		
		this.setXPath( operation.getXPath() );
		
		Iterator <String> iter = operation.getNodeIterator();
		
		while ( iter.hasNext() ) {
			nodeVector.addElement( iter.next() );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#parseByteArray(byte[])
	 */
	public byte[] parseByteArray( byte[] buffer ) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";
		
		DataByteInputStream contentDetails = new DataByteInputStream( buffer );
		
		int iBytesParesed = 0;
		
		try {
			
			this.key        = contentDetails.readInt();
			this.operation  = OperationEnum.valueOfInt( contentDetails.readInt() );
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

	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getNodeIterator()
	 */
	public final Iterator  <String>  getNodeIterator() {
		return nodeVector.iterator();
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getNodeString()
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
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getClientData()
	 */
	public final int getClientData() {
		return this.clientData;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#setClientData(int)
	 */
	public final void setClientData( int iClientData ) {
		this.clientData = iClientData;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getOperation()
	 */
	public final OperationEnum getOperation() {
		return operation;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#setOperation(int)
	 */
	public final boolean setOperation( OperationEnum operation ) {
		
		if ( operation.isExecutableOperation()) {
			this.operation = operation;		
			return true;
		}
		
		assert false : "Unsupported operation type [" + operation + "]";
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getXPath()
	 */
	public final String getXPath() {
		return this.xPath;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#setXPath(Stringt)
	 */
	public final void setXPath( String xPath ) {
		this.xPath = xPath;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#getByteLength()
	 */
	public int getByteLength() {
		return SIZE_OF_OPERATION_HEADER + 
			xPath.length() + 1 + 
			getNodeLength();
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#addNodeString(Stringt)
	 */
	public final void addNodeString( String nodeString ) {
		if ( nodeString.length() > 0 ) {
			nodeVector.addElement( nodeString );
			numNodes++;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#removeAllNodes()
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
		int iSum = 0;
		
		Iterator <String> iter = nodeVector.iterator();
		while ( iter.hasNext() ) {			
			iSum += iter.next().length() + 1;
		}
		
		return iSum;
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#createMessageByteArray(int, byte[], int)
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
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, operation.getIndex() );
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
		
		iIndex = MessageBorders.fillRest( iIndex, insertIntoByteArray );
		
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
		result.append( operation.name() );
		
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
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IOperation#toString(int)
	 */
	public String toString( int iStyle) {
		
		StringBuffer result = new StringBuffer();	
		
		switch (iStyle) {
		
		case IMessage.MESSAGE_STYLE_DEBUG:
			return toString();
			
		case IMessage.MESSAGE_STYLE_FULL:
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
			
		case IMessage.MESSAGE_STYLE_BRIEF:
			result.append("[");
			result.append( operation.name() );
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
