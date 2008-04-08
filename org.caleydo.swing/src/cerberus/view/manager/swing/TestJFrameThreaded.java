/**
 * 
 */
package cerberus.view.manager.swing;

import cerberus.view.manager.swing.JFrameThreaded;

/**
 * @author Michael Kalkusch
 *
 */
public class TestJFrameThreaded {

	private JFrameThreaded frame1;
	
	private JFrameThreaded frame2;
	
	/**
	 * 
	 */
	public TestJFrameThreaded() {
		frame1 = new JFrameThreaded();		
		frame2 = new JFrameThreaded();
	}

	public void run() {
		
		String[] args = new String[0];
		
		Thread t1 = new Thread(frame1);
		Thread t2 = new Thread(frame2);
		
		t1.run();
		t2.run();
		
		//frame2.run(args);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestJFrameThreaded runApp = new TestJFrameThreaded();

		runApp.run();
	}

}
