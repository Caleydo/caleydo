/*
 * @(#) TextCell.java	1.0 11/2/03
 * 
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
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
package org.jgraph.pad.graphcellsbase.cells;

public class TextCell extends org.jgraph.graph.DefaultGraphCell {

	private boolean multiLined = true;

    public TextCell() {
      this(null);
    }

    public TextCell(Object userObject) {
      this(userObject, true);
    }

    public TextCell(Object userObject, boolean multiLined) {
      super(userObject);
      this.multiLined = multiLined;
    }


	public boolean isMultiLined() {
		return multiLined;
	}


}
