package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

/**	Polynomial functions.
 */

public class Polynomial
{
	/** Evaluate a polynomial expression using Horner's method.
	 *
	 *	@param	polycoefs	Array of polynomial coefficients.
	 *						The coefficients should be ordered
	 *						with the constant term first and the
	 *						coefficient for the highest powered term
	 *						last.
	 *
	 *	@param	x	    	Value for which to evaluate polynomial.
	 *
	 *	@return				Polynomial evaluated using Horner's method.
	 *
	 *	<p>
	 *	Horner's method is given by the following recurrence relation:
	 *	c[i]*x^i + ... + c[1]*x + c[0] = c[0] + x*(c[i-1]*x^[i-1] + ... + c[1])
	 *	</p>
	 *
	 *	<p>
	 *	Horner's method avoids loss of precision which can occur
	 *	when the higher-power values of x are computed directly.
	 *	</p>
	 */

	public static double hornersMethod( double polycoefs[] , double x )
	{
		double result	= 0.0;

		for ( int i = polycoefs.length - 1 ; i >= 0 ; i-- )
		{
			result	= result * x + polycoefs[ i ] ;
        }

		return result;
	}

	/**	Evaluates a Chebyschev series.
	 *
	 *	@param	coeffs		The Chebyschev polynomial coefficients.
	 *
	 *	@param	x			The value for which to evaluate
	 *						the polynomial.
	 *
	 *	@return				The Chebyschev polynomial evaluated
	 *						at x.
	 */

	public static double evaluateChebyschev
	(
		double coeffs[] ,
		double x
	)
	{
		double b0;
		double b1;
		double b2;
		double twox;

		int		i;

		b0		= 0.0D;
		b1		= 0.0D;
		b2		= 0.0D;

		twox	= x + x;

		for ( i = coeffs.length - 1 ;  i >= 0 ;  i-- )
		{
			b2	= b1;
			b1	= b0;
			b0	= twox * b1 - b2 + coeffs[ i ];
		}

		return 0.5D * ( b0 - b2 );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected Polynomial()
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

