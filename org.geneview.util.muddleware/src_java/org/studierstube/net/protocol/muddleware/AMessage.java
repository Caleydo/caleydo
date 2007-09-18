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
import java.io.ObjectOutputStream;

/**
 * Handle header and footer of a Message.
 * 
 * @author Michael Kalkusch
 *
 * @see org.studierstube.net.protocol.muddleware.IMessage
 */
public abstract class AMessage {

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

	protected AMessage() {
		header = new Header();			
		footer = new Footer();
	}

	protected AMessage(IMessage cloneMessage) {
		 header = new Header( cloneMessage.getHeader() );			
		 footer = new Footer( cloneMessage.getFooter() );		 
	}
	
	public static String writeMessageToStreamGetString(ObjectOutputStream outStream, 
			final int iId, 
			final int numOperations, 
			final int restSize, 
			final String message) {
		
		writeMessageToStream(outStream,
				iId,
				numOperations,
				restSize, 
				message);
		
		StringBuffer buffer = new StringBuffer(Integer.toString( Header.HEADER_KEY ));
		
		buffer.append(" Id=");
		buffer.append(iId);
		buffer.append(" #op=");
		buffer.append(numOperations);
		buffer.append(" restSize=");
		buffer.append(restSize);
		
		return buffer.toString();
	}
	
	public static void writeMessageToStream(ObjectOutputStream outStream, 
			final int iId, 
			final int numOperations, 
			final int restSize, 
			final String message) {
			
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
	}

	public final void setId(final int id) {
		header.setId( id );
		footer.setId( id );
	}

	public final int getId() {
		return header.getId();
	}

	public final int getRestSize() {
		return header.getRestSize();
	}

	public final Header getHeader() {
		return this.header;
	}

	public final Footer getFooter() {
		return this.footer;
	}
	
}