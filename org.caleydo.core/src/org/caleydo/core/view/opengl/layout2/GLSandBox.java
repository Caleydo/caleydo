/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.media.opengl.FPSCounter;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.MyAnimator;
import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.swt.SWTGLCanvasFactory;
import org.caleydo.core.view.opengl.layout2.internal.SWTLayer;
import org.caleydo.core.view.opengl.layout2.internal.SandBoxLibraryLoader;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.SimplePickingManager;
import org.caleydo.core.view.opengl.util.text.CompositeTextRenderer;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * acts as a sandbox for elements, just use {@link GLSandBox#main(String[], GLElement)} and provide a element, and run the
 * application to open a window with the element shown, without the need of the whole caleydo / eclipse overhead
 *
 * perfect for prototyping
 *
 * supports picking, textures, ...
 *
 * @author Samuel Gratzl
 *
 */
public class GLSandBox implements GLEventListener, IGLElementParent, IGLElementContext {
	private final GLAnimatorControl animator;
	private final WindowGLElement root;

	private final ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 100, 100, 0, -20, 20);

	private final SimplePickingManager pickingManager = new SimplePickingManager();

	protected final Shell shell;
	protected final IGLCanvas canvas;
	private final ISWTLayer swtLayer;
	protected boolean renderPick;

	private GLPadding padding = GLPadding.ZERO;

	protected final QueuedEventListenerManager eventListeners = EventListenerManagers.createQueued();

	private GLContextLocal local;

	private boolean dirty = true;
	/**
	 * @param canvas
	 */
	public GLSandBox(Shell parentShell, String title, GLElement root, GLPadding padding, Dimension dim) {
		this.shell = parentShell;
		this.shell.setText(title);
		this.shell.setLayout(new GridLayout(1, true));
		this.shell.setSize(dim.width, dim.height);

		IGLCanvasFactory canvasFactory = new SWTGLCanvasFactory();

		GLCapabilities caps = createCapabilities();


		this.canvas = canvasFactory.create(caps, shell);
		canvas.asComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.swtLayer = new SWTLayer(canvas);

		this.padding = padding;
		canvas.addGLEventListener(this);

		this.animator = new MyAnimator(MyPreferences.getFPS());

		this.animator.add(canvas.asGLAutoDrawAble());
		canvas.asGLAutoDrawAble().setAutoSwapBufferMode(true);

		// ENABLE to print the fps to System.err
		animator.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, System.err);

		this.canvas.addMouseListener(pickingManager.getListener());
		this.canvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyReleased(IKeyEvent e) {
				if (e.isKey('p')) {
					renderPick = !renderPick;
				}
			}

			@Override
			public void keyPressed(IKeyEvent e) {

			}
		});
		this.root = new WindowGLElement(root);

		animator.start();
	}

	protected GLCapabilities createCapabilities() {
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setStencilBits(1);
		caps.setDoubleBuffered(true);
		caps.setAlphaBits(8);
		return caps;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return GLLayouts.resolveLayoutDatas(clazz, default_, canvas, this.local);
	}

	/**
	 * @return the root, see {@link #root}
	 */
	public GLElement getRoot() {
		return this.root.getRoot();
	}

	@Override
	public int registerPickingListener(IPickingListener l) {
		return registerPickingListener(l, 0);
	}

	@Override
	public int registerPickingListener(IPickingListener l, int objectId) {
		return pickingManager.register(l, objectId);
	}

	@Override
	public void unregisterPickingListener(int pickingID) {
		pickingManager.unregister(pickingID);
	}

	@Override
	public void repaintPick() {

	}


	@Override
	public Vec2f toAbsolute(Vec2f relative) {
		relative.add(new Vec2f(padding.left, padding.top));
		return relative;
	}

	@Override
	public Vec2f toRelative(Vec2f absolute) {
		absolute.sub(new Vec2f(padding.left, padding.top));
		return absolute;
	}

	@Override
	public void init(GLElement element) {
		eventListeners.register(element, null, AGLElementView.isNotBaseClass);
	}

	@Override
	public void takeDown(GLElement element) {
		eventListeners.unregister(element);
	}

	@Override
	public IMouseLayer getMouseLayer() {
		return root.getMouseLayer();
	}

	@Override
	public IPopupLayer getPopupLayer() {
		return root.getPopupLayer();
	}

	@Override
	public final ISWTLayer getSWTLayer() {
		return swtLayer;
	}

	@Override
	public boolean moved(GLElement child) {
		return true;
	}



	@Override
	public IGLElementParent getParent() {
		return null;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		AGLView.initGLContext(gl);

		ITextRenderer text_plain = createTextRenderer(ETextStyle.PLAIN);
		ITextRenderer text_bold = createTextRenderer(ETextStyle.BOLD);
		ITextRenderer text_italics = createTextRenderer(ETextStyle.ITALIC);
		IResourceLocator loader = createResourceLocator();
		TextureManager textures = new TextureManager(new ResourceLoader(loader));


		this.local = new GLContextLocal(text_plain, text_bold, text_italics, textures, loader);

		gl.glLoadIdentity();
		this.root.setParent(this);
		this.root.init(this);
	}

	protected IResourceLocator createResourceLocator() {
		return ResourceLocators.chain(
				ResourceLocators.classLoader(root.getClass().getClassLoader()), ResourceLocators.FILE);
	}

	protected ITextRenderer createTextRenderer(ETextStyle style) {
		return new CompositeTextRenderer(style, 8, 16, 24, 40);
	}

	@Override
	public DisplayListPool getDisplayListPool() {
		return local.getPool();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		root.takeDown();
		GL2 gl = drawable.getGL().getGL2();
		local.destroy(gl);
	}

	private float getWidth() {
		return viewFrustum.getRight();
	}

	private float getHeight() {
		return viewFrustum.getBottom();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final int deltaTimeMs = local.getDeltaTimeMs();
		eventListeners.processEvents();

		GL2 gl = drawable.getGL().getGL2();
		final GLGraphics g = new GLGraphics(gl, local, true, deltaTimeMs);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.375f, 0.375f, 0);

		float paddedWidth = getWidth() - padding.left - padding.right;
		float paddedHeight = getHeight() - padding.top - padding.bottom;
		g.move(padding.left, padding.right);

		if (dirty) {
			root.setBounds(0, 0, paddedWidth, paddedHeight);
			root.relayout();
			dirty = false;
		}

		// 1 pass: picking
		Runnable toRender = new Runnable() {
			@Override
			public void run() {
				root.renderPick(g);
			}
		};

		Vec2f mousePos = pickingManager.getCurrentMousePos();
		if (mousePos != null) {
			root.getMouseLayer().setBounds(mousePos.x() - padding.left, mousePos.y() - padding.top,
					getWidth() - mousePos.x(), getHeight() - mousePos.y());
			root.getMouseLayer().relayout();
		}
		pickingManager.doPicking(g.gl, toRender);

		// 2. pass: layout
		root.layout(deltaTimeMs);

		// 3. pass: rendering
		if (renderPick)
			root.renderPick(g);
		else
			root.render(g);

		g.move(-padding.left, -padding.right);
	}

	@Override
	public void relayout() {
		dirty = true;
	}

	@Override
	public void repaint() {

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		viewFrustum.setRight(canvas.getDIPWidth());
		viewFrustum.setBottom(canvas.getDIPHeight());

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		viewFrustum.setProjectionMatrix(gl);

		relayout();

	}


	public static void main(String[] args, IGLRenderer renderer) {
		main(args, new GLElement(renderer));
	}

	public static void main(String[] args, GLElement root) {
		main(args, root, GLPadding.ZERO);
	}

	public static void main(String[] args, GLElement root, GLPadding padding) {
		main(args, root, padding, new Dimension(800, 600));
	}

	public static void main(String[] args, GLElement root, GLPadding padding, Dimension dim) {
		main(args, GLSandBox.class, root.getClass().getSimpleName(), root, padding, dim);
	}

	public static void main(String[] args, Class<? extends GLSandBox> toRun, Object... toRunArgs) {
		new SandBoxLauncher(args, toRun.getCanonicalName(), toRunArgs).run();
	}



	private static class SandBoxLauncher implements Runnable{
		private final String[] args;
		private final ClassLoader wrapper;
		private final Class<? extends GLSandBox> toRun;
		private final Object[] toRunArgs;
		private GLSandBox sandbox;


		public SandBoxLauncher(String[] args, String cannonicalClassToRun, Object[] toRunArgs) {
			this.args = args;
			this.wrapper = new URLClassLoader(new URL[0], getClass().getClassLoader());
			try {
				this.toRun = wrapper.loadClass(cannonicalClassToRun).asSubclass(GLSandBox.class);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException();
			}
			this.toRunArgs = toRunArgs;
		}

		@Override
		public void run() {
			// use my class for library loading
			System.setProperty("jnlp.launcher.class", SandBoxLibraryLoader.class.getCanonicalName());
			try {
				Display display = new Display();
				final Shell splash = createSplash(display);
				final Shell content = new Shell(display);
				splash.open();
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						runContent(content);
						content.open();
						splash.close();
					}
				});
				while (!splash.isDisposed() || !content.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
				if (sandbox != null)
					sandbox.animator.stop();
				display.dispose();
			} catch (IllegalArgumentException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.err.flush();
				System.out.flush();
				System.exit(0);
			}
		}

		protected Shell createSplash(Display display) {
			final Shell splash = new Shell(display, SWT.ON_TOP);
			splash.setLayout(new GridLayout(1, true));
			Label l = new Label(splash, SWT.NONE);
			l.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			l.setText("Caleydo Sandbox");
			splash.setSize(200, 100);
			Rectangle splashRect = splash.getBounds();
			Rectangle displayRect = display.getBounds();
			int x = (displayRect.width - splashRect.width) / 2;
			int y = (displayRect.height - splashRect.height) / 2;
			splash.setLocation(x, y);
			return splash;
		}

		private void runContent(Shell shell) {
			Object[] realArgs = new Object[toRunArgs.length + 1];
			realArgs[0] = shell;
			for (int i = 0; i < toRunArgs.length; ++i)
				realArgs[i + 1] = toRunArgs[i];
			try {
				this.sandbox = (GLSandBox) toRun.getConstructors()[0].newInstance(realArgs);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
