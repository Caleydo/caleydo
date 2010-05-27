package jmetest.awt.swingui.dnd;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import com.jme.bounding.BoundingSphere;
import com.jme.input.FirstPersonHandler;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jmex.awt.swingui.JMEDesktopState;
import com.jmex.awt.swingui.dnd.JMEDndException;
import com.jmex.awt.swingui.dnd.JMEDragAndDrop;
import com.jmex.awt.swingui.dnd.JMEDragGestureEvent;
import com.jmex.awt.swingui.dnd.JMEDragGestureListener;
import com.jmex.awt.swingui.dnd.JMEDragSourceEvent;
import com.jmex.awt.swingui.dnd.JMEDragSourceListener;
import com.jmex.awt.swingui.dnd.JMEDropTargetEvent;
import com.jmex.awt.swingui.dnd.JMEDropTargetListener;
import com.jmex.awt.swingui.dnd.JMEMouseDragGestureRecognizer;
import com.jmex.editors.swing.settings.GameSettingsPanel;
import com.jmex.game.StandardGame;
import com.jmex.game.state.DebugGameState;
import com.jmex.game.state.GameStateManager;

/**
 * Clean and simple drag and drop test with GameStates
 * 
 * If you want to test without the swapping with source icon, comment this code in DndIcon.drop(JMEDropTargetEvent e)
 *  //DndIcon source = (DndIcon) e.getSource();
 *	//source.setIcon(this.getIcon());
 * 
 * @author Nomis
 */
public class TestDnd {
	public static void main(String[] args) {
	    System.setProperty("jme.stats", "set");
		TestDnd test = new TestDnd();
		StandardGame game = new StandardGame("DND test");
		try {
            if (!GameSettingsPanel.prompt(game.getSettings())) {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		game.start();

		TestDnd.MyGameState ingameState = test.new MyGameState(game);
		GameStateManager.getInstance().attachChild(ingameState);

		// always updateRenderState or get funky effects
		ingameState.getRootNode().updateRenderState();
		// By the way I strongly advise you to comment it to see the funky
		// effects it's really fun !
		
		ingameState.setActive(true);
		
		TestDnd.MyHudState hudState = test.new MyHudState();
		hudState.setActive(true);
		hudState.getRootNode().updateRenderState();
		GameStateManager.getInstance().attachChild(hudState);
	}

	/**
	 * GameState which shows a rotating Box in the background. 
	 */
	public class MyGameState extends DebugGameState {
		private Box box;
		private Quaternion rotQuat = new Quaternion();
		private float angle = 0;
		private Vector3f axis = new Vector3f(1, 1, 0.5f);

		public MyGameState(StandardGame game) {
			super(game);
			// remove the MouseLookHnadler because the mouse is used for the hud
			((FirstPersonHandler)input).getMouseLookHandler().setEnabled(false);
			this.box = new Box("my box", new Vector3f(0, 0, 0), 2, 2, 2);
			box.setModelBound(new BoundingSphere());
			box.updateModelBound();
			this.getRootNode().attachChild(box);

			this.axis.normalizeLocal();
		}

		/**
		 *  rotating the box to show there is some 3d behind the UI
		 */
		@Override
		public void update(float tpf) {
			super.update(tpf);
			if (tpf < 1) {
				angle = angle + (tpf * 25);
				if (angle > 360) {
					angle = 0;
				}
			}
			rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
			box.setLocalRotation(rotQuat);
		}
	}

	/**
	 * JMEDesktopGamestate which shows a few dragable Icons
	 * inside a JInternalFrame.
	 */
	public class MyHudState extends JMEDesktopState {
		public MyHudState() {
			super();
		}

		/**
		 * creates a JInternalFrame with a few dragable Icons.
		 */
		protected void buildUI() {
			new JMEDragAndDrop(this.getDesktop());

			JInternalFrame frame = new JInternalFrame("dnd test", true, true);
			frame.setLayout(new GridLayout(4, 4));

			Icon icon1 = getResizedIcon("Monkey.jpg");
			frame.add(new DndIcon(this, icon1));
			Icon icon2 = getResizedIcon("Monkey.png");
			frame.add(new DndIcon(this, icon2));
			Icon icon3 = getResizedIcon("logo.jpg");
			frame.add(new DndIcon(this, icon3));

			// few empty icons to let you play
			for (int i = 0; i < 13; i++) {
				frame.add(new DndIcon(this, null));
			}
			frame.setSize(64 * 4, 64 * 4);
			frame.setLocation(100, 100);
			frame.setVisible(true);
			this.getDesktop().getJDesktop().add(frame);
			MouseInput.get().setCursorVisible(true);
		}

		private Icon getResizedIcon(String fileName) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/jmetest/data/images/" + fileName));
			icon.setImage(icon.getImage().getScaledInstance(64, 64, 16));
			return icon;
		}
	}

	/**
	 * DndIcon is the drag source and the drop target, so you can easily drag /
	 * swap icons from different panels
	 * 
	 * @author Nomis
	 */
	public class DndIcon extends JLabel implements JMEDragGestureListener, JMEDragSourceListener, JMEDropTargetListener {

		private static final long serialVersionUID = 1L;

		private JMEDragAndDrop dndSupport;

		public DndIcon(JMEDesktopState desktopSate, Icon icon) {
			this.setIcon(icon);
			this.dndSupport = desktopSate.getDesktop().getDragAndDropSupport();
			new JMEMouseDragGestureRecognizer(dndSupport, this, DnDConstants.ACTION_COPY_OR_MOVE, this);
			this.setBorder(BorderFactory.createLineBorder(Color.black));
		}

		/**
		 *  drop = swap this icon with event's source component icon
		 */
		public void drop(JMEDropTargetEvent e) {
			TransferableImage t = (TransferableImage) e.getTransferable();

			Icon icon=null;
			try {
				icon = (Icon) t.getTransferData(null);
			} catch (UnsupportedFlavorException e1) {				
				e1.printStackTrace();
			} catch (IOException e1) {				
				e1.printStackTrace();
			}

			if (icon != null) {
				// Set current icon to the source
				DndIcon source = (DndIcon) e.getSource();
				source.setIcon(this.getIcon());
				this.setIcon(icon);
			}

		}

		public void dragGestureRecognized(JMEDragGestureEvent dge) {
			if (this.getIcon() == null) {
				// nothing to transfer
				return;
			}

			TransferableImage transferable = new TransferableImage(this.getIcon());

			try {
				dndSupport.startDrag(dge, (ImageIcon) this.getIcon(), transferable, this);
			} catch (JMEDndException e) {
				e.printStackTrace();
			}
		}

		// we don't care for all other events

		public void dragDropEnd(JMEDragSourceEvent e) {
		}

		public void dragEnter(JMEDragSourceEvent e) {
		}

		public void dragExit(JMEDragSourceEvent e) {
		}

		public void dragEnter(JMEDropTargetEvent e) {
		}

		public void dragExit(JMEDropTargetEvent e) {
		}

		public void dragOver(JMEDropTargetEvent e) {
		}
	}
}