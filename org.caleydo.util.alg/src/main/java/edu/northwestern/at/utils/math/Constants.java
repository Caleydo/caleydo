package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

/**	Machine-dependent arithmetic constants.
 */

public class Constants
{
	/** Machine epsilon.  Smallest double floating point number
	 *	such that (1 + MACHEPS) > 1 .
	 */

	public static final double MACHEPS		= determineMachineEpsilon();

	/* Machine precision in decimal digits . */

	public static final int	MAXPREC			= determineMaximumPrecision();

	/**	Maximum logarithm value. */

	public static final double MAXLOG		= 7.09782712893383996732E2;

	/** Minimum logarithm value. */

	public static final double MINLOG		= -7.451332191019412076235E2;

	/** Square root of 2. */

	public static final double SQRT2		= Math.sqrt( 2.0D );

	/** ( Square root of 2 ) / 2 . */

	public static final double SQRT2DIV2	= SQRT2 / 2.0D;

	/** Square root of PI. */

	public static final double SQRTPI		= Math.sqrt( Math.PI );

	/** Natural log of PI. */

	public static final double LNPI			= Math.log( Math.PI );

	/* LN(10) .            */

	public static final double	LN10		= Math.log( 10.0D );

	/* 1 / LN(10)             */

//	public static final double	LN10INV		= 1.0D / LN10;
	public static final double	LN10INV		= 0.43429448190325182765D;

	/* LN(2)                  */

	public static final double	LN2			= Math.log( 2.0D );

	/* 1 / LN(2)              */

	public static final double	LN2INV		= 1.0D / LN2;

	/* LN( Sqrt( 2 * PI ) ) */

	public static final double	LNSQRT2PI	=
		Math.log( Math.sqrt( 2.0D * Math.PI ) );

	/** Determine machine epsilon.
	 *
	 *	@return		The machine epsilon as a double.
	 *				The machine epsilon MACHEPS is the
	 *				smallest number such that (1 + MACHEPS) == 1 .
	 */

	public static double determineMachineEpsilon()
    {
        double d1 = 1.3333333333333333D;
        double d3;
        double d4;

        for( d4 = 0.0D; d4 == 0.0D; d4 = Math.abs( d3 - 1.0D ) )
        {
            double d2 = d1 - 1.0D;
            d3 = d2 + d2 + d2;
        }

        return d4;
    }

	/** Determine maximum double floating point precision.
	 *
	 *	@return		Maximum number of digits of precision
	 *				for double precision floating point.
	 */

	public static int determineMaximumPrecision()
    {
								//	Get machine epsilon.

    	double	macheps	= determineMachineEpsilon();

								//	Maximum digits of precision
								//	is given by the negative of
								//	of the base 10 exponent of
								//	of the machine precision.
		double	digits	=
			ArithUtils.trunc(
				Math.log( macheps ) / Math.log( 10.0D ) );

    	return -new Long( Math.round( digits ) ).intValue();
    }

	/**	This class is non-instantiable but inheritable.
	 */

	protected Constants()
	{
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

