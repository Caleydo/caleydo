/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
