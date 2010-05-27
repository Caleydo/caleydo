/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
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

package com.jmex.awt.swingui.dnd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.jmex.awt.swingui.JMEDesktop;

/**
 * Drag and Drop support for {@link JMEDesktop} see {@link jmetest.awt.swingui.dnd.TestJMEDragAndDrop} for an example.
 * @author Galun
 */
public class JMEDragAndDrop {
    private static final Logger logger = Logger.getLogger(JMEDragAndDrop.class
            .getName());

    private JMEDragGestureEvent dge;
    private Transferable transferable;
    private JMEDragSourceListener dragSourceListener;
    private JMEDropTargetListener dropTargetListener;
    private boolean dndInProgress;
    private JMEDesktop desktop;
    private boolean allowFreeDrop;
    private JComponent dragComponent;

    /**
     * create a drag and drop support for this desktop
     * @param desktop the JMEDesktop to add drag and drop support
     */
    public JMEDragAndDrop( JMEDesktop desktop ) {
        dndInProgress = false;
        setDesktop( desktop );
    }

    private void setDesktop( JMEDesktop desktop ) {
        desktop.setDragAndDropSupport( this );
        this.desktop = desktop;
    }

    /**
     * get the transferable (the wrapped object to transport via drag and drop)
     * @return the Transferable or null if there is no drag and drop in progress
     */
    public Transferable getTransferable() {
        return transferable;
    }

    /**
     * check if the component is the drag component
     * @param c the component to check
     * @return true if this is the drag component
     */
    public boolean isDragPanel( Component c ) {
        return c != null && c == dragComponent;
    }

    /**
     * start to drag an object
     * this is the same as calling startDrag( JMEDragGestureEvent dge, ImageIcon icon, Transferable transferable,
     * JMEDragSourceListener listener, false )
     * 
     * @param dge the drag gesture event that initiated the dragging
     * @param icon the icon to represent the dragged object
     * @param transferable the wrapped object to drag
     * @param listener the drag source listener
     * @throws JMEDndException
     * @see #startDrag(JMEDragGestureEvent, javax.swing.ImageIcon, java.awt.datatransfer.Transferable, 
     *                 JMEDragSourceListener, boolean)
     */
    public void startDrag(JMEDragGestureEvent dge, ImageIcon icon, Transferable transferable,
                          JMEDragSourceListener listener) throws JMEDndException {
        startDrag(dge, icon, transferable, listener, false);
    }

    /**
     * start to drag an object
     * @param dge the drag gesture event that initiated the dragging
     * @param icon the icon to represent the dragged object
     * @param transferable the wrapped object to drag
     * @param listener the drag source listener
     * @param allowFreeDrop whether to allow drops on the desktop without a drop target (drops in free space)
     * @throws JMEDndException
     */
    public void startDrag( JMEDragGestureEvent dge, ImageIcon icon, Transferable transferable,
                           JMEDragSourceListener listener, boolean allowFreeDrop ) throws JMEDndException {
        if ( dndInProgress ) {
            throw new JMEDndException( "drag and drop in progress" );
        }
        this.dge = dge;
        this.transferable = transferable;
        this.allowFreeDrop = allowFreeDrop;
        dragSourceListener = listener;
        // if drag source is also a drop target, initialize dropTargetListener to the same component
        if (listener instanceof JMEDropTargetListener)
            dropTargetListener = (JMEDropTargetListener)listener;
        JLabel label = new JLabel( icon );
        label.setName( "dragLabel" );
        label.setSize( new Dimension( icon.getIconWidth(), icon.getIconHeight() ) );
        dragComponent = label;
        desktop.getJDesktop().add( dragComponent, Integer.MAX_VALUE );
        desktop.getJDesktop().setComponentZOrder( dragComponent, 0 );
        dndInProgress = true;
    }

    /**
     * check whether there is currently a drag in progress
     * @return true if there is a drag started
     */
    public boolean isDragging() {
        return dndInProgress;
    }

    /**
     * drag the icon around. This is called by the DragGestureRecognizer on Mouse Drag events
     * @param event the mouse event
     */
    public void doDrag( MouseEvent event ) {
        Point p = SwingUtilities.convertPoint((Component)event.getSource(), event.getX(), event.getY(), desktop.getJDesktop());
        dragComponent.setLocation(p.x - dragComponent.getWidth() / 2, p.y - dragComponent.getHeight() / 2);
        if (dropTargetListener != null) {
            Point p2 = SwingUtilities.convertPoint(desktop.getJDesktop(), p.x, p.y, (Component)dropTargetListener);
			dropTargetListener.dragOver(new JMEDropTargetEvent(event.getSource(),p2, dge.getAction(), this));
        }
    }

    private JMEDropTargetListener getDropTargetListener(Container cont) {
        for (int i = 0; i < cont.getComponentCount(); i++) {
            Component c = cont.getComponent(i);
            if (c instanceof JMEDropTargetListener)
                return (JMEDropTargetListener)c;
            if (c instanceof Container) {
                JMEDropTargetListener dtl = getDropTargetListener((Container)c);
                if (dtl != null)
                    return dtl;
            }
        }
        return null;
    }

    private JMEDropTargetListener getDropTargetListenerAt(Component c, int x, int y) {
        Component cc = c.getComponentAt(x, y);
        if (cc == null || cc == c)
            return null;
        if (cc instanceof JMEDropTargetListener)
            return (JMEDropTargetListener)cc;        
        if (cc instanceof Container)
            return getDropTargetListener((Container)cc);
        return null;
    }

    /**
     * the drag is about to end. This is called by DragGestureRecongnizer after dragging when the mouse button
     * is released
     * @param e the mouse event that ends the dragging
     */
    public void doDrop(MouseEvent e) {
        Point tp = e.getPoint();
        //log.info("dropTargetListener=" + dropTargetListener + ", e=" + e);
        dragComponent.setVisible(false);
        desktop.getJDesktop().remove(dragComponent);
        // check if we got confused by MOUSE_EXITED events
        if (dropTargetListener == null && ! ((Component)dragSourceListener).contains(tp)) {
            tp = SwingUtilities.convertPoint((Component)dragSourceListener, e.getX(), e.getY(), desktop.getJDesktop());
            //log.info("looking at " + tp);
            dropTargetListener = getDropTargetListenerAt(desktop.getJDesktop(), tp.x, tp.y);
            //log.info("after restore: dropTargetListener:" + dropTargetListener);
        }
        boolean freeDrop = false;
        boolean dropSuccess = false;
        if (dropTargetListener != null) {
            Point p = SwingUtilities.convertPoint((Component)dragSourceListener, e.getX(), e.getY(), (Component)dropTargetListener);
			JMEDropTargetEvent dte = new JMEDropTargetEvent(e.getSource(),p, dge.getAction(), this);
            dropTargetListener.drop(dte);
            dropSuccess = dte.isCompleted();
        } else {
            if (allowFreeDrop) {
                tp = SwingUtilities.convertPoint((Component)dragSourceListener, e.getX(), e.getY(), desktop.getJDesktop());
                Component c = desktop.getJDesktop().getComponentAt(tp);
                if (c == null || c instanceof JDesktopPane) {
                    dropSuccess = true;
                    freeDrop = true;
                } else {
                    logger.info("no drop target and dropped on " + tp + c);
                }
            } else
                logger.info("no drop target and freedrop is not allowed.");
        }
        dragSourceListener.dragDropEnd(new JMEDragSourceEvent(tp, dge.getAction(), dropSuccess, freeDrop));
        dndInProgress = false;
        dropTargetListener = null;
    }

    /**
     * called by the DragGestureRecognizer on mouse enter events
     * @param e the mouse event that triggered
     */
    public void mouseEntered( MouseEvent e ) {
        if ( e.getSource() instanceof JMEDropTargetListener ) {
            dropTargetListener = (JMEDropTargetListener) e.getSource();
            Point p = SwingUtilities.convertPoint((Component)dragSourceListener, e.getX(), e.getY(), (Component)dropTargetListener);
			( (JMEDropTargetListener)e.getSource() ).dragEnter( new JMEDropTargetEvent(e.getSource(),p, dge.getAction(), this ) );
        }
        dragSourceListener.dragEnter( new JMEDragSourceEvent( e.getPoint(), dge.getAction(), e.getComponent() ) );
    }

    /**
     * called by the DragGestureRecognizer on mouse exit events
     * @param e the mouse event that triggered
     */
    public void mouseExited( MouseEvent e ) {
        if ( e.getSource() instanceof JMEDropTargetListener ) {
            dropTargetListener = null;
            ( (JMEDropTargetListener) e.getSource() ).dragExit( new JMEDropTargetEvent(e.getSource(), e.getPoint(), dge.getAction(), this ) );
        }
        dragSourceListener.dragExit( new JMEDragSourceEvent( e.getPoint(), dge.getAction() ) );
    }

    /**
     * creates an ImageIcon using the supplied text. This can be used to drag text around.
     * @param c the component to take the font from (typically the drag source)
     * @param text the text to put into the image
     * @return a new ImageIcon
     */
    public static ImageIcon createTextIcon( JComponent c, String text ) {
        Font font = c.getFont();
        int mx = 2;
        int my = 1;
        int w = c.getFontMetrics( font ).stringWidth( text ) + mx * 2;
        int h = c.getFontMetrics( font ).getHeight() + my * 2;
        BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_4BYTE_ABGR );
        Graphics g = bi.getGraphics();
        g.setColor( Color.black );
        g.fillRect( 0, 0, w, h );
        g.setFont( font );
        g.setColor( Color.yellow );
        g.drawString( text, mx, h - my );
        logger.info("created a text image for " + text + ": " + bi.toString());
// try {
//            ImageIO.write(bi, "png", new File("/tmp/text.png"));
//        } catch (Exception ex) {}
        return new ImageIcon( bi );
    }

    /**
     * return the current drop target listener
     * @return the current drop target listener or null if there is none
     */
    public JMEDropTargetListener getDropTargetListener() {
        return dropTargetListener;
    }
}
