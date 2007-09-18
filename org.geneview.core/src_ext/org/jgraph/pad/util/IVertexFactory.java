package org.jgraph.pad.util;

/*
 * Created on 8 juin 2005
 * This is GNU GPL free software
 * Copyright (C) 2004 Rapha�l Valyi, See LICENSE file in distribution for license details
 */

import java.awt.geom.Rectangle2D;

import javax.swing.JToggleButton;

import org.jgraph.graph.GraphCell;
import org.jgraph.pad.coreframework.GPGraphpad;

/**
 * An interface specifying the minimum required methods for creating a cell
 * More methods can also be subclassed form the AbstractDefaultVertexnPortsCreator
 * that already implements this interface with a convenient default behavior.
 * @author rvalyi, consider this as a LGPL contribution
 */
public interface IVertexFactory {
  public GraphCell createCell();
  public JToggleButton getButton();
  public void setGraphpad(GPGraphpad graphpad);
  public GraphCell addVertexnPorts(Rectangle2D bounds);
}
