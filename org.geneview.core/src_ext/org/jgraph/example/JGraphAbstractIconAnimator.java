/** Copyright (c) 2005 Timothy Wall, All Rights Reserved.
 Permission granted for any use, this notice must not
 be removed or modified.
 twall@users.sf.net
 */
package org.jgraph.example;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.Map;
import javax.swing.*;

/**
 * Provides animated {@link ImageIcon}s for <code>Component</code> s which
 * have CellRenderer-like functionality. Ordinarily, you'd get no animation with
 * ordinary {@link javax.swing.tree.TreeCellRenderer}or
 * {@link javax.swing.table.TableCellRenderer}since a single component is used
 * to paint multiple locations on the <code>Component</code>. Animated icons
 * must be tracked independently, one per animated cell. This class provides for
 * maintaining a number of independently animated icons for a given
 * {@link Component}context. A key must be provided which must be unique across
 * all elements of substructure (a table might provide "row x col", while a tree
 * might provide the actual value in a row).
 * <p>
 * Subclasses should be instantiated near the <code>Component</code> context
 * and override the {@link #getRepaintRect(Component,Object)}method to trigger
 * a refresh of its corresponding <code>Component</code> substructure
 * location.
 * <p>
 * Subclasses should take care to invoke {@link #stop()}when you want to stop
 * the animation on a particular location.
 * <p>
 * This class assumes that all operations will take place on the event dispatch
 * thread, thus there is no class-level synchronization.
 * 
 * @see javax.swing.tree.TreeCellRenderer
 * @see javax.swing.tree.DefaultTreeCellRenderer
 * @see javax.swing.table.TableCellRenderer
 * @see javax.swing.table.DefaultTableCellRenderer
 */
// TODO: Figure out how labels update their animation. They don't seem to
// use addImageObserver...
public abstract class JGraphAbstractIconAnimator {

	private static final Map contexts = new WeakHashMap();

	/**
	 * Return whether the given icon is animated. Currently assumes the original
	 * filename ends with "animated.gif".
	 */
	public static boolean isAnimated(Icon icon) {
		// Kind of a hack... can we see if it's an animated GIF by looking
		// inside the ImageIcon some other way?
		if (icon instanceof ImageIcon) {
			// NOTE: icon.toString() may return null!
			String label = icon.toString();
			if (label != null && label.indexOf("animated.gif") != -1)
				return true;
		}
		return false;
	}

	/**
	 * Return any existing, cached animator for the given context/key, or
	 * <code>null</code> if there is none.
	 */
	public static JGraphAbstractIconAnimator get(Component context, Object key) {
		Map map = (Map) contexts.get(context);
		if (map != null) {
			return (JGraphAbstractIconAnimator) map.get(key);
		}
		return null;
	}

	private final ImageIcon icon;

	private final Component context;

	private final Object key;

	/**
	 * Create an object to animate <code>icon</code> on the given
	 * <code>Component</code> at the substructure location represented by the
	 * <code>key</code>.
	 * 
	 * @param context
	 *            Component on which the animation is to be painted
	 * @param key
	 *            Substructure location identifier
	 * @param icon
	 *            The animated icon
	 */
	public JGraphAbstractIconAnimator(final Component context,
			final Object key, ImageIcon icon) {
		this.context = context;
		this.key = key;
		// Make a copy, since the original icon may be used repeatedly.
		this.icon = new ImageIcon(icon.getImage());
		this.icon.setImageObserver(new ImageObserver() {
			public boolean imageUpdate(Image image, int flags, int x, int y,
					int w, int h) {
				if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
					repaint(context, key);
				}
				return (flags & (ALLBITS | ABORT)) == 0;
			}
		});
		Map map = (Map) contexts.get(context);
		if (map == null) {
			map = new HashMap();
			contexts.put(context, map);
		}
		map.put(key, this);
	}

	/**
	 * Invoked when the animated icon indicates that it is time for a repaint.
	 * 
	 * @param context
	 *            Component to refresh
	 * @param key
	 *            Substructure identification key
	 */
	protected void repaint(Component context, Object key) {
		Rectangle rect = getRepaintRect(context, key);
		if (rect != null) {
			context.repaint(rect.x, rect.y, rect.width, rect.height);
		}
	}

	/**
	 * Based on the <code>Component</code> context and key, return the
	 * <code>Component</code> -relative rectangle to be repainted.
	 */
	public abstract Rectangle getRepaintRect(Component context, Object key);

	/** Stop animating this instance of <code>AbstractIconAnimator</code>. */
	public void stop() {
		icon.setImageObserver(null);
		icon.getImage().flush();
		Map map = (Map) contexts.get(context);
		if (map != null) {
			map.remove(key);
			if (map.size() == 0)
				contexts.remove(context);
		}
	}

	/**
	 * Returns the icon to be used for the renderer corresponding to this
	 * <code>AbstractIconAnimator</code>.
	 */
	public Icon getIcon() {
		return icon;
	}
}
