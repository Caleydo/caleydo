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
 *  @author Michael Kalkusch
 *  
 */
package org.studierstube.net.protocol.muddleware;

import java.io.EOFException;
//import java.io.IOException;


/**
 * Abstract class for Header and Footer.
 * Reuse variables and methoeds for Header and Footer.
 * 
 * Note: JAVA only, optimize OOP structure.
 * 
 * 
 * @see org.studierstube.net.protocol.muddleware.Header
 * @see org.studierstube.net.protocol.muddleware.Footer
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class MessageBorders {

	/**
	 * Filler byte used to fill 32 bits if array of (char) is top short.
	 */
	public static final int FILLER_BYTE0 = 0x77;
	/**
	 * 'magic number' to identify Message Header
	 */
	public static final int HEADER_KEY= 0xdeadbabe;
	
	/**
	 *  'magic number' to identify Message Footer
	 */
	public static final int FOOTER_KEY= 0x00beaf00;
	
	/**
	 * tag for invalid client Id
	 */
	public static final int INVALID_CLIENTID = 0xffffffff;
	
	
	/**
	 * header contains 4 int values.
	 */
	public static final int SIZE_OF_HEADER = 16;
	
	/**
	 * footer contains two int values.
	 */
	public static final int SIZE_OF_FOOTER = 8;
	
	/**
	 * Idtentifier key for Header and Footer
	 * 
	 * see Muddleware::common/Message.h
	 * 
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#HEADER_KEY
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#FOOTER_KEY
	 */
	protected int key;
	
	/**
	 * Id set by the client to identify message.
	 * Server tags the answer this this id and the client knows, which answer belongs to wich question.
	 * 
	 * see Muddleware::common/Message.h
	 * 
	 * Note: Anyhow the answer is allways "42" anyway!
	 */
	protected int id;
	
	/**
	 * Set key and id.
	 * 
	 * @param key sould be HEADER_KEY or FOOTER_KEY
	 * @param id set by client to identify message
	 * 
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#HEADER_KEY
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#FOOTER_KEY	
	 */
	protected MessageBorders( final int key , final int id) {
		this.key = key;
		this.id = id;
	}
	
	/**
	 * Set key.
	 * 
	 * @param key key for this object
	 */
	protected MessageBorders( final int key ) {
		this.key = key;
	}
	

	
	/**
	 * Fill rest of 32 bits after inserting (char) into byte-array.
	 * Note: if iByteArrayOffset is already a divider of 4 this methode does not change anything 
	 * neither the byte[] nor the returned index is alterd from the iByteArrayOffset.
	 * 
	 * @param iByteArrayOffset current position in byte[]
	 * @param byteArray byte[] to write to
	 * 
	 * @return new position in index, which must now be a divider of 4
	 */
	public static int fillRest( final int iByteArrayOffset, byte[] byteArray ) {
		int iFillRestSize = iByteArrayOffset % 4;	
		
		if ( iFillRestSize != 0 ) {
			int iFillerData = FILLER_BYTE0;
			int iCurrentByteArrayOffset = iByteArrayOffset;
			
			for ( int i=0; i < 4 - iFillRestSize; i++) {
				byteArray[iCurrentByteArrayOffset] = (byte) (iFillerData++);
				iCurrentByteArrayOffset++;
			}

//			System.err.println("Filler bytes: " + Integer.toString(iFillRestSize));
			
			return iCurrentByteArrayOffset;
		}
		
//		System.err.println("no Filler bytes !");
		
		return iByteArrayOffset;
	}
	
	
	/**
	 * Get key for header or footer.
	 * 	
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#HEADER_KEY
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#FOOTER_KEY
	 * 
	 * @return key
	 */
	public final int getKey() {
		return this.key;
	}
	
	/**
	 * Set the
	 * @param key
	 */
	public final void setKey( int key ) {
		this.key = key;
	}
	
	/**
	 * Get id set by client to identify this message.
	 * 
	 * Note: a valid Message carries the id inside the header and the footer.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#getId()
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#setId(int)
	 * 
	 * @return id
	 */
	public final int getId() {
		return this.id;
	}
	
	/**
	 * Set the id used by the client.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#setId(int)	 
	 * @see org.studierstube.net.protocol.muddleware.MessageBorders#getId()
	 * 
	 * @param id
	 */
	public final void setId( int id) {
		this.id = id;
	}
	
	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public abstract byte[] parseByteArray( byte[] buffer ) throws EOFException;

}
