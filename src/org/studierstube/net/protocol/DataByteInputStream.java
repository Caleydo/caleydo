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
package org.studierstube.net.protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;

import java.lang.StringBuffer;

/**
 * Helper class handle incomming byte data from c++ source via TCP.
 * 
 * Note: JAVA only
 * 
 * @author Michael Kalkusch
 *
 */
public class DataByteInputStream extends ByteArrayInputStream {

	static int index = 1;
	
	private DataInputStream dataInStream;
	
	/**
	 * @param byteArray use this array for parsing
	 */
	public DataByteInputStream(byte[] byteArray) {
		super(byteArray);
		
		dataInStream = new DataInputStream( (InputStream) this );
		
	}

//	/**
//	 * @param arg0
//	 * @param arg1
//	 * @param arg2
//	 */
//	public DataByteInputStream(byte[] byteArray, int arg1, int arg2) {
//		super(byteArray, arg1, arg2);
//		// TODO Auto-generated constructor stub
//	}

	public final int readInt() throws IOException {
		try {
			return Integer.reverseBytes( dataInStream.readInt() );
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
	public final float readFloat() throws IOException {
		try {
			return Float.intBitsToFloat( Integer.reverseBytes( dataInStream.readInt() ) );
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
	public final int readIntInverse() throws IOException {
		try {
			return Integer.numberOfLeadingZeros( dataInStream.readInt() );
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
	public final long readLong() throws IOException {
		try {
			return dataInStream.readLong();
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
	public final double readDouble() throws IOException {
		try {
			return Double.longBitsToDouble( dataInStream.readLong() );
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
 
	
	public final char readChar() throws IOException {
		try {
			return (char) dataInStream.readByte();
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
	public final void skipBytes( final int length ) throws IOException {
		dataInStream.skipBytes(length);		
	}
	
	public final String readString( final int length ) throws IOException {
		StringBuffer stb = new StringBuffer(length);
				
		try {
			
			byte buffer;
			
			boolean bCutTail = false;
			
			for ( int i=0; i<length;i++) {
				buffer = dataInStream.readByte();
//				data = dataInStream.readChar();
			
				
				if ( i >= length-3 ) {
					if ( buffer == (byte) 119 ) {
						/* skip this char */
						bCutTail = true;
						//System.out.println(" " + i + ": " + (char) buffer  + "  -->" + (int) buffer + "  <== SKIP!");						
					} 
					else {
						if ( ! bCutTail ) {
							/* insert this char*/
							stb.append( (char) buffer );
						}		
						/* else --> skip this char */
						
					}
				}
				else {
					stb.append( (char) buffer );
					//System.out.println(" " + i + ": " + (char) buffer  + "  -->" + (int) buffer );					
				}
//				
//				System.out.println(" " + i + ": " + (char) data  + "  -->" + (int) data );
//				stb.append( data );
			}
			
//			System.out.println("\n OUT: " + stb.toString());
			
			return stb.toString();
			
		} catch (EOFException eofe) {
			throw new IOException("EOF while parsing: "+ eofe.toString() );
		}
	}
	
	public void assumeStreamAsInteger() {
		
		int iLength = (int) (buf.length / 4);
		
		try {
			dataInStream.mark(0);
			
			int iBufferInt;
			
			for ( int i = 0; i < iLength; i++ ) {
				
				try {
					iBufferInt = readIntInverse();
					System.out.print( " " + i + "-> " + iBufferInt );
					
					int intBufferRevByte = Integer.reverseBytes(iBufferInt);
					System.out.print( " [revByte]->" + intBufferRevByte );
					System.out.print( " (unsigned int)->" + ((long) intBufferRevByte - (long) Integer.MIN_VALUE) );
					
					int intBufferRev = Integer.reverse(iBufferInt);
					System.out.print( " [rev]->" + intBufferRev );
					System.out.println( " (unsigned int)->" + ((long) intBufferRev - (long) Integer.MIN_VALUE) );
					
					
				} catch (IOException ioe) {
					System.out.println( " " + i + "-> [--]" );
				}				
			}
			
			System.out.println("\n  DONE");
			
			dataInStream.reset();
			
		} catch (IOException ioe) {
			System.out.println("\n  reset of Stream failed!");
		}
		
	}
	
//	public synchronized void reassignByteArray(byte[] byteArray) {
//		
//		dataInStream = null;
//		index = 1;
//		
//		dataInStream = new DataInputStream( (InputStream) this );
//	}
}
