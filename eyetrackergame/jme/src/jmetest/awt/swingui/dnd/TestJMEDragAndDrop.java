/*
 * Copyright (c) 2006 World of Mystery Project Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.awt.swingui.dnd;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.event.AWTEventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

import com.jme.app.SimpleGame;
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jmex.awt.swingui.JMEDesktop;
import com.jmex.awt.swingui.dnd.JMEDragAndDrop;

/**
 * test changes to JMEDesktop
 * 
 * @author galun
 * @version $Id: TestJMEDragAndDrop.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestJMEDragAndDrop extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestJMEDragAndDrop.class.getName());

    private JMEDesktop desktop;
    private JMEDndPanel dndPanel1;
    private JMEDndPanel dndPanel2;
    private DndImage dndImage1;
    private DndImage dndImage2;
    private JTextPane debugPanel;
    private static TestJMEDragAndDrop instance;
    private Text t;

    public TestJMEDragAndDrop() {
    }

    public static void main(String[] args) {
        if (logger.getUseParentHandlers()) {
            logger.setUseParentHandlers(false);
        }
        TestJMEDragAndDrop app = new TestJMEDragAndDrop();
        instance = app;
        instance.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        t.print(desktop.getDragAndDropSupport().isDragging() ? "dragging" : "");
    }

    protected void simpleInitGame() {
        t = Text.createDefaultTextLabel("drag", "");
        statNode.attachChild(t);
        input = new InputHandler();
        desktop = new JMEDesktop("desktop", display.getWidth(), display
                .getHeight(), input);
        new JMEDragAndDrop(desktop);
        rootNode.attachChild(desktop);
        desktop.setLightCombineMode(Spatial.LightCombineMode.Off);
        desktop.getJDesktop().setBackground(new Color(1, 1, 1, 0.0f));
        desktop.setColorBuffer(null);
        desktop.setDefaultColor(new ColorRGBA(1, 1, 1, 0.5f));
        desktop.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        desktop.setCullHint(Spatial.CullHint.Never);
        desktop.getLocalTranslation().set(display.getWidth() / 2,
                display.getHeight() / 2, 0);
        desktop.updateGeometricState(0, true);
        desktop.updateRenderState();

        dndPanel1 = new JMEDndPanel(desktop.getDragAndDropSupport());
        dndPanel1.setSize(380, 100);
        dndPanel1.setVisible(true);
        dndPanel1.setLocation(10, 200);
        dndPanel1.setName("DndPanel1");
        dndPanel2 = new JMEDndPanel(desktop.getDragAndDropSupport());
        dndPanel2.setSize(380, 100);
        dndPanel2.setVisible(true);
        dndPanel2.setLocation(400, 200);
        dndPanel2.setName("DndPanel2");

        dndImage1 = new DndImage(desktop.getDragAndDropSupport());
        dndImage1.setSize(380, 100);
        dndImage1.setVisible(true);
        dndImage1.setLocation(10, 80);
        dndImage1.setName("dndImage1");
        dndImage2 = new DndImage(desktop.getDragAndDropSupport());
        dndImage2.setSize(380, 100);
        dndImage2.setVisible(true);
        dndImage2.setLocation(400, 80);
        dndImage2.setName("dndImage2");

        debugPanel = new JTextPane();
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(debugPanel);
        JInternalFrame f = new JInternalFrame();
        f.add(scroller);
        f.setSize(760, 100);
        f.setResizable(true);
        f.setTitle("Debug");
        f.setLocation(10, 340);
        f.setVisible(true);

        desktop.getJDesktop().add(dndPanel1);
        desktop.getJDesktop().add(dndPanel2);
        desktop.getJDesktop().add(dndImage1);
        desktop.getJDesktop().add(dndImage2);
        desktop.getJDesktop().add(f);

        desktop.getJDesktop().repaint();
        desktop.getJDesktop().revalidate();
        desktop.setVBOInfo(null);
        MouseInput.get().setCursorVisible(true);
        desktop.getJDesktop().getToolkit().addAWTEventListener(
                new AWTEventListener() {
                    public void eventDispatched(AWTEvent event) {
                        logger.fine("AWT:" + event.toString());
                        // if (event instanceof ComponentEvent
                        // &&
                        // ((ComponentEvent)event).getComponent().getName().equals("frame0"))
                        // Thread.dumpStack();
                    }
                }, 0xfffff);
    }

    public static void addText(String text) {
        int offset = instance.debugPanel.getDocument().getLength();
        AttributeSet normal = SimpleAttributeSet.EMPTY;
        try {
            instance.debugPanel.getDocument().insertString(offset, text + "\n",
                    normal);
        } catch (Exception ex) {
            logger.logp(Level.SEVERE, TestJMEDragAndDrop.class.toString(),
                    "addText(String text)", "Exception", ex);
        }
    }
}
