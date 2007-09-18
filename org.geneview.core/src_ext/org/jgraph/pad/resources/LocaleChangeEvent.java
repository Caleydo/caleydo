/*
 * @(#)LocaleChangeEvent.java	1.0 23/01/02
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
package org.jgraph.pad.resources;

import java.util.Locale;

/**If the Locale changes this Event will fired by the
 * Translator to all registered LocalChangeListeners.
 */

public final class LocaleChangeEvent {

  /**
   * Represents the old Locale or null
   */
  protected transient Locale oldLocale;

  /**
   * Represents the new Locale
   */
  protected transient Locale newLocale;

  /**
   * Creates a new Locale Change Event with the old and the
   * new Locale.
   */
  public LocaleChangeEvent(Locale oldLocale, Locale newLocale) {
    this.oldLocale = oldLocale;
    this.newLocale = newLocale;
  }

  /**
   * Returns the old Locale
   */
  public Locale getOldLocale(){
    return this.oldLocale;
  }

  /**
   * Returns the new Locale
   */
  public Locale getNewLocale(){
    return this.newLocale;
  }

}