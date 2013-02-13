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
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.IMouseLayer.IDragInfo;

import com.google.common.base.Objects;

/**
 * factory class for {@link IDragInfo}
 *
 * @author Samuel Gratzl
 *
 */
public class DragInfos {

	/**
	 * special version of {@link #wrap(Object)} for a string content
	 *
	 * @param info
	 * @return
	 */
	public static StringDragInfo createString(String info) {
		return new StringDragInfo(info);
	}

	/**
	 * wraps an arbitrary object into a wrapper object
	 *
	 * @param data
	 * @return
	 */
	public static WrappedDragInfo wrap(Object data) {
		return new WrappedDragInfo(data);
	}

	public static final class StringDragInfo implements IDragInfo {
		private final String info;

		private StringDragInfo(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((info == null) ? 0 : info.hashCode());
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
			StringDragInfo other = (StringDragInfo) obj;
			return Objects.equal(info, other.info);
		}

		@Override
		public String toString() {
			return "StringDragInfo: " + info;
		}

	}

	public static final class WrappedDragInfo implements IDragInfo {
		private final Object info;

		private WrappedDragInfo(Object info) {
			this.info = info;
		}

		public Object getInfo() {
			return info;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((info == null) ? 0 : info.hashCode());
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
			WrappedDragInfo other = (WrappedDragInfo) obj;
			return Objects.equal(info, other.info);
		}

		@Override
		public String toString() {
			return "WrappedDragInfo: " + info;
		}

	}
}
