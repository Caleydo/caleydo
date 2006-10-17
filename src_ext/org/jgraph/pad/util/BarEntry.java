/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.pad.util;

/* 
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


/**An entry for inserts into the menubar, toolbars, graph popup
 * menu or library popup menu
 */
public final class BarEntry {
	
	/** The name of the bar key where to insert this 
	 *  bar entry.
	 */
	String barKey;
	
	/** The position where to insert this 
	 *  bar entry.
	 */
	int pos;
	
	
	/** The name of this bar value. Must be equal with
	 *  the Action name. 
	 */
	String barValue;
	
	/**
	 * Constructor for BarEntry.
	 */
	public BarEntry(String barKey, int pos, String barValue) {
		this.barKey = barKey;
		this.pos = pos;
		this.barValue = barValue;
	}
	
	
	/**
	 * Returns the barKey.
	 * @return String
	 */
	public String getBarKey() {
		return barKey;
	}
	
	/**
	 * Returns the barValue.
	 * @return String
	 */
	public String getBarValue() {
		return barValue;
	}
	
	/**
	 * Returns the pos.
	 * @return int
	 */
	public int getPos() {
		return pos;
	}
	
	/**
	 * Sets the barKey.
	 * @param barKey The barKey to set
	 */
	public void setBarKey(String barKey) {
		this.barKey = barKey;
	}
	
	/**
	 * Sets the barValue.
	 * @param barValue The barValue to set
	 */
	public void setBarValue(String barValue) {
		this.barValue = barValue;
	}
	
	/**
	 * Sets the pos.
	 * @param pos The pos to set
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}
	
	/** Prints the Entry with all properties.
	 * 
	 */	
	public String toString(){
		StringBuffer b = new StringBuffer();
		b.append("BarEntry: barKey=");
		b.append(barKey);
		b.append("; pos=");
		b.append(pos);
		b.append("; barValue=");
		b.append(barValue);
		return b.toString() ;
	}
	
}
