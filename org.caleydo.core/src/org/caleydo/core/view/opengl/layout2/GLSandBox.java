package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;

import javax.media.opengl.FPSCounter;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.swt.SWTGLCanvasFactory;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.SimplePickingManager;
import org.caleydo.core.view.opengl.util.text.CompositeTextRenderer;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.util.FPSAnimator;

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
	private final FPSAnimator animator;
	private TextureManager textures;
	private ITextRenderer text;
	private final WindowGLElement root;
	private boolean dirty = true;

	protected boolean tracingGL = false;
	private final ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 100, 100, 0, -20, 20);

	private final DisplayListPool pool = new DisplayListPool();

	private final SimplePickingManager pickingManager = new SimplePickingManager();

	protected final Shell shell;
	protected final IGLCanvas canvas;
	private final IResourceLocator loader;
	protected boolean renderPick;

	private GLPadding padding = GLPadding.ZERO;

	protected final QueuedEventListenerManager eventListeners = EventListenerManagers.createQueued();

	private final TimeDelta timeDelta = new TimeDelta();
	/**
	 * @param canvas
	 */
	public GLSandBox(Shell parentShell, String title, GLElement root, GLPadding padding, Dimension dim) {
		this.shell = parentShell;
		this.shell.setText(title);
		this.shell.setLayout(new GridLayout(1, true));
		this.shell.setSize(dim.width, dim.height);

		this.loader = ResourceLocators.chain(ResourceLocators.classLoader(root.getClass().getClassLoader()),
				ResourceLocators.FILE);

		IGLCanvasFactory canvasFactory = new SWTGLCanvasFactory();

		GLCapabilities caps = createCapabilities();


		this.canvas = canvasFactory.create(caps, shell);
		canvas.asComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.padding = padding;
		canvas.addGLEventListener(this);

		this.animator = new FPSAnimator(canvas.asGLAutoDrawAble(), 30);
		animator.setPrintExceptions(true);
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
	public IPickingListener createTooltip(ILabelProvider label) {
		return canvas.createTooltip(label);
	}

	@Override
	public void showContextMenu(Iterable<? extends AContextMenuItem> items) {
		new SWTGLCanvasFactory().showPopupMenu(canvas, items);
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
	public TextureManager getTextureManager() {
		return textures;
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

		text = new CompositeTextRenderer(8, 16, 24, 40);
		textures = new TextureManager(new ResourceLoader(loader));

		gl.glLoadIdentity();
		this.root.setParent(this);
		this.root.init(this);
	}

	@Override
	public DisplayListPool getDisplayListPool() {
		return pool;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		root.takeDown();
		GL2 gl = drawable.getGL().getGL2();
		pool.deleteAll(gl);
	}

	private float getWidth() {
		return viewFrustum.getRight();
	}

	private float getHeight() {
		return viewFrustum.getBottom();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final int deltaTimeMs = timeDelta.getDeltaTimeMs();
		eventListeners.processEvents();

		GL2 gl = drawable.getGL().getGL2();
		final GLGraphics g = tracingGL ? new GLGraphicsTracing(gl, text, textures, loader, true, deltaTimeMs)
				: new GLGraphics(gl,
				text, textures, loader, true, deltaTimeMs);

		// I have no idea, why I always need to initialize the context again
		AGLView.initGLContext(gl);


		//gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
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

		// first pass: layout
		root.layout(deltaTimeMs);

		// second pass: picking
		Runnable toRender = new Runnable() {
			@Override
			public void run() {
				root.renderPick(g);
			}
		};

		Point mousePos = pickingManager.getCurrentMousePos();
		if (mousePos != null) {
			root.getMouseLayer().setBounds(mousePos.x - padding.left, mousePos.y - padding.top,
					getWidth() - mousePos.x, getHeight() - mousePos.y);
			root.getMouseLayer().relayout();
		}
		pickingManager.doPicking(g.gl, toRender);

		// third pass: rendering
		if (renderPick)
			root.renderPick(g);
		else
			root.render(g);

		g.move(-padding.left, -padding.right);
		g.destroy();
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

		viewFrustum.setRight(width);
		viewFrustum.setBottom(height);

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		viewFrustum.setProjectionMatrix(gl);

		relayout();

	}

	@Override
	public void setCursor(final int swtCursorConst) {
		final Composite c = canvas.asComposite();
		final Display d = c.getDisplay();
		d.asyncExec(new Runnable() {
			@Override
			public void run() {
				c.setCursor(swtCursorConst < 0 ? null : d.getSystemCursor(swtCursorConst));
			}
		});
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
		new SandBoxLauncher(args, toRun, toRunArgs).run();
	}

	private static class SandBoxLauncher implements Runnable{
		private final String[] args;
		private final Class<? extends GLSandBox> toRun;
		private final Object[] toRunArgs;
		private GLSandBox sandbox;


		public SandBoxLauncher(String[] args, Class<? extends GLSandBox> toRun, Object[] toRunArgs) {
			this.args = args;
			this.toRun = toRun;
			this.toRunArgs = toRunArgs;
		}

		@Override
		public void run() {
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
			} catch (java.lang.NoClassDefFoundError e) {
				// expected error as we aren't part of eclipse
				System.exit(0);
			} finally {
				System.err.flush();
				System.out.flush();
				// System.exit(0);
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
