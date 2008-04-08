/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * Copyright (C) 2001-2004 Gaudenz Alder
 * (6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jgraph.pad.coreframework;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;


/**
 * 
 * Undo listener for one document. When a UndoableEditEvent is fired on a
 * listened object undoableEditHappened will be called. The UndoableEditEvent
 * will contain information about the latest edit and store this in the
 * undo command history
 */
public class GPUndoHandler implements UndoableEditListener {
	/**
	 * <code>document</code> is used by the GPUndoHandler to determine the
	 * document that undos/rados are actioned upon
	 */
	GPDocument document;
	
	public GPUndoHandler(GPDocument document) {
		this.document = document;
	}
	
	/**
	 * Messaged when the Document has created an edit, the edit is
	 * added to <code>graphUndoManager</code>, an instance of UndoManager.
	 */
	public void undoableEditHappened(UndoableEditEvent e) {
		document.getGraphUndoManager().addEdit(e.getEdit());
		// Update state of undo and redo buttons to reflect whether or not
		// there are actions in the history to undo/redo
		document.getGraphpad().getEditUndoAction().update();
		document.getGraphpad().getEditRedoAction().update();
	}
	
}
