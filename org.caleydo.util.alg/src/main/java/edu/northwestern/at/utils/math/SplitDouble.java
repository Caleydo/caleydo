package edu.northwestern.at.utils.math;

/*	Please see the license information in the header below. */

/**	Splits a double into a normalized base two mantissa and exponent..
 *
 *	<p>
 *	A double value is split into a normalized mantissa in
 *	the range 0.5 to 1.0, and a corresponding base 2 exponent.
 *	</p>
 */

public class SplitDouble
{
	/** The normalized mantissa.*/

	public double mantissa;

	/** The base two exponent. */

	public int exponent;

	/**	Create SplitDouble object.
	 *
	 *	@param	d	The double to split.
	 */

	public SplitDouble( double d )
	{
		if ( d == 0.0D )
		{
			mantissa	= 0.0D;
			exponent	= 0;
		}
		else
		{
			long bits	= Double.doubleToLongBits( d );

			mantissa	=
				Double.longBitsToDouble( ( 0X800FFFFFFFFFFFFFL & bits ) |
				0x3FE0000000000000L );

			exponent	= (int)( ( bits >> 52 ) & 0x7ff ) - 1022;
		}
	}
}

/*
 * <p>
 * Copyright &copy; 2004-2011 Northwestern University.
 * </p>
 * <p>
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 * </p>
 */


