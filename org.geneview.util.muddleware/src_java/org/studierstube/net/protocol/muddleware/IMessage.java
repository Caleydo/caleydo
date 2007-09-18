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

import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.studierstube.net.protocol.ErrorMessageHandler;

public interface IMessage {

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
	public static final int MESSAGE_STYLE_FULL = 1;

	/**
	 * Define output style for message. 
	 * Brief info only.
	 * 
	 * @see org.studierstube.net.protocol.muddleware.Message#toString(int)
	 */
	public static final int MESSAGE_STYLE_BRIEF = 2;

	/**
	 * Set the id of this message.
	 * Id is set by client and used to identify a message.
	 * 
	 * Note: id is stored in header and footer and both id's sould be the same
	 * 
	 * @param id id for thsi message
	 */
	public abstract void setId(final int id);

	/**
	 * Get the id of this message.
	 * Id is set by client and used to identify a message.
	 * 
	 * Note: id is stored in header and footer and both id's sould be the same
	 * 
	 * @return if of this message
	 */
	public abstract int getId();

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
	public abstract int getByteLength();

	/**
	 * Get the restSize in bytes
	 * 
	 * @return number of bytes remaining after header is read. 
	 */
	public abstract int getRestSize();

	/**
	 * Retrun the Header of this Message.
	 * 
	 * @return header of message
	 */
	public abstract Header getHeader();

	/**
	 * Retrun the Footer of this Message.
	 * 
	 * @return footer of message
	 */
	public abstract Footer getFooter();

	/**
	 * Converts this Mesasge and its nested Operations into a byte[] .
	 * This will be sent to the Muddleware XML-Server.
	 * 
	 * @return Message and nested Operations as byte[]
	 */
	public abstract byte[] createMessageByteArray();

	/**
	 * Writes the byte[] of this Message to an OutputStream.
	 * 
	 * @param byteStream OutputStream to write the byte[] to
	 * @return String of the message as debug-info
	 */
	public abstract String createMessageByte(OutputStream byteStream);

	/**
	 * Assings data to this Message and creates nested Operationy by parsing 
	 * a byte array from an InputStream.
	 * 
	 * @param in InputStream containing byte[] 
	 * @param errorHandler Interface to print debug and status information to
	 * @return TRUE if byte[] was valid, FALSE in case of an error.
	 * @throws EOFException if byte[] is cut of before end of byte[] is read or expected.
	 */
	public abstract boolean parseByteArrayFromInStream(InputStream in,
			ErrorMessageHandler errorHandler) throws EOFException;

	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public abstract boolean parseByteArray(final byte[] buffer)
			throws EOFException;

	/**
	 * Parse a byte array and create an operation from it, if possible.
	 * 
	 * @param buffer byte array used for parsing
	 * 
	 * @return byte array as part of the incoming byte array remained after parsing
	 */
	public abstract boolean parseHeaderOnly(byte[] buffer) throws EOFException;

	/**
	 * Get Operation at index.
	 * @param nWhich Range [0 - getNumOperations()-1]
	 * 
	 * @return Operation
	 */
	public abstract IOperation getOperation(int nWhich);

	/**
	 * Adds a new Operation to this Message.
	 * 
	 * @param nOperation add new Operation.
	 */
	public abstract void addOperation(IOperation nOperation);

	/**
	 * Sets a new Operation to this Message and removes all previouse Operations.
	 * 
	 * @param nOperation set new Operation.
	 */
	public abstract void setOperation(IOperation nOperation);

	/**
	 * Get the number of operations stored in this message
	 * 
	 * @see org.studierstube.net.protocol.muddleware.IMessage#addOperation(IOperation)
	 * @see org.studierstube.net.protocol.muddleware.IMessage#getOperation(int)
	 * 
	 * @return number of operations strored in this message
	 */
	public abstract int getNumOperations();
	
	/**
	 * Create Iterator containing Operations.
	 * 
	 * @return iterator with all operations in IMessage
	 */
	public abstract Iterator <IOperation> getOperationIterator();

	/**
	 * All details on this class and its nested Operations.
	 * 
	 * @return details as a String
	 */
	public abstract String toString();

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
	public abstract String toString(int iStyle);

}