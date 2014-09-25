package edu.northwestern.at.utils.math.statistics;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.Factorial;

/** Calculate Fisher's exact test for a 2x2 frequency table.
 */

public class FishersExactTest
{
	/**	Calculate Fisher's exact test from the four cell counts.
	 *
	 *	@param	n11		Frequency for cell(1,1).
	 *	@param	n12		Frequency for cell(1,2).
	 *	@param	n21		Frequency for cell(2,1).
	 *	@param	n22		Frequency for cell(2,2).
	 *
	 *	@return			double vector with three entries.
	 *              	[0]	= two-sided Fisher's exact test.
	 *					[1]	= left-tail Fisher's exact test.
	 *					[2]	= right-tail Fisher's exact test.
	 */

	public static double[] fishersExactTest
	(
		int n11 ,
		int n12 ,
		int n21 ,
		int n22
	)
	{
		double result[]	= new double[ 3 ];

								//	Force cell counts to be positive or zero.

		int a	= Math.abs( n11 );
		int b	= Math.abs( n12 );
		int c	= Math.abs( n21 );
		int d	= Math.abs( n22 );

								//	Compute parameters for hypergeometric
								//	distribution.
		int r	= a + b;
		int s	= c + d;
		int m	= a + c;
		int n	= b + d;
								/*	Get range of variation. */

		int lm	= ( 0 > m - s ) ? 0 : m - s;
		int um	= ( m < r   ) ? m : r;

								/*	Probability is 1 if no range of variation. */

		if ( ( um - lm + 2 ) == 0 )
		{
			result[ 0 ]		= 1.0D;
			result[ 1 ]		= 1.0D;
			result[ 2 ]		= 1.0D;
		}
		else
		{
								//	Compute critical value of
								//	hypergeometric distribution
								//	which serves as cut-off when
								//	computing two-tailed test. *

			double cutoff	=
				hypergeometricProbability( a , r , s , m , n );

								//	Compute Fisher's exact test values.
								//	result[0] = two-tailed value
								//	result[1] = left tail value
								//	result[2] = right tail value

			result[ 0 ]	= 0.0D;
			result[ 1 ]	= 0.0D;
			result[ 2 ]	= 0.0D;

			for ( int i = lm ; i <= um ; i++ )
			{
				double probability	=
					hypergeometricProbability( i , r , s , m , n );

				if ( i <= a ) result[ 1 ] += probability;
				if ( i >= a ) result[ 2 ] += probability;

				if ( probability <= cutoff ) result[ 0 ] += probability;
			}
								//	Make sure all values less then 1 .

			result[ 0 ]	= Math.min( result[ 0 ]	, 1.0D );
			result[ 1 ]	= Math.min( result[ 1 ]	, 1.0D );
			result[ 2 ]	= Math.min( result[ 2 ]	, 1.0D );
		}

		return result;
	}

	/**	Compute log of number of combinations of n things taken k at a time.
	 *
	 *	@param	n	Number of items.
	 *	@param	k	Group size.
	 *
	 *	@return		Value for log of n items taken k at a time.
	 *
	 *	<p>
	 *	log(combination(n,m))	= log(n!/m!(n-m)!)
	 *							= log(n!) - log(m!) - log((n-m)!)
	 *	</p>
	 */

	protected static double logCombination( int n , int k )
	{
		return
			Factorial.logFactorial( n ) -
			Factorial.logFactorial( k ) -
			Factorial.logFactorial( n - k );
	}

	/**	Compute hypergeometric probability.
	 *
	 *	@param	x
	 *	@param	n1d
	 *	@param	n2d
	 *	@param	nd1
	 *	@param	nd2
	 *
	 *	@return		The hypergeometric probability.
	 */

	protected static double hypergeometricProbability
	(
		int x,
		int n1d,
		int n2d,
		int nd1,
		int nd2
	)
	{
		int n3	= nd1 - x;
		int ndd	= nd1 + nd2;

		double sum	=
			logCombination( n1d , x ) +
			logCombination( n2d , n3 ) -
			logCombination( ndd , nd1 );

		return Math.exp( sum );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected FishersExactTest()
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

