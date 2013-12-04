/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.internal.CaleydoTransfer;
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
import org.caleydo.core.view.opengl.layout2.dnd.IRemoteDragInfoUICreator;
import org.caleydo.core.view.opengl.layout2.dnd.IUIDragInfo;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class MouseLayer extends GLElementContainer implements IMouseLayer, IGLLayout2 {
	private final IGLCanvas canvas;

	/**
	 * current possible drop targets
	 */
	private final Deque<IDropGLTarget> dropTargets = new ConcurrentLinkedDeque<>();
	/**
	 * current possible drag sources
	 */
	private final Deque<IDragGLSource> dragSources = new ConcurrentLinkedDeque<>();
	private final Collection<IRemoteDragInfoUICreator> creators = new ArrayList<>(1);

	volatile IDropGLTarget activeDropTarget;
	volatile DnDItem active;

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
			DragEvent e = new DragEvent(toPoint(event.x, event.y, false));
			for(IDragGLSource source : dragSources) {
				IDragInfo info = source.startSWTDrag(e);
				if (info != null) {
					active = new DnDItem(info, source);
					active.setMousePos(e.getMousePos());
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
			if (CaleydoTransfer.getInstance().isSupportedType(event.dataType) && CaleydoTransfer.isValid(info)) {
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
				return; // strange shouldn't happen
			active.setType(event.doit ? event.detail : DND.DROP_NONE);
			// thread switch
			EventPublisher.trigger(new DragItemEvent(readOnly(active), active.source, true).to(MouseLayer.this));
			active = null;
		}
	};
	private final DropTargetListener drop = new DropTargetListener() {
		private int acceptDetail;
		private Vec2f acceptPoint;
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
			// I don't know why but I need to transfer the method from the accept to the drop
			acceptDetail = event.detail;
			acceptPoint = toPoint(event.x, event.y, true);
		}

		@Override
		public void drop(DropTargetEvent event) {
			event.detail = acceptDetail;
			DnDItem item = toItem(event);
			if (validateDropTarget(event, item)) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));
				// thread switch
				item.setMousePos(acceptPoint);
				EventPublisher.trigger(new DropItemEvent(readOnly(item), activeDropTarget, true)
						.to(MouseLayer.this));
				activeDropTarget = null;
			}
			if (active != null && active.source == null) // cleanup remote
				active = null;
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
			DnDItem item = toItem(event);
			if (validateDropTarget(event, item)) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));
				item.setMousePos(toPoint(event.x, event.y, true));
				EventPublisher.trigger(new DropItemEvent(readOnly(item), activeDropTarget, false).to(MouseLayer.this));
			}
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
		public void dragLeave(final DropTargetEvent event) {
			// later to be after the optional drop event
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (activeDropTarget != null) {
						IDnDItem item = toItem(event);
						if (item != null)
							EventPublisher.trigger(new DropEnterLeaveItemEvent(readOnly(item), activeDropTarget, false)
									.to(MouseLayer.this));
						activeDropTarget = null;
					}
				}
			});
		}
	};


	static IDnDItem readOnly(IDnDItem item) {
		return new DNDTransferItem(item.getInfo(), item.getType(), item.getMousePos());
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	protected Vec2f toPoint(int x, int y, boolean absolute) {
		if (absolute) {
			Point p = canvas.asComposite().toControl(x, y);
			x = p.x;
			y = p.y;
		}
		return canvas.toDIP(new java.awt.Point(x, y));
	}

	@ListenTo(sendToMe = true)
	private void onDropEnterLeaveItemEvent(DropEnterLeaveItemEvent event) {
		IDnDItem item = event.getItem();
		if (event.isEntering()) {
			if (!showhideInfo(item.getInfo(), EVisibility.VISIBLE)) {
				addInfo(item instanceof DnDItem ? ((DnDItem) item).source : null, item);
			}
		} else {
			removeInfo(item.getInfo());
		}

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
		final IDragInfo info = item.getInfo();
		if (isAlreadyThere(info))
			return;

		GLElement ui = null;
		for (IRemoteDragInfoUICreator creator : creators)
			if ((ui = creator.createUI(info)) != null)
				break;
		if (ui == null && s != null)
			ui = s.createUI(info);
		if (ui == null && info instanceof IUIDragInfo)
			ui = ((IUIDragInfo) info).createUI();
		if (ui == null)
			return;
		ui.setLayoutData(info);
		this.add(ui);
	}

	private boolean isAlreadyThere(final IDragInfo info) {
		for (GLElement elem : this)
			if (elem.getLayoutDataAs(IDragInfo.class, null) == info)
				return true; // already there
		return false;
	}

	private void removeInfo(final IDragInfo info) {
		for (GLElement elem : this) {
			IDragInfo d = elem.getLayoutDataAs(IDragInfo.class, null);
			if (d == info) {
				remove(elem);
				break;
			}
		}
	}

	private boolean showhideInfo(final IDragInfo item, EVisibility vis) {
		for (GLElement elem : this)
			if (elem.getLayoutDataAs(IDragInfo.class, null) == item) {
				elem.setVisibility(vis);
				return true;
			}
		return false;
	}

	@ListenTo(sendToMe = true)
	private void onDropItemEvent(DropItemEvent event) {
		IDropGLTarget t = event.getTarget();
		if (event.isDropping()) {
			t.onDrop(event.getItem());
			removeInfo(event.getItem().getInfo());
		} else
			t.onItemChanged(event.getItem());
	}

	/**
	 * @param item
	 * @return
	 */
	protected IDropGLTarget findDropTarget(IDnDItem item) {
		for (IDropGLTarget target : dropTargets) {
			if (target.canSWTDrop(item)) {
				return target;
			}
		}
		return null;
	}

	/**
	 * @param event
	 * @return
	 */
	protected DnDItem toItem(DropTargetEvent event) {
		DnDItem item = null;
		if (active != null)
			item = active;
		else {
			IDragInfo info = extract(event);
			if (info != null)
				item = active = new DnDItem(info, null);
		}
		if (item != null)
			item.setType(event.detail);
		return item;
	}

	private static IDragInfo extract(DropTargetEvent event) {
		final TransferData d = event.currentDataType;
		Object obj;
		if (CaleydoTransfer.getInstance().isSupportedType(d) && (obj = CaleydoTransfer.getInstance().nativeToJava(d)) instanceof IDragInfo) {
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

	@Override
	public void addRemoteDragInfoUICreator(IRemoteDragInfoUICreator creator) {
		this.creators.add(creator);
	}

	/**
	 * @param detail
	 * @return
	 */
	static EDnDType toType(int detail) {
		switch (detail) {
		case DND.DROP_MOVE:
		case DND.DROP_TARGET_MOVE:
			return EDnDType.MOVE;
		case DND.DROP_COPY:
			return EDnDType.COPY;
		case DND.DROP_LINK:
			return EDnDType.LINK;
		case DND.DROP_DEFAULT:
			return EDnDType.NONE;
		default:
			return EDnDType.NONE;
		}
	}

	private static class DnDItem implements IDnDItem {
		private final IDragGLSource source;
		private final IDragInfo info;
		private EDnDType type = EDnDType.MOVE;
		private Vec2f mousePos;

		public DnDItem(IDragInfo info, IDragGLSource source) {
			this.info = info;
			this.source = source;
		}

		/**
		 * @param mousePos
		 *            setter, see {@link mousePos}
		 */
		public void setMousePos(Vec2f mousePos) {
			this.mousePos = mousePos;
		}

		/**
		 * @return the mousePos, see {@link #mousePos}
		 */
		@Override
		public Vec2f getMousePos() {
			return mousePos;
		}

		/**
		 * @param detail
		 */
		public void setType(int detail) {
			this.type = toType(detail);
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
		private final Vec2f mousePos;

		public DNDTransferItem(IDragInfo info, EDnDType type, Vec2f mousePos) {
			this.info = info;
			this.type = type;
			this.mousePos = mousePos;
		}

		/**
		 * @return the mousePos, see {@link #mousePos}
		 */
		@Override
		public Vec2f getMousePos() {
			return mousePos;
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
		public Vec2f getMousePos() {
			return offset;
		}
	}
}
