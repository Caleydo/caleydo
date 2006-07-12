/**
 * 
 */
package org.studierstube.net.protocol.muddleware;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.InputStream;

import java.util.Iterator;
import java.util.Vector;

import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.Footer;
import org.studierstube.net.protocol.muddleware.Header;


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
/**
 * Massage send to Muddleware and received from Muddleware.
 * 
 * see Muddleware::common/Message.h
 * 
 * @author Michael Kalkusch
 *
 */
public class Message {


	/**
	 * Identification for Operation Header.
	 * 
	 * Note: compare Muddleware::common\Message.h    HEADER_KEY = 0xdeadbabe
	 */
	public static final int MESSAGE_HEADER_KEY = -559039810;
	
	/**
	 * Identification for Operation Header.
	 * 
	 * Note: compare Muddleware::common\Message.h    FOOTER_KEY = 0x00beaf00
	 */
	public static final int MESSAGE_FOOTER_KEY = -559039810;
	
	/**
	 * Identification for Operation Header.
	 * 
	 * Note: compare Muddleware::common\Message.h    INVALID_CLIENTID = 0xffffffff
	 */
	public static final int MESSAGE_INVALID_CLIENTID = Integer.MIN_VALUE;
	
	/**
	 *  Define output style for message.
	 *  All pieces of informtaion on a message.
	 *  
	 *  @see org.studierstube.net.protocol.muddleware.Message#toString(int)
	 */
	public static final int MESSAGE_STYLE_DEBUG = 0;
	
	/**
	 *  Define output style for message. ´
	 *  Full info with out debug info.
	 *  
	 *  @see org.studierstube.net.protocol.muddleware.Message#toString(int)
	 */
	public static final int MESSAGE_STYLE_FULL  = 1;
	
	/**
	 * Define output style for message. 
	 * Brief info only.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#toString(int)
	 */
	public static final int MESSAGE_STYLE_BRIEF = 2;
	
	/**
	 * Header of this message.
	 * 
	 * Note: Footer.id must be the same as Header.id
	 */
	protected Header header;
	
	/**
	 * Footer of this message.
	 * 
	 * Note: Footer.id must be the same as Header.id
	 */
	protected Footer footer;
	
	/**
	 * 
	 */
//	protected int id;
//	
//	protected int numOperations;
//	
//	protected int restSize; 
	
//	protected String message;
	
	/**
	 * Vector wiht all Operation stored in this message.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#addOperation(Operation)
	 * @see org.studierstube.net.protocol.muddleware.Message#getOperation(int)
	 * @see org.studierstube.net.protocol.muddleware.Message#getNumOperations()
	 */
	protected Vector <Operation> vectorOperation;
	
	/**
	 * Default constructor creates empty Message without any Operation inside.
	 */
	public Message() {
		vectorOperation = new Vector <Operation> ();
		
		 header = new Header();			
		 footer = new Footer();
	}
	
	/**
	 * Create a new Message and one Operation inside defined by operationType
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#Operation(int)
	 * 
	 * @param operationType define kind of Operation nesting inside Message
	 */
	public Message( int operationType ) {
		 header = new Header();			
		 footer = new Footer();
		 
		 Operation firstOp = new Operation( operationType );
		 
		 vectorOperation = new Vector <Operation> ();
		 vectorOperation.addElement( firstOp );
	}
	
	/**
	 * Creates a new Message by copying the old message.
	 * 
	 * @param cloneMessage Message to be cloned
	 */
	public Message( Message cloneMessage ) {
		vectorOperation = new Vector <Operation> ();
		
		 header = new Header( cloneMessage.getHeader() );			
		 footer = new Footer( cloneMessage.getFooter() );		 
		 
	}
	
	/**
	 * Set the id of this message.
	 * Id is set by client and used to identify a message.
	 * 
	 * Note: id is stored in header and footer and both id's sould be the same
	 * 
	 * @param id id for thsi message
	 */
	public final void setId( final int id ) {
		header.setId( id );
		footer.setId( id );
	}
	
	/**
	 * Get the id of this message.
	 * Id is set by client and used to identify a message.
	 * 
	 * Note: id is stored in header and footer and both id's sould be the same
	 * 
	 * @return if of this message
	 */
	public final int getId() {
		return header.getId();
	}
	
//	protected final void attatchMessageHeader( byte [] byteArray ) {
//		
//		assert byteArray != null : "parameter must not be null-pointer.";
//		
//		if ( byteArray.length < 6 ) {
//			throw new RuntimeException("createMessageHeader() failed, because length byte of array was to small.");
//		}
//		
//		byteArray[0] = (new Integer( Header.HEADER_KEY )).byteValue();
//		byteArray[1] = (new Integer( id )).byteValue();
//		byteArray[2] = (new Integer( numOperations )).byteValue();
//		byteArray[3] = (new Integer( restSize )).byteValue();
//	}
//	
//	protected final void attatchMessageFooter( byte [] byteArray ) {
//		assert byteArray != null : "parameter must not be null-pointer.";
//		
//		int iLength = byteArray.length;
//		
//		if ( iLength < 6 ) {
//			throw new RuntimeException("createMessageHeader() failed, because length byte of array was to small.");
//		}
//		
//		byteArray[iLength-2] = (new Integer( Header.HEADER_KEY )).byteValue();
//		byteArray[iLength-1] = (new Integer( Header.FOOTER_KEY )).byteValue();
//	}
	
	/**
	 * Number of bytes this Message requires.
	 * Note: calls Operation#getByteLength() recursivly for all nested Operations.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#vectorOperation
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#getByteLength()
	 * 
	 * @return number of bytes if message is converted into a byte[]
	 */
	public int getByteLength() {
		
		int iCurrentLength = Header.SIZE_OF_HEADER + Footer.SIZE_OF_FOOTER;
			
		Iterator <Operation> iter = this.vectorOperation.iterator();
		 
		 while ( iter.hasNext() ) {
			 iCurrentLength += iter.next().getByteLength();
		 }
		 
		 int iCurrentRestSize = iCurrentLength % 4;
		 if ( iCurrentRestSize != 0 ) {
			 iCurrentLength += 4 - iCurrentRestSize; 
		 }
		 
		 this.header.setRestSize( iCurrentLength - Header.SIZE_OF_HEADER);		 
		 
		return iCurrentLength;
	}
	
//	public final String getMessage() {
//		return message;
//	}
//	
//	public final void setMessage( String message ) {
//		this.message = message;
//	}
	
	/**
	 * Get the restSize in bytes
	 * 
	 * @return number of bytes remaining after header is read. 
	 */
	public final int getRestSize() {
		return header.getRestSize();
	}
	
	/**
	 * Retrun the Header of this Message.
	 * 
	 * @return header of message
	 */
	public final Header getHeader() {
		return this.header;
	}
	
	/**
	 * Retrun the Footer of this Message.
	 * 
	 * @return footer of message
	 */
	public final Footer getFooter() {
		return this.footer;
	}
	
	
	
//	public String createMessageString( int iId, int iNumOperations, int iRestSize ) {
//		String result = Integer.toString( Header.HEADER_KEY );
//			
//		result += " Id=" + iId;
//		result += " #op=" + iNumOperations;
//		result += " restSize=" + iRestSize;
//			
//		return result;
//	}
	
//	public final boolean setHeader( int id, int numOperations, int restSize ) {
//		this.header.setId( id );
//		this.header.set.numOperations = numOperations;
//		this.restSize = restSize;
//		
//		return true;
//	}
	
	/**
	 * Converts this Mesasge and its nested Operations into a byte[] .
	 * This will be sent to the Muddleware XML-Server.
	 * 
	 * @return Message and nested Operations as byte[]
	 */
	public byte[] createMessageByteArray() {

		int iLength = this.getByteLength();
		byte[] result = new byte[iLength];

		header.setNumOp(vectorOperation.size());

		int iIndex = header.createMessageByteArray(result);

		Iterator<Operation> iter = this.vectorOperation.iterator();

		while (iter.hasNext()) {
			iIndex = iter.next()
					.createMessageByteArray(iIndex, result, iLength);
		}

		iIndex = MessageBorders.fillRest(iIndex, result);

		footer.createMessageByteArray(iIndex, result);				

		return result;
	}

	/**
	 * Writes the byte[] of this Message to an OutputStream.
	 * 
	 * @param byteStream OutputStream to write the byte[] to
	 * @return String of the message as debug-info
	 */
	public final String createMessageByte( OutputStream byteStream ) {
			
		try {
			/* Message header.. */
			byteStream.write( this.createMessageByteArray() );			
			
		} catch (IOException ioe ) {
			return "FAILED to write: " + this.toString();
		}
		
		return this.toString();
	}
	
	public static String createMessageString( ObjectOutputStream outStream,
			int iId, 
			int numOperations, 
			int restSize,
			String message ) {
		String result = Integer.toString( Header.HEADER_KEY );
			
		result += " Id=" + iId;
		result += " #op=" + numOperations;
		result += " restSize=" + restSize;
			
		try {
			/* Message header.. */
			outStream.writeInt( Header.HEADER_KEY );
			outStream.writeInt( iId );
			outStream.writeInt( numOperations );
			outStream.writeInt( restSize + message.length() );
			
			/* Message data.. */
			outStream.writeObject( message );
			
			/* Message footer .. */
			outStream.writeInt( Header.FOOTER_KEY );
			outStream.writeInt( iId);
			
		} catch (IOException ioe ) {
			
		}
		
		return result;
	}

	/**
	 * Assings data to this Message and creates nested Operationy by parsing 
	 * a byte array from an InputStream.
	 * 
	 * @param in InputStream containing byte[] 
	 * @param errorHandler Interface to print debug and status information to
	 * @return TRUE if byte[] was valid, FALSE in case of an error.
	 * @throws EOFException if byte[] is cut of before end of byte[] is read or expected.
	 */
	public boolean parseByteArrayFromInStream( InputStream in, 
			ErrorMessageHandler errorHandler ) throws EOFException {
	
		try {
			byte[] inStreamHeaderByteArray = new byte[Header.SIZE_OF_HEADER];
			
			in.read(inStreamHeaderByteArray);	
			
			if ( parseHeaderOnly(inStreamHeaderByteArray) ) {
				byte[] inStreamWithoutHeaderByteArray = 
					new byte[ getRestSize() ];
				
				in.read(inStreamWithoutHeaderByteArray);
				
				if ( parseByteArray(inStreamWithoutHeaderByteArray) ) {
					
//					errorHandler.logMsg("\nMessage --> " + 
//							this.toString() ,true );
					
					return true;
					
				} else {
					errorHandler.logMsg("ERROR while parsing message content! " ,true );
					return false;
				}
				
			} else {
				errorHandler.logMsg("ERROR while parsing message header! " ,true );
				return false;
			}
			
		}catch (IOException ioe) {
			errorHandler.logMsg("ERROR while parsing message: " + ioe.toString() ,true );
			return false;
		}
	}
	
	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	  * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public boolean parseByteArray( final byte[] buffer ) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";
					
		try {
			
			byte[] restBuffer = buffer;
					
			//System.out.println( this.toString() );
			
			while ( restBuffer.length > 12 ) {
				
				Operation operation = new Operation();
				
				restBuffer = operation.parseByteArray( restBuffer );
				
				vectorOperation.addElement( operation );
			}
			
			footer.parseByteArray( restBuffer );
			
			/**
			 * Check integratey of message...
			 */
			
			if ( footer.getId() != header.getId() ) {
				assert false : "Header-Id [" + header.getId() + 
					"] and Footer-ID [" + footer.getId()+ "] are not equal!";
			
				return false;
			}
			
		} catch (EOFException eofe) {
			
			return false;
		}
		
		return true;
		
	}
	
	
	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public boolean parseHeaderOnly( byte[] buffer ) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";
		
		if ( ! vectorOperation.isEmpty() ) {
			vectorOperation.clear();
		}
		
		return header.parseHeaderByteArray( buffer );
	}
	
	/**
	 * Get Operation at index.
	 * @param nWhich Range [0 - getNumOperations()-1]
	 * 
	 * @return Operation
	 */
	public final Operation getOperation( int nWhich ) {
		return vectorOperation.get( nWhich );
	}
	
	/**
	 * Adds a new Operation to this Message.
	 * 
	 * @param nOperation add new Operation.
	 */
	public final void addOperation( Operation nOperation ) {
		vectorOperation.addElement( nOperation );
	}
	
	/**
	 * Sets a new Operation to this Message and removes all previouse Operations.
	 * 
	 * @param nOperation set new Operation.
	 */
	public final void setOperation( Operation nOperation ) {
		vectorOperation.clear();
		vectorOperation.addElement( nOperation );
	}
	
	/**
	 * Get the number of operations stored in this message
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#vectorOperation
	 * @see org.studierstube.net.protocol.muddleware.Message#addOperation(Operation)
	 * @see org.studierstube.net.protocol.muddleware.Message#getOperation(int)
	 * 
	 * @return number of operations strored in this message
	 */
	public final int getNumOperations() {
		return vectorOperation.size();
	}
	
	/**
	 * All details on this class and its nested Operations.
	 * 
	 * @return details as a String
	 */
	public String toString() {
		 String result = "(M: ";
		 
		 result += header.toString() + "\n";
		 
		 Iterator <Operation> iter = this.vectorOperation.iterator();
		 
		 while ( iter.hasNext() ) {
			 result += iter.next().toString() + "\n";
		 }
		 
		 result += footer.toString();
		 
		 return	result;
	}
	
	/**
	 * Get detailed information on message using different modes.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#MESSAGE_STYLE_BRIEF
	 * @see org.studierstube.net.protocol.muddleware.Message#MESSAGE_STYLE_FULL
	 * @see org.studierstube.net.protocol.muddleware.Message#MESSAGE_STYLE_DEBUG
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#toString(int)
	 * 
	 * @param iStyle define the style
	 * @return details on the message
	 */
	public String toString( int iStyle) {
		
		StringBuffer result = new StringBuffer();
		
		switch (iStyle) {
		
		case MESSAGE_STYLE_DEBUG:
			return this.toString();
			
		case MESSAGE_STYLE_FULL:
			
		case MESSAGE_STYLE_BRIEF:
			result.append("M: #");
			result.append( getId() );
			
			Iterator <Operation> iter = vectorOperation.iterator();
			 
			 while ( iter.hasNext() ) {
				 result.append( iter.next().toString(iStyle) + "\n");
			 }
			 break;
			 
		default:
			return "NO_STYLE " + this.toString();
		}
		
		return result.toString();
	}

}
