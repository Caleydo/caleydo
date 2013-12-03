/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import gleem.linalg.Vec2f;

import java.awt.Point;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.internal.CaleydoJAXBTransfer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.FileDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource.IDragEvent;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.TextDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.URLDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.URLTransfer;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class MouseLayer extends GLElementContainer implements IMouseLayer, IGLLayout2 {
	private final IGLCanvas canvas;

	private final Deque<IDropGLTarget> dropTargets = new ConcurrentLinkedDeque<>();
	private final Deque<IDragGLSource> dragSources = new ConcurrentLinkedDeque<>();

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
					System.out.println("drag start using " + source + " " + dragSources);
					active = new DNDItem(info, source);
					EventPublisher.trigger(new DragItemEvent(readOnly(active), active.source, false)
							.to(MouseLayer.this));
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
			IDragInfo info = active.getInfo();
			if (CaleydoJAXBTransfer.getInstance().isSupportedType(event.dataType) && CaleydoJAXBTransfer.isValid(info)) {
				event.data = info;
			} else if (FileTransfer.getInstance().isSupportedType(event.dataType) && info instanceof FileDragInfo) {
				event.data = ((FileDragInfo) info).getFileNames();
			} else if (URLTransfer.getInstance().isSupportedType(event.dataType) && info instanceof URLDragInfo) {
				event.data = ((URLDragInfo) info).getUrl();
			} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = info instanceof TextDragInfo ? ((TextDragInfo) info).getText() : info.getLabel();
			} else {
				event.doit = false; // invalid type
				return;
			}
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			if (active == null)
				return;
			System.out.println("finished: " + active.getType());
			active.setType(event.doit ? event.detail : DND.DROP_NONE);
			System.out.println("finished: " + active.getType() + " " + event.detail + " " + event.doit);
			// thread switch
			EventPublisher.trigger(new DragItemEvent(readOnly(active), active.source, true).to(MouseLayer.this));
			active = null;
		}
	};
	private final DropTargetListener drop = new DropTargetListener() {

		@Override
		public void dragEnter(DropTargetEvent event) {
			IDnDItem item = toItem(event);
			if (validateDropTarget(event, item) && event.detail == DND.DROP_DEFAULT)
				event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));

			if (item != null)
				EventPublisher.trigger(new DropEnterLeaveItemEvent(readOnly(item), activeDropTarget, true)
					.to(MouseLayer.this));
		}

		@Override
		public void dropAccept(DropTargetEvent event) {

		}

		@Override
		public void drop(DropTargetEvent event) {
			IDnDItem item = toItem(event);
			if (validateDropTarget(event, item)) {
				System.out.println(event.detail);
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));
				System.out.println(event.detail);
				// thread switch
				EventPublisher.trigger(new DropItemEvent(readOnly(item), activeDropTarget, true).to(MouseLayer.this));
				activeDropTarget = null;
			}
		}

		private int fromType(EDnDType type) {
			if (type == null)
				return DND.DROP_MOVE;
			switch (type) {
			case COPY:
				return DND.DROP_COPY;
			case LINK:
				return DND.DROP_LINK;
			case MOVE:
				return DND.DROP_MOVE;
			case NONE:
				return DND.DROP_NONE;
			}
			return 0;
		}

		@Override
		public void dragOver(DropTargetEvent event) {
			itemChanged(event);

		}

		private void itemChanged(DropTargetEvent event) {
			IDnDItem item = toItem(event);
			if (validateDropTarget(event, item))
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));
				// thread switch
			if (item != null)
				EventPublisher.trigger(new DropItemEvent(readOnly(item), activeDropTarget, false).to(MouseLayer.this));
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
			IDnDItem item = toItem(event);
			if (item != null)
				EventPublisher.trigger(new DropEnterLeaveItemEvent(readOnly(item), activeDropTarget, false)
						.to(MouseLayer.this));
			activeDropTarget = null;
		}
	};

	static IDnDItem readOnly(IDnDItem item) {
		return new DNDTransferItem(item.getInfo(), item.getType());
	}

	@ListenTo
	private void onDropEnterLeaveItemEvent(DropEnterLeaveItemEvent event) {
		showhideInfo(event.getItem().getInfo(), event.isEntering() ? EVisibility.VISIBLE : EVisibility.NONE);
	}

	@ListenTo(sendToMe = true)
	private void onDragItemEvent(DragItemEvent event) {
		final IDragGLSource s = event.getSource();
		final IDnDItem item = event.getItem();

		if (event.isFinished()) {
			s.onDropped(item);
			removeInfo(item.getInfo());
		} else {
			addInfo(s, item);
		}
	}

	private void addInfo(final IDragGLSource s, final IDnDItem item) {
		GLElement ui = s.createUI(item.getInfo());
		ui.setLayoutData(item.getInfo());
		if (ui != null)
			this.add(ui);
	}

	private void removeInfo(final IDragInfo item) {
		for (GLElement elem : this)
			if (elem.getLayoutDataAs(IDragInfo.class, null) == item) {
				remove(elem);
				break;
			}
	}

	private void showhideInfo(final IDragInfo item, EVisibility vis) {
		for (GLElement elem : this)
			if (elem.getLayoutDataAs(IDragInfo.class, null) == item) {
				elem.setVisibility(vis);
				break;
			}
	}

	@ListenTo(sendToMe = true)
	private void onDropItemEvent(DropItemEvent event) {
		IDropGLTarget t = event.getTarget();
		if (!dropTargets.contains(t))
			return;
		if (event.isDropping()) {
			System.out.println("drop: " + t);
			t.onDrop(event.getItem());
		} else
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
			IDragInfo info = extract(event);
			if (info != null)
				item = active = new DNDItem(info, null);
		}
		if (item != null)
			item.setType(event.detail);
		return item;
	}

	private static IDragInfo extract(DropTargetEvent event) {
		final TransferData d = event.currentDataType;
		Object obj;
		if (CaleydoJAXBTransfer.getInstance().isSupportedType(d) && (obj = CaleydoJAXBTransfer.getInstance().nativeToJava(d)) instanceof IDragInfo) {
			return (IDragInfo) obj;
		} else if (FileTransfer.getInstance().isSupportedType(d)
				&& (obj = FileTransfer.getInstance().nativeToJava(d)) instanceof String[]) {
			return new FileDragInfo((String[])obj);
		} else if (URLTransfer.getInstance().isSupportedType(d)
				&& (obj = URLTransfer.getInstance().nativeToJava(d)) instanceof String) {
			return new URLDragInfo((String)obj);
		} else if (TextTransfer.getInstance().isSupportedType(d)
				&& (obj = TextTransfer.getInstance().nativeToJava(d)) instanceof String) {
			return new TextDragInfo((String)obj);
		}
		return null;
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
		this.dragSources.add(dragSource);
	}

	@Override
	public void removeDragSource(IDragGLSource dragSource) {
		this.dragSources.remove(dragSource);
	}

	@Override
	public void addDropTarget(IDropGLTarget dropTarget) {
		this.dropTargets.add(dropTarget);
	}

	@Override
	public void removeDropTarget(IDropGLTarget dropTarget) {
		this.dropTargets.remove(dropTarget);
		if (activeDropTarget == dropTarget) {
			activeDropTarget = null;
		}
	}

	private static class DNDItem implements IDnDItem {
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
			EDnDType type;
			if ((detail & (DND.DROP_MOVE | DND.DROP_TARGET_MOVE)) != 0)
				type = EDnDType.MOVE;
			else if ((detail & DND.DROP_COPY) != 0)
				type = EDnDType.COPY;
			else if ((detail & DND.DROP_LINK) != 0)
				type = EDnDType.LINK;
			else if ((detail & DND.DROP_DEFAULT) != 0)
				type = EDnDType.MOVE;
			else
				type = EDnDType.NONE;
			this.type = type;
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

	private static class DNDTransferItem implements IDnDItem {
		private final IDragInfo info;
		private final EDnDType type;

		public DNDTransferItem(IDragInfo info, EDnDType type) {
			this.info = info;
			this.type = type;
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
