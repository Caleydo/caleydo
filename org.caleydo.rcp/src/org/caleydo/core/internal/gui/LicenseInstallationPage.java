package org.caleydo.core.internal.gui;


import org.apache.commons.lang.StringUtils;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.about.InstallationPage;

public class LicenseInstallationPage extends InstallationPage {

	public LicenseInstallationPage() {

	}

	@Override
	public void createControl(Composite parent) {
		parent = new Composite(parent,SWT.NONE);
		final GridLayout l = new GridLayout(1, true);
		l.verticalSpacing = 20;
		parent.setLayout(l);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		label.setText("Following licensed materials are used within Caleydo:");

		FontData fontData = label.getFont().getFontData()[0];
		Font bold = new Font(label.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));

		addLine(parent,
				bold,
				"Splash Screen",
				"The background image is from <a href=\"http://en.wikipedia.org/wiki/File:Hyades.jpg\">Wikipedia</a> and was created by Todd Vance.",
				"It is licensed under <a href=\"http://creativecommons.org/licenses/by-sa/2.5/deed.en\">Creative Commons Attribution-Share Alike 2.5 Generic</a>.");
		addLine(parent,
				bold,
				"Icon Set",
				"<a href=\"https://www.fatcow.com/free-icons\">fatcow.com</a> IconSet.",
				"It is licensed under <a href=\"https://creativecommons.org/licenses/by/3.0/legalcode\">Creative Commons Attribution 3.0 License</a>.");
	}

	private void addLine(Composite parent, Font bold, String title, String... text) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		label.setText(title);
		label.setFont(bold);

		Link l = new Link(parent, SWT.WRAP);
		l.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		l.addSelectionListener(BrowserUtils.LINK_LISTENER);
		l.setText(StringUtils.join(text, "\n"));
	}

}
