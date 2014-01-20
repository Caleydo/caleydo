/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import gleem.linalg.Vec2f;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.ExtensionUtils.IExtensionLoader;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory2.EVisScaleType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * special element factories framework for injection and managing remote elements
 *
 * @author Samuel Gratzl
 *
 */
public final class GLElementFactories {
	private static final List<ElementExtension> extensions;

	static {
		List<ElementExtension> loadExtensions = ExtensionUtils.loadExtensions("org.caleydo.ui.GLElementFactory",
				new IExtensionLoader<ElementExtension>() {
					@Override
					public ElementExtension load(IConfigurationElement elem) throws CoreException {
						return new ElementExtension(elem);
					}
				});
		loadExtensions = new ArrayList<>(loadExtensions);
		// sort by order
		Collections.sort(loadExtensions);
		extensions = ImmutableList.copyOf(loadExtensions);
	}
	/**
	 *
	 */
	private GLElementFactories() {
		// IEclipseContext context = EclipseContextFactory.create();
		// context.dispose();
		// GLElementFactories make = ContextInjectionFactory.make(GLElementFactories.class, context);
	}

	/**
	 * returns a list a element suppliers that can be created using the given parameters
	 * 
	 * @param context
	 *            the context with all the parameters set
	 * @param callerId
	 *            the id of the caller (for including excluding)
	 * @param filter
	 *            an additional filter to include / exclude from the caller side
	 * @return
	 */
	public static ImmutableList<GLElementSupplier> getExtensions(GLElementFactoryContext context, String callerId,
			Predicate<? super String> filter) {
		return getExtensions(context, callerId, filter, null);
	}
	
	/**
	 * returns a list a element suppliers that can be created using the given parameters
	 * 
	 * @param context
	 *            the context with all the parameters set
	 * @param callerId
	 *            the id of the caller (for including excluding)
	 * @param filter
	 *            an additional filter to include / exclude from the caller side
	 * @param scaleFilter
	 *            an additional filter to include / exclude scaling type specific elements
	 * @return
	 */
	public static ImmutableList<GLElementSupplier> getExtensions(GLElementFactoryContext context, String callerId,
			Predicate<? super String> filter, Predicate<? super EVisScaleType> scaleFilter) {
		ImmutableList.Builder<GLElementSupplier> builder = ImmutableList.builder();
		for (ElementExtension elem : extensions) {
			if ((filter != null && !filter.apply(elem.getId()))
					|| (scaleFilter != null && !scaleFilter.apply(elem.getScaleType()))
					|| !elem.canCreate(callerId, context))
				continue;
			builder.add(new GLElementSupplier(elem, context));
		}
		return builder.build();
	}

	/**
	 * a element factory description as a {@link Supplier} to create the actual element
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public static class GLElementSupplier implements Supplier<GLElement>, ILabeled {
		private final ElementExtension extension;
		private final GLElementFactoryContext context;

		private GLElementSupplier(ElementExtension extension, GLElementFactoryContext context) {
			this.extension = extension;
			this.context = context;
		}

		public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
			return extension.getDesc(dim, elem);
		}

		/**
		 * @return
		 */
		public GLElement createParameters(GLElement elem) {
			return extension.createParameters(elem);
		}

		@Override
		public GLElement get() {
			return extension.create(context);
		}

		public String getId() {
			return extension.getId();
		}

		public URL getIcon() {
			return extension.getIcon();
		}

		@Override
		public String getLabel() {
			return extension.getLabel();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("GLElementSupplier[").append(getId()).append(" ").append(getLabel()).append(']');
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((context == null) ? 0 : context.hashCode());
			result = prime * result + ((extension == null) ? 0 : extension.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GLElementSupplier other = (GLElementSupplier) obj;
			if (context == null) {
				if (other.context != null)
					return false;
			} else if (!context.equals(other.context))
				return false;
			if (extension == null) {
				if (other.extension != null)
					return false;
			} else if (!extension.equals(other.extension))
				return false;
			return true;
		}

	}

	private static class ElementExtension implements ILabeled, Comparable<ElementExtension> {
		private final String label;
		private final URL icon;
		private final String id;
		private final int order;
		private final IGLElementFactory factory;
		private final Set<String> excludes;
		private final Set<String> includes;

		/**
		 * @param elem
		 * @throws CoreException
		 */
		private ElementExtension(IConfigurationElement elem) throws CoreException {
			label = elem.getAttribute("name");
			icon = ExtensionUtils.getResource(elem, "icon");
			factory = (IGLElementFactory) elem.createExecutableExtension("factory");
			id = factory.getId();
			excludes = parseList(elem, "exclude");
			includes = parseList(elem, "include");
			String order = elem.getAttribute("order");
			if (order == null || StringUtils.isBlank(order) || !StringUtils.isNumeric(order))
				this.order = 10;
			else
				this.order = Integer.parseInt(order);
		}

		public GLElement createParameters(GLElement elem) {
			if (factory instanceof IGLElementFactory2)
				return ((IGLElementFactory2) factory).createParameters(elem);
			return null;
		}

		public EVisScaleType getScaleType() {
			if (factory instanceof IGLElementFactory2)
				return ((IGLElementFactory2) factory).getScaleType();
			return EVisScaleType.FIX;
		}

		public GLElementDimensionDesc getDesc(EDimension dim, GLElement elem) {
			if (factory instanceof IGLElementFactory2)
				return ((IGLElementFactory2) factory).getDesc(dim, elem);
			// create a dummy one
			Vec2f size = new Vec2f(100, 100);
			if (elem instanceof IHasMinSize)
				size = ((IHasMinSize) elem).getMinSize();
			return GLElementDimensionDesc.newFix(dim.select(size)).build();
		}

		@Override
		public int compareTo(ElementExtension o) {
			return order - o.order;
		}

		private Set<String> parseList(IConfigurationElement elem, String key) {
			Builder<String> builder = ImmutableSet.builder();
			for (IConfigurationElement exclude : elem.getChildren(key)) {
				builder.add(exclude.getAttribute("id"));
			}
			return builder.build();
		}

		/**
		 * @return the id, see {@link #id}
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the icon, see {@link #icon}
		 */
		public URL getIcon() {
			return icon;
		}

		public boolean canCreate(String callerId, GLElementFactoryContext context) {
			// not explicitly excluded by myself
			if (!excludes.isEmpty() && excludes.contains(callerId))
				return false;
			// not explicitly included by myself
			if (!includes.isEmpty() && !includes.contains(callerId))
				return false;
			return factory.apply(context.sub(id));
		}

		public GLElement create(GLElementFactoryContext context) {
			return factory.create(context.sub(id));
		}

		/**
		 * @return the label, see {@link #label}
		 */
		@Override
		public String getLabel() {
			return label;
		}

	}
}
