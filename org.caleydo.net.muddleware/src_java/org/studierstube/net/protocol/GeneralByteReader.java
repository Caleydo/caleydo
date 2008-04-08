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
package org.studierstube.net.protocol;

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
public class GeneralByteReader {

	
	/**
	 * Only static mehtodes and no constructor.
	 */
	private GeneralByteReader( ) {

	}

	
	protected byte[] toBytesMSB(final long n)
	{
		long n_shift = n;
		byte[] b = new byte[8];
		b[7] = (byte) (n);
		n_shift >>>= 8;
		b[6] = (byte) (n);
		n_shift >>>= 8;
		b[5] = (byte) (n);
		n_shift >>>= 8;
		b[4] = (byte) (n);
		n_shift >>>= 8;
		b[3] = (byte) (n);
		n_shift >>>= 8;
		b[2] = (byte) (n);
		n_shift >>>= 8;
		b[1] = (byte) (n);
		n_shift >>>= 8;
		b[0] = (byte) (n);
	
		return b;
	}
	
	protected byte[] toBytesLSB(long n)
	{
		byte[] b = new byte[8];
		b[0] = (byte) (n);
		n >>>= 8;
		b[1] = (byte) (n);
		n >>>= 8;
		b[2] = (byte) (n);
		n >>>= 8;
		b[3] = (byte) (n);
		n >>>= 8;
		b[4] = (byte) (n);
		n >>>= 8;
		b[5] = (byte) (n);
		n >>>= 8;
		b[6] = (byte) (n);
		n >>>= 8;
		b[7] = (byte) (n);
	
		return b;
	}
	
	protected byte[] toBytesMSB(int n)
	{
		byte[] b = new byte[4];
		b[3] = (byte) (n);
		n >>>= 8;
		b[2] = (byte) (n);
		n >>>= 8;
		b[1] = (byte) (n);
		n >>>= 8;
		b[0] = (byte) (n);
	
		return b;
	}
	
	protected byte[] toBytesLSB(int n)
	{
		byte[] b = new byte[4];
		b[0] = (byte) (n);
		n >>>= 8;
		b[1] = (byte) (n);
		n >>>= 8;
		b[2] = (byte) (n);
		n >>>= 8;
		b[3] = (byte) (n);
	
		return b;
	}

	
	/**
	 * Inserts a String into the byte[] and fills the remaining spaces to allign 32 bits.
	 * Note: calls fillRest() internal.
	 * 
	 * @param iByteArrayOffset current position in byte[]
	 * @param byteArray byte[] to write to
	 * @param sContent String to insert into byte[]
	 * 
	 * @throws EOFException if size of string exceeds the buffer size
	 * 
	 * @return new index in byte[]
	 */
	public static int insertString( final int iByteArrayOffset, byte[] byteArray, String sContent ) {
		
		//assert sContent != null : "Can not handle null-pointer";
		
		byte[] contentByteBuffer = sContent.getBytes();		
		int iIndexByteArray = iByteArrayOffset;
		int iContentLength = contentByteBuffer.length;
		
		
		/* Check range..  */
		if ( byteArray.length < iByteArrayOffset+iContentLength ) {
			throw new RuntimeException("byte array was to small to store String=" + sContent );
		}
		
		/* copy data */
		for ( int i=0; i < iContentLength; i++) {
			byteArray[iIndexByteArray] = contentByteBuffer[i];
			iIndexByteArray++;
		}
	
//		/*
//		 * Do we need to fill the remaining 32 bits? 
//		 */
//		int iFillRestSize = iContentLength % 4;
//		
//		if ( iFillRestSize != 0 ) {
//			int iFillerData = FILLER_BYTE0;
//			
//			for ( int i=0; i < 4 - iFillRestSize; i++) {
//				byteArray[iIndexByteArray] = (byte) (iFillerData++);
//				iIndexByteArray++;
//			}
//		}
		
		return iIndexByteArray;
	}
	
	/**
	 * Converts a int to byte[] and inserts it into a given byte[] array using an iByteArrayOffset
	 * Note: use MSB 
	 * 
	 * @param n interger value to be converted.
	 * @param byteArray byte[] to insert "n" to
	 * @param iByteArrayOffset offset inside byte[] 
	 * 
	 * @return new offset inside the byte array.
	 */
	public static int toByteArrayMSB( int iByteArrayOffset, byte[] byteArray, int n )
	{
		byteArray[iByteArrayOffset] = (byte) (n);
		n >>>= 8;
		byteArray[iByteArrayOffset+1] = (byte) (n);
		n >>>= 8;
		byteArray[iByteArrayOffset+2] = (byte) (n);
		n >>>= 8;
		byteArray[iByteArrayOffset+3] = (byte) (n);
	
		return iByteArrayOffset + 4;
	}
	
	public static int toByteArrayMSB( int iByteArrayOffset, byte[] byteArray, double n )
	{
		
		long buffer = (long) n;
		
		byteArray[iByteArrayOffset] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+1] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+2] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+3] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+4] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+5] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+6] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+7] = (byte) (buffer);
		
		return iByteArrayOffset + 8;
	}
	
	public static int toByteArrayMSB( int iByteArrayOffset, byte[] byteArray, float n )
	{
		
		int buffer = (int) n;
		
		byteArray[iByteArrayOffset] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+1] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+2] = (byte) (buffer);
		buffer >>>= 8;
		byteArray[iByteArrayOffset+3] = (byte) (buffer);
	
		return iByteArrayOffset + 4;
	}
	
	/**
	 * Converts a int to byte[] and inserts it into a given byte[] array using an iByteArrayOffset
	 * Note: use MSB 
	 * 
	 * @param n interger value to be converted.
	 * @param byteArray byte[] to insert "n" to
	 * @param iByteArrayOffset offset inside byte[] 
	 * 
	 * @return new offset inside the byte array.
	 */
	public static int toByteArrayMSB_8bit( int iByteArrayOffset, byte[] byteArray, int n )
	{
		byteArray[iByteArrayOffset] = (byte) (n);
	
		return iByteArrayOffset + 1;
	}
	
	/**
	 * Converts a int to byte[] and inserts it into a given byte[] array using an iByteArrayOffset
	 * Note: use LSB 
	 * 
	 * @param n interger value to be converted.
	 * @param byteArray byte[] to insert "n" to
	 * @param iByteArrayOffset offset inside byte[] 
	 * 
	 * @return new offset inside the byte array.
	 */
	protected static int toByteArrayLSB(  int iByteArrayOffset, byte[] byteArray, int n )
	{
		byteArray[iByteArrayOffset+3] = (byte) (n);
		n >>>= 8;
		byteArray[iByteArrayOffset+2] = (byte) (n);
		n >>>= 8;
		byteArray[iByteArrayOffset+1] = (byte) (n);
		n >>>= 8;
		byteArray[iByteArrayOffset] = (byte) (n);
	
		return iByteArrayOffset + 4;
	}

}
