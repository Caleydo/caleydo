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
 * 
 */
package org.studierstube.net.protocol.muddleware;

import java.io.EOFException;
import java.io.IOException;

import org.studierstube.net.protocol.DataByteInputStream;
import org.studierstube.net.protocol.GeneralByteReader;
import org.studierstube.net.protocol.muddleware.MessageBorders;

/**
 * Header for each Message.
 * 
 * See Muddleware::common\Message.Header
 * 
 * @author Michael Kalkusch
 *
 */
public class Header extends MessageBorders {

	
	//public static final byte SKIP_BYTE = 0x77;
	
	/**
	 * Number of Operations.
	 * 
	 * see Muddleware::common/Message.Header
	 */
	protected int numOperations;
	
	/**
	 * Remainign bytes after reading header.
	 * 
	 * see Muddleware::common/Message.Header
	 */
	protected int restSize;
	
	/**
	 * Default constructor.
	 */
	public Header() {
		super( HEADER_KEY );
	}
	

	/**
	 * Cloens existing Header.
	 * 
	 * Note: does not copy key!
	 *  
	 * @param cloneHeader
	 */
	public Header( Header cloneHeader ) {			
		super( HEADER_KEY,  cloneHeader.getId() );
		
		assert cloneHeader != null : "Can not handle null-pointer";
		
		this.restSize = cloneHeader.getRestSize();
		this.numOperations = cloneHeader.getNumOp();
	}
	
	/**
	 * Get number of Operations.
	 * Note: must be set by Message and kept up to date by Message
	 * 
	 * @see org.studierstube.net.protocol.muddleware.IMessage#addOperation(IOperation)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getNumOperations()
	 * 
	 * @return number of operations
	 */
	public final int getNumOp() {
		return this.numOperations;
	}
	
	/**
	 * Set the nubber of Operations.
	 * Note: This method should ONLY be called during parsing a byte[] array 
	 * and creating a Message from it. See next 3 methods for details.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseHeaderOnly(byte[])
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseByteArray(byte[])
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseByteArrayFromInStream(InputStream, ErrorMessageHandler)
	 * 
	 * @param numOperations number of operations.
	 */
	protected final void setNumOp( int numOperations ) {
		this.numOperations = numOperations;
	}
	
	/**
	 * Number of remaining bytes to be read after reading header.
	 * 
	 * see Muddleware::common/Message.Header
	 * 
	 * @return remaining bytes after reading header
	 */
	public final int getRestSize() {
		return this.restSize;
	}
	
	/**
	 * Set the number of reaminign bytes.
	 * Note: This method should ONLY be called during parsing a byte[] array 
	 * and creating a Message from it. See next 3 methods for details.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseHeaderOnly(byte[])
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseByteArray(byte[])
	 * @see org.studierstube.net.protocol.muddleware.IMessage#parseByteArrayFromInStream(InputStream, ErrorMessageHandler)
	 * 
	 * @param restSize number of remaining bytes
	 */
	protected final void setRestSize( int restSize ) {
		this.restSize = restSize;
	}

	
	
	/**
	 * Convertes this header into a byte array.
	 * 
	 * @param insertIntoByteArray byte array to write to
	 * @return index in byte array after insertion
	 */
	public int createMessageByteArray( byte[] insertIntoByteArray) {
		
		int iIndex = GeneralByteReader.toByteArrayMSB( 0, insertIntoByteArray, key );
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, id );
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, numOperations );
		iIndex = GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, restSize );
		
		return iIndex;
	}
	
	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public byte[] parseByteArray( byte[] buffer ) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";
		
		DataByteInputStream contentDetails = new DataByteInputStream( buffer );
		
		try {
			
			this.key = contentDetails.readInt();
			this.id  = contentDetails.readInt();
			this.numOperations  = contentDetails.readInt();
			this.restSize  = contentDetails.readInt();
			
			contentDetails.close();
		} catch ( IOException ioe) {
			
			try {
				contentDetails.close();
			} catch ( IOException ioe2) { }
			
			throw new EOFException( "error while parsing byte array. " + ioe.toString() );
			
		} 
		
		System.out.println( this.toString() );
		
		int ICutByteArrayLength = buffer.length - SIZE_OF_HEADER;
		
		if ( ICutByteArrayLength > 0 ) {
			byte[] resultByteArray = new byte[ ICutByteArrayLength ];
			
			/*
			 * remove header from byteArray..
			 */
			for ( int i=0; i < ICutByteArrayLength ;i++ ) {			
				resultByteArray[i] = buffer[i + SIZE_OF_HEADER];
			}
			
			return resultByteArray;
		}
		else {
			return new byte[0];
		}
	}
	
	/**
	 * Parse a byte array and create an header from it, if possible.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#parseByteArray(byte[])
	 * 
	 * @param buffer byte array to be parsed
	 * @return TRUE if header could be created from byte[] 
	 */
	public boolean parseHeaderByteArray( byte[] buffer ) {
		
		assert buffer != null : "Can not handle null-pointer";
		
		DataByteInputStream contentDetails = new DataByteInputStream( buffer );
		
		try {			
			this.key = contentDetails.readInt();
			this.id  = contentDetails.readInt();
			this.numOperations  = contentDetails.readInt();
			this.restSize  = contentDetails.readInt();
			
			contentDetails.close();			
			//System.out.println( " parse_Header[" + this.toString() +"]" );
			
			return true;
		} catch ( IOException ioe) {
			
			try {
				contentDetails.close();
			} catch ( IOException ioe2) { }
			
			return false;		
		} 
	}
	
	public String toString() {
		String result ="(H:";
		
		if ( this.key != HEADER_KEY ) {
			result += " key=" + this.key;
		} else {
			result += " key=HEADER_KEY";
		}
		
		result += " id=" + this.id;
		result += " numOp=" + this.numOperations;
		result += " rest=" + this.restSize;
		
//		result += " HEAD=" + HEADER_KEY;
//		
//		result += " FOOTER=" + FOOTER_KEY;
//		
//		result += " INVALID_CLIENT=" + INVALID_CLIENTID;
		
		result += " )";
		
		return result;
	}

}
