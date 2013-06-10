/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;

/**
 * @author Christian
 *
 */
public final class FontUtil {

	private FontUtil() {
	}

	public static Control makeBold(Control control) {
		FontData fontData = control.getFont().getFontData()[0];
		Font font = new Font(control.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		control.setFont(font);
		return control;
	}

	public static Control makeItalic(Control control) {
		FontData fontData = control.getFont().getFontData()[0];
		Font font = new Font(control.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.ITALIC));
		control.setFont(font);
		return control;
	}

	public static Control makeUnderlined(Control control) {
		FontData fontData = control.getFont().getFontData()[0];
		Font font = new Font(control.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(),
				SWT.UNDERLINE_SINGLE));
		control.setFont(font);
		return control;
	}

}
