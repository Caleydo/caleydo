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

import java.lang.StringBuffer;
import java.net.SocketException;
import java.io.EOFException;
import java.io.IOException;
//import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.studierstube.net.protocol.ErrorMessageHandler;
import org.studierstube.net.protocol.muddleware.Footer;
import org.studierstube.net.protocol.muddleware.Header;
import org.studierstube.util.StudierstubeException;

/**
 * Massage send to Muddleware and received from Muddleware.
 * 
 * see Muddleware::common/Message.h
 * 
 * @author Michael Kalkusch
 *
 */
public class Message
extends AMessage 
implements IMessage {


	/**
	 * Vector with all Operation stored in this message.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.IMessage#addOperation(IOperation)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getOperation(int)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getNumOperations()
	 */
	protected Vector <IOperation> vectorOperation; 
	
	/**
	 * Default constructor creates empty Message without any Operation inside.
	 */
	public Message() {
		
		super();
		
		vectorOperation = new Vector <IOperation> ();
	}
	
	/**
	 * Create a new Message and one Operation inside defined by operationType
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#Operation(int)
	 * 
	 * @param operationType define kind of Operation nesting inside Message
	 */
	public Message( int operationType ) {
		
		this();
		 
		vectorOperation.addElement( new Operation( operationType ));
	}
	
	/**
	 * Create a new Message and one Operation inside defined by operationType
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Operation#Operation(int)
	 * 
	 * @param operationType define kind of Operation nesting inside Message
	 */
	public Message( OperationEnum operationType ) {
		
		this();
		 
		vectorOperation.addElement( new Operation( operationType ));
	}
	
	/**
	 * Creates a new Message by copying the old message.
	 * 
	 * @param cloneMessage Message to be cloned
	 */
	public Message( IMessage cloneMessage ) {
		
		super( cloneMessage );
		
		vectorOperation = new Vector <IOperation> ();
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getByteLength()
	 */
	public int getByteLength() {
		
		int iCurrentLength = Header.SIZE_OF_HEADER + Footer.SIZE_OF_FOOTER;
			
		Iterator <IOperation> iter = this.vectorOperation.iterator();
		 
		 while ( iter.hasNext() ) {
			 iCurrentLength += ( iter.next()).getByteLength();
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
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#createMessageByteArray()
	 */
	public byte[] createMessageByteArray() {

		int iLength = this.getByteLength();
		byte[] result = new byte[iLength];

		header.setNumOp(vectorOperation.size());

		int iIndex = header.createMessageByteArray(result);

		Iterator <IOperation> iter = this.vectorOperation.iterator();

		while (iter.hasNext()) {	
			iIndex = (iter.next()).createMessageByteArray(iIndex, result, iLength);
		}

		iIndex = MessageBorders.fillRest(iIndex, result);

		footer.createMessageByteArray(iIndex, result);				

		return result;
	}

	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#createMessageByte(java.io.OutputStream)
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
	
	public boolean parseByteArray(final byte[] buffer) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";		
		
		try {
			
			byte[] restBuffer = buffer;
					
			//System.out.println( this.toString() );
			
			while ( restBuffer.length > 12 ) {
				
				IOperation operation = new Operation();
				
				restBuffer = operation.parseByteArray( restBuffer );
				
				vectorOperation.addElement( operation );
			}
			
			footer.parseByteArray( restBuffer );
			
			/**
			 * Check integratey of message...
			 */
			
			if ( footer.getId() != header.getId() ) {
				assert false : "Header-Id [" + header.getId() + "] and Footer-ID [" + footer.getId() + "] are not equal!";
	
				return false;
			}
			
		} catch (EOFException eofe) {
			
			return false;
		}
		
		return true;
		
	}
	
	public boolean parseHeaderOnly(byte[] buffer) throws EOFException {
		 
		assert buffer != null : "Can not handle null-pointer";
		
		if ( ! vectorOperation.isEmpty() ) {
			vectorOperation.clear();
		}
		
		return header.parseHeaderByteArray( buffer );
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseByteArrayFromInStream(java.io.InputStream, org.studierstube.net.protocol.ErrorMessageHandler)
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
					errorHandler.logMsg(this, "ERROR while parsing message content! ");
					return false;
				}
				
			} else {
				errorHandler.logMsg(this, "ERROR while parsing message header! ");
				return false;
			}
			
		} catch (SocketException se) {
			throw new StudierstubeException("Error while reading MEssage from Socket, probably connection to server was lost!");
		}catch (IOException ioe) {
			errorHandler.logMsg(this, "ERROR while parsing message: " + ioe.toString());
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getOperation(int)
	 */
	public final IOperation getOperation( int nWhich ) {	
		return vectorOperation.get( nWhich );
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#addOperation(org.studierstube.net.protocol.muddleware.Operation)
	 */
	public final void addOperation( IOperation nOperation ) {
		vectorOperation.addElement( nOperation );
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#setOperation(org.studierstube.net.protocol.muddleware.Operation)
	 */
	public final void setOperation( IOperation nOperation ) {
		vectorOperation.clear();
		vectorOperation.addElement( nOperation );
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getNumOperations()
	 */
	public final int getNumOperations() {
		return vectorOperation.size();
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("(M: ");
		 
		buffer.append(header.toString());
		
		Iterator <IOperation> iter = this.vectorOperation.iterator();
		 
		if  (iter.hasNext()) {
			buffer.append("\n");
		}
		else
		{
			buffer.append("no operations\n");
		}
		
		 while ( iter.hasNext() ) {
			 buffer.append(iter.next().toString());
			 buffer.append("\n");
		 }
		 
		 buffer.append(footer.toString());
		 
		 return	buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#toString(int)
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
			result.append(" ");
			
			Iterator <IOperation> iter = this.vectorOperation.iterator();

			while (iter.hasNext()) {						
				 //java1.3>>
				 result.append( (iter.next()).toString(iStyle));	
				 
				 if  (iter.hasNext()) {
					 result.append("\n");
				 }
			}
			break;
			 
		default:
			return "NO_STYLE " + this.toString();
		}
		
		return result.toString();
	}

	/**
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getOperationIterator()
	 */
	public Iterator <IOperation> getOperationIterator() {
		return vectorOperation.iterator();
	}

}
