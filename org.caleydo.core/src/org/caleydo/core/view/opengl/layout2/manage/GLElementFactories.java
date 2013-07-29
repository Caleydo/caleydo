/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.ExtensionUtils.IExtensionLoader;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
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
public class GLElementFactories {
	private static final Collection<ElementExtension> extensions = ExtensionUtils.loadExtensions(
			"org.caleydo.ui.GLElementFactory", new IExtensionLoader<ElementExtension>() {
				@Override
				public ElementExtension load(IConfigurationElement elem) throws CoreException {
					return new ElementExtension(elem);
				}
			});
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
	 *            a additional filter to include / exclude from the caller side
	 * @return
	 */
	public static ImmutableList<GLElementSupplier> getExtensions(GLElementFactoryContext context, String callerId,
			Predicate<String> filter) {
		ImmutableList.Builder<GLElementSupplier> builder = ImmutableList.builder();
		for (ElementExtension elem : extensions) {
			if (!filter.apply(elem.getId()) || !elem.canCreate(callerId, context))
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
	public static class GLElementSupplier implements Supplier<GLElement> {
		private final ElementExtension extension;
		private final GLElementFactoryContext context;

		private GLElementSupplier(ElementExtension extension, GLElementFactoryContext context) {
			this.extension = extension;
			this.context = context;
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

		public String getLabel() {
			return extension.getLabel();
		}
	}

	private static class ElementExtension implements ILabeled {
		private final String label;
		private final URL icon;
		private final String id;
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
			return factory.canCreate(context.sub(id));
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
