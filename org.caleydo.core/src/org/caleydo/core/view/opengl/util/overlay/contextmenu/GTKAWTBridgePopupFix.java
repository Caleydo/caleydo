package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class GTKAWTBridgePopupFix {
	private static final int MAX_ATTEMPTS = 200;
	private static final int MAX_RETRIES = 5;

	public static void showMenu(final MenuCreator mc) {
		showMenu(mc, MAX_RETRIES);
	}

	public static void showMenu(final MenuCreator mc, final int retriesLeft) {
		System.out.println("Retries left: " +retriesLeft);
		
		if (retriesLeft == 0)
			return;

		final Display display = Display.getCurrent();

		final Shell active = display.getActiveShell();
		final Shell useForPopups = new Shell(display, SWT.NO_TRIM | SWT.NO_FOCUS
				| SWT.ON_TOP);

		Point l = display.getCursorLocation();
		l.x -= 2;
		l.y -= 2;
		useForPopups.setLocation(l);
		useForPopups.setSize(4, 4);
		useForPopups.open();
		final int[] count = new int[1];
		Runnable r = new Runnable() {
			public void run() {
				useForPopups.setActive();

				final Menu menu = mc.create(useForPopups);
				menu.addListener(SWT.Hide, new Listener() {
					public void handleEvent(Event e) {
						System.out.println("menu hidden");
						useForPopups.dispose();
						active.setActive();
					}
				});
				menu.addListener(SWT.Show, new Listener() {
					public void handleEvent(Event e) {
						count[0]++;
						if (!menu.isVisible() && count[0] > MAX_ATTEMPTS) {
							Runnable r = new Runnable() {
								public void run() {
									System.err.println("menu not shown after "
											+ MAX_ATTEMPTS + "attempts");
									menu.setVisible(false);
									menu.dispose();
									useForPopups.dispose();
									System.err.println("disposing");
									showMenu(mc, retriesLeft - 1);
								}
							};
							display.asyncExec(r);
							return;
						}

						Runnable r = new Runnable() {
							public void run() {
								if (!menu.isVisible())
									menu.setVisible(true);
								else {
									System.out.println("menu shown " + (count[0]) + " "
											+ menu.isVisible());
								}
							}
						};
						display.asyncExec(r);
					}
				});

				menu.setVisible(true);
			}
		};
		display.asyncExec(r);

	}
}