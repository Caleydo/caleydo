/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import gleem.linalg.Vec2f;

import java.awt.Point;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.internal.CaleydoJAXBTransfer;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.google.common.collect.Sets;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class MouseLayer extends GLElementContainer implements IMouseLayer, IGLLayout2 {
	private final IGLCanvas canvas;

	private final Set<IDropGLTarget> dropTargets = Sets.newCopyOnWriteArraySet();
	private final Set<IDragGLSource> dragSources = Sets.newCopyOnWriteArraySet();

	volatile IDropGLTarget activeDropTarget;
	volatile DNDItem active;

	DragSource source;
	DropTarget target;

	public MouseLayer(IGLCanvas canvas) {
		super();
		this.canvas = canvas;
		setLayout(this);
	}

	@Override
	protected void init(IGLElementContext context) {
		canvas.addDragListener(drag);
		canvas.addDropListener(drop);
		super.init(context);
	}

	@Override
	protected void takeDown() {
		canvas.removeDragListener(drag);
		canvas.removeDropListener(drop);
		super.takeDown();
	}

	private final DragSourceListener drag = new DragSourceListener() {
		@Override
		public void dragStart(DragSourceEvent event) {
			DragEvent e = new DragEvent(canvas.toDIP(new Point(event.x, event.y)));
			for(IDragGLSource source : dragSources) {
				IDragInfo info = source.startSWTDrag(e);
				if (info != null) {
					active = new DNDItem(info, source);
					return;
				}
			}
			// drag drag sources if there check if it one can be created
			event.doit = false; // abort
		}

		@Override
		public void dragSetData(DragSourceEvent event) {
			if (active == null) {
				event.doit = false;
				return;
			}
			if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = active.getInfo().getLabel();
			} else if (CaleydoJAXBTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = active.getInfo();
			}
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			if (active == null)
				return;
			active.setType(event.detail);
			// thread switch
			EventPublisher.trigger(new DragFinishedEvent(active, active.source).to(MouseLayer.this));
			active = null;
		}
	};
	private final DropTargetListener drop = new DropTargetListener() {

		@Override
		public void dragEnter(DropTargetEvent event) {
			IDnDItem item = toItem(event);
			validateDropTarget(event, item);
		}

		@Override
		public void dropAccept(DropTargetEvent event) {

		}

		@Override
		public void drop(DropTargetEvent event) {
			IDnDItem item = toItem(event);
			if (validateDropTarget(event, item)) {
				// thread switch
				EventPublisher.trigger(new DropItemEvent(item, activeDropTarget, true).to(MouseLayer.this));
				activeDropTarget = null;
			}
		}

		@Override
		public void dragOver(DropTargetEvent event) {
			itemChanged(event);

		}

		private void itemChanged(DropTargetEvent event) {
			IDnDItem item = toItem(event);
			if (validateDropTarget(event, item))
				// thread switch
				EventPublisher.trigger(new DropItemEvent(item, activeDropTarget, false).to(MouseLayer.this));
		}

		@Override
		public void dragOperationChanged(DropTargetEvent event) {
			itemChanged(event);
		}

		private boolean validateDropTarget(DropTargetEvent event, IDnDItem item) {
			if (item == null) {
				event.detail = DND.DROP_NONE;
				return false;
			}
			if (activeDropTarget == null) {
				activeDropTarget = findDropTarget(item);
				if (activeDropTarget != null) {
					event.detail = DND.DROP_DEFAULT;
				}
			}
			if (activeDropTarget == null) {
				event.detail = DND.DROP_NONE;
				return false;
			}
			return true;
		}

		@Override
		public void dragLeave(DropTargetEvent event) {
			activeDropTarget = null;
		}
	};

	@ListenTo(sendToMe = true)
	private void onDragFinishedEvent(DragFinishedEvent event) {
		event.getSource().onDropped(event.getItem());
	}

	@ListenTo(sendToMe = true)
	private void onDropItemEvent(DropItemEvent event) {
		IDropGLTarget t = event.getTarget();
		if (event.isDropping())
			t.onDrop(event.getItem());
		else
			t.onItemChanged(event.getItem());
	}

	/**
	 * @param item
	 * @return
	 */
	protected IDropGLTarget findDropTarget(IDnDItem item) {
		for (IDropGLTarget target : dropTargets) {
			if (target.canSWTDrop(item))
				return target;
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	protected IDnDItem toItem(DropTargetEvent event) {
		DNDItem item = null;
		if (active != null)
			item = active;
		else {
			// FIXME extract the item from the drop target in case of multi view manipulation
		}
		if (item != null)
			item.setType(event.detail);
		return item;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		for (IGLLayoutElement child : children) {
			child.setBounds(defaultValue(child.getSetX(), 0), defaultValue(child.getSetY(), 0),
					defaultValue(child.getSetWidth(), w), defaultValue(child.getSetHeight(), h));
		}
		return false;
	}

	@Override
	public boolean isDragging(Class<? extends IDragInfo> type) {
		return active != null && type.isInstance(active.info);
	}

	@Override
	public void addDragSource(IDragGLSource dragSource) {
		System.out.println("add drag source");
		this.dragSources.add(dragSource);
	}

	@Override
	public void removeDragSource(IDragGLSource dragSource) {
		System.out.println("remove drag source");
		this.dragSources.remove(dragSource);
	}

	@Override
	public void addDropTarget(IDropGLTarget dropTarget) {
		System.out.println("add drop target");
		this.dropTargets.add(dropTarget);
	}

	@Override
	public void removeDropTarget(IDropGLTarget dropTarget) {
		System.out.println("remove drop target");
		this.dropTargets.remove(dropTarget);
		if (activeDropTarget == dropTarget) {
			activeDropTarget = null;
		}
	}

	private class DNDItem implements IDnDItem {
		private final IDragGLSource source;
		private final IDragInfo info;
		private EDnDType type = EDnDType.MOVE;

		public DNDItem(IDragInfo info, IDragGLSource source) {
			this.info = info;
			this.source = source;
		}

		/**
		 * @param detail
		 */
		public void setType(int detail) {
			if ((detail & DND.DROP_MOVE) != 0)
				type = EDnDType.MOVE;
			else if ((detail & DND.DROP_COPY) != 0)
				type = EDnDType.COPY;
			else if ((detail & DND.DROP_LINK) != 0)
				type = EDnDType.LINK;
			else
				type = EDnDType.NONE;
		}

		@Override
		public IDragInfo getInfo() {
			return info;
		}

		@Override
		public EDnDType getType() {
			return type;
		}
	}

	private static class DragEvent implements IDragEvent {
		private final Vec2f offset;

		public DragEvent(Vec2f offset) {
			this.offset = offset;
		}

		/**
		 * @return the offset, see {@link #offset}
		 */
		@Override
		public Vec2f getOffset() {
			return offset;
		}
	}
}
