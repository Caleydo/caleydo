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

import org.studierstube.net.protocol.GeneralByteReader;
import org.studierstube.net.protocol.DataByteInputStream;
import org.studierstube.net.protocol.muddleware.MessageBorders;

/**
 * Footer for each Message.
 * 
 * See Muddleware::common\Message.Footer
 * 
 * @author Michael Kalkusch
 *
 */
public class Footer extends MessageBorders {
	
//	
//	protected int key;
//	
//	protected int id;
	
	/**
	 * 
	 */
	public Footer() {
		super( FOOTER_KEY );
	}
	
	/**
	 * Clone existing footer.
	 * 
	 * Note: does not copy key!
	 * 
	 * @param cloneFooter Footer to be cloned; must not be null!
	 */
	public Footer( final Footer cloneFooter ) {
		super( FOOTER_KEY,  cloneFooter.getId() );
	}
	
	/**
	 * Set id.
	 * 
	 * @param id set by client to identify message
	 */
	public Footer( final int id ) {
		super( FOOTER_KEY,  id );
	}

	public byte[] parseByteArray( final byte[] buffer ) throws EOFException {
		return parseByteArray( buffer, true );
	}
	
	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * @param readEnd TRUE assumes footer to be at the end of the byte array, FLASE reads footer from first position in byte array
	 * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public byte[] parseByteArray( byte[] buffer, boolean readEnd ) throws EOFException {
		
		assert buffer != null : "Can not handle null-pointer";
		
		byte[] bufferTail;
		int iBufferOffset = buffer.length - SIZE_OF_FOOTER;
		
		
		if ( readEnd ) {
			bufferTail = new byte[SIZE_OF_FOOTER];
			
			/*
			 * copy end of buffer array to new array..
			 */
			for ( int i=0; i<SIZE_OF_FOOTER; i++) {
				bufferTail[i] = buffer[i+iBufferOffset];
			}
		} else {
			bufferTail = buffer;
		}
		DataByteInputStream contentDetails = new DataByteInputStream( bufferTail );
		
		try {
			
			this.key = contentDetails.readInt();
			this.id  = contentDetails.readInt();
			
			contentDetails.close();
		} catch ( IOException ioe) {
			
			try {
				contentDetails.close();
			} catch ( IOException ioe2) { }
			
			throw new EOFException( "error while parsing byte array. " + ioe.toString() );
			
		} 
		
		//System.out.println( this.toString() );
		
		return new byte[0];
	}
	
	public String toString() {
		String result ="(F:";
		
		if ( this.key != FOOTER_KEY ) {
			result += " key=" + this.key;
		} else {
			result += " key=FOOTER_KEY";
		}
		
		result += " id=" + this.id;
		
//		result += " HEAD=" + HEADER_KEY;
//		
//		result += " FOOTER=" + FOOTER_KEY;
//		
//		result += " INVALID_CLIENT=" + INVALID_CLIENTID;
		
		result += " )";
		
		return result;
	}
	
	/**
	 * Convertes this header into a byte array.
	 * 
	 * @param insertIntoByteArray byte array to write to
	 * @return index in byte array after insertion
	 */
	public int  createMessageByteArray( int iStartIndex, byte[] insertIntoByteArray ) {
	
		int iIndex = GeneralByteReader.toByteArrayMSB( iStartIndex, insertIntoByteArray, key );
		
		return GeneralByteReader.toByteArrayMSB( iIndex, insertIntoByteArray, id  );
	}
	
	/**
	 * Brief description of Operation data.
	 * 
	 * @return String with brief description
	 */
	public String toStringBrief() {
		String result ="(F";
		
		result += " k:" + this.key;
		result += " i:" + this.id;
		
		result += ")";
		
		return result;
	}
}
