package org.geneview.core.application.testing;

import org.geneview.core.math.MathUtil;

/**
 * Test org.geneview.core.math.MathUtil implementation.
 * 
 * @author Michael Kalkusch
 *
 */
public class DivTest {

	public DivTest() {
		
	}
	
	public void test () {
	int range = 256;
		
		for ( int k=5; k<10; k++)
		{
			for ( int i=1; i<7; i++)
			{
				int a = range * k + i;
				
				int b = a >> 8;
			
				int c = a - (b * range);
			
				System.out.println(" #" + i + 
						"(" + k +
						"): " + a + 
						"  =>" + b + 
						"  | " + c);
				
			}
			
			System.out.println(" ----");
		}
		
		int z = Integer.MAX_VALUE >> 8;
		
		System.out.println("TOP: #" + Integer.toString(Integer.MAX_VALUE) + 
				": " + z );
		
		MathUtil.testConsitency();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DivTest a= new DivTest();
		a.test();
	}

}
