/**
 * 
 */
package cerberus.view.manager.tester;

import cerberus.manager.singelton.OneForAllManager;
import cerberus.view.FrameBaseType;
import cerberus.view.manager.jogl.swing.SwingJoglJInternalFrame;
import cerberus.view.manager.jogl.swing.SwingJoglJFrame;


/**
 * @author Michael Kalkusch
 *
 */
public class TestFrameSingelton {

	private SingeltonJoglFrameManager viewManager;
	
	/**
	 * 
	 */
	public TestFrameSingelton() {
		OneForAllManager regGeneralManager = new OneForAllManager(null);
		regGeneralManager.initAll();
		
		viewManager = new SingeltonJoglFrameManager( regGeneralManager );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("START TESTER");
		TestFrameSingelton tester = new TestFrameSingelton();

		System.out.println("CONSTRUCTOR DONE");
		
		SwingJoglJFrame testFrameA = tester.viewManager.createNewJFrame("Test Frame X");
		
		
		testFrameA.setLocation( 333, 200 );
		testFrameA.setSize( 1111, 777 );
		testFrameA.setVisible(true);
		
		System.out.println("FRAME A DONE");
		
		SwingJoglJInternalFrame testFrameA_internal_A = 
			tester.viewManager.createNewJInternalFrame("Test I-Frame A",
					testFrameA.getId() );
		
		testFrameA_internal_A.setSize( 444, 125 );
		testFrameA_internal_A.setVisible( true );
		
		System.out.println("INTERNAL FRAME A of FRAME A DONE");
		
//		SwingJoglJInternalFrame testFrameA_internal_B = 
//			tester.viewManager.createNewJInternalFrame("Test I-Frame B",
//					testFrameA.getId());
//		
//		testFrameA_internal_B.setSize( 250, 100 );
//		testFrameA_internal_B.setLocation( 555, 77 );
//		testFrameA_internal_B.setVisible( true );
		
		System.out.println("INTERNAL FRAME A of FRAME A DONE");
		

		
		
		SwingJoglJFrame testFrameB = tester.viewManager.createNewJFrame("Test Frame Y");
		
		testFrameB.setLocation( 100, 50 );
		testFrameB.setSize( 500, 700 );
		testFrameB.setVisible(true);
	
		
		tester.viewManager.run(args);		
//		tester.viewManager.initMenus(testFrameA);
//		tester.viewManager.addWindow(FrameBaseType.HEATMAP, true);
		
		System.out.println("INTERNAL FRAME A of FRAME A -HEATMAP- DONE");
		
		System.out.println("END");
	}

}
