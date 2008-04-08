/*
 * @(#)JGraphEllipseView.java 1.0 12-MAY-2004
 * 
 * Copyright (c) 2001-2005, Gaudenz Alder
 * All rights reserved. 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package org.jgraph.pad.graphcellsbase.cellviews;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Gaudenz Alder
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ScaledVertexView extends VertexView {

    public static transient VertexRenderer renderer = new ScaledVertexRenderer();

    public ScaledVertexView() {
        super();
    }

    public ScaledVertexView(Object v) {
        super(v);
    }

    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class ScaledVertexRenderer extends VertexRenderer {

        public void paint(Graphics g) {
            Icon icon = getIcon();
            setIcon(null);
            Dimension d = getSize();
            Image img = null;
            if (icon instanceof ImageIcon)
                img = ((ImageIcon) icon).getImage();
            if (img != null)
                g.drawImage(img, 0, 0, d.width - 1, d.height - 1, this);
            super.paint(g);
        }
    }

}
