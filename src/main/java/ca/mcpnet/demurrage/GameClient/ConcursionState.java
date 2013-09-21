package ca.mcpnet.demurrage.GameClient;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import ca.mcpnet.demurrage.GameClient.GL.Axis;
import ca.mcpnet.demurrage.GameClient.GL.Camera;
import ca.mcpnet.demurrage.GameClient.GL.ConcursionEdge;
import ca.mcpnet.demurrage.GameClient.GL.ConcursionPoint;
import ca.mcpnet.demurrage.GameClient.GL.GlowSphere;
import ca.mcpnet.demurrage.GameClient.GL.Projection;
import ca.mcpnet.demurrage.GameClient.GL.WireSphere;
import ca.mcpnet.demurrage.GameClient.jme.Vector3f;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.BindingList;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.Concursion;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.CosmNode;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.LinkBinding;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.LinkTerminus;
import de.matthiasmann.twl.CallbackWithReason;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ListBox.CallbackReason;
import de.matthiasmann.twl.ListBoxDisplay;
import de.matthiasmann.twl.Rect;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.ListModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.renderer.Font;

public class ConcursionState extends ClientState {

	private static final float ZOOMOUTRADIUS = 5f;
	// Main window
	private Projection _projection;
	private Camera _camera;
	private ConcursionPoint _concursionPoint;
	private ConcursionEdge _concursionEdge;
	private GlowSphere _glowSphere;
	private WireSphere _wireSphere;
	// Sub window
	private Rect _subWindowRect;
	private Projection _subProjection;
	private Camera _subCamera;
	private Axis _axis;

	private long _timeDelta;
	private RootPane _concursionStateRootPane;

	public ConcursionState(GameClient gc) {
		super(gc);
		
		// Main Window
		_projection = new Projection();
		_camera = new Camera();
		
		_concursionPoint = new ConcursionPoint();
		_concursionEdge = new ConcursionEdge();
		_glowSphere = new GlowSphere();
		_wireSphere = new WireSphere();
		// Sub Window
		_subWindowRect = new Rect();
		_subWindowRect.setXYWH(20, 20, 200, 200);
		_subProjection = new Projection();
		_subCamera = new Camera();
		
		_axis = new Axis();
		_timeDelta = 0;

		_rootPane = _concursionStateRootPane = new RootPane(this);
	}

	@Override
	public void onEnterState() {
		_gameClient.getGUI().setRootPane(_rootPane);
		
		resetLinkBindingModel();
		
		// Do the main window
		// Set up the projection matrix
		float aspect = (float) Display.getWidth() / (float) Display.getHeight();
		_projection.fromPerspective(60.0f, aspect, 0.1f, 10.0f);
		
		_camera.setRadius(ZOOMOUTRADIUS);
		_camera.setUpVector(Vector3f.UNIT_Y);
		_camera.setTarget(Vector3f.ZERO);
		_camera.lookAtTarget();
		
		// Do the subwindow
		aspect = _subWindowRect.getWidth() / _subWindowRect.getHeight();
		_subProjection.fromPerspective(60f, aspect, 0.1f, 10f);
		
		_subCamera.setRadius(3f);
		_subCamera.setUpVector(Vector3f.UNIT_Y);
		_subCamera.setTarget(Vector3f.ZERO);
		_subCamera.lookAtTarget();
	}

	@Override
	public void onExitState() {
		// TODO Auto-generated method stub

	}

	public void setHoverCosm(long cnId) {
		_hoverCosmId = cnId;
		_concursionStateRootPane._cosmTargetWidget.snapToTarget(200);
		_concursionStateRootPane._cosmTargetWidget.setVisible(true);
	}
	
	/**
	 * If the current hover cosm matches the given Id
	 * then clear it
	 * @param cnId
	 */
	public void matchAndClearHoverCosm(long cnId) {
		if (_hoverCosmId == cnId) {
			_hoverCosmId = 0;
			_concursionStateRootPane._cosmTargetWidget.setVisible(false);
		}
	}

	private boolean _lastRightButtonDown = false;
	private long _hoverCosmId = 0;
	@Override
	public void update() {
		long curtime = (System.currentTimeMillis() + _timeDelta);

		Vector3f mouseVector = new Vector3f(Mouse.getX(),Mouse.getY(),0);
		float dx = Mouse.getDX();
		float dy = Mouse.getDY();
		float dr = Mouse.getDWheel();
		
		boolean leftButtonDown = Mouse.isButtonDown(0);
		boolean rightButtonDown = Mouse.isButtonDown(1);
		boolean middleButtonDown = Mouse.isButtonDown(2);
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (_focusCosmNode != null) {
				_focusCosmNode = null;
				_camera.startTargetTransition(Vector3f.ZERO, 1000);
				_camera.startRadiusTransition(ZOOMOUTRADIUS, 1000);
				_concursionStateRootPane._linkBindingBox.setSelected(-1);
			}
		}
		/*
		while (Mouse.next() && Mouse.getEventButton() != -1) {
			Log.debug(Mouse.getEventButton() + " " + Mouse.getEventButtonState());			
		}
		*/
		
		// _camera.addRadius(dr/10000f);
		if (rightButtonDown && !_lastRightButtonDown)
			Mouse.setGrabbed(true);
		if (!rightButtonDown && _lastRightButtonDown)
			Mouse.setGrabbed(false);
		if (rightButtonDown) {
			_camera.addHorizontalRotationAboutTarget(-dx/400f);
			_camera.addVerticalRotationAboutTarget(-dy/400f);
		}
		_lastRightButtonDown = rightButtonDown;
		_camera.update();

		// Set up camera and view matrix
//		float a = (float) (time/2000 % (FastMath.TWO_PI));
		// float b = (float) (time/500 % (FastMath.TWO_PI));
//		float c = (float) (time/15000 % (FastMath.TWO_PI));
//		float radius = FastMath.sin(a)*2.0f + 2.5f;
//		float radius = 3.0f;
		// _camera.setPosition(FastMath.sin(c)*radius, 0.0f, FastMath.cos(c)*radius);

		// START RENDER FOR MAIN WINDOW
		
		// Set up matrices for main window
		_gameClient.getShaderProgramManager().setShaderProgramMatrixes(_projection.getProjectionMatrixFloatBuffer(),_camera.getViewMatrixFloatBuffer());

		// Render Solids first
    	glEnable(GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        // Throw a test sphere in for now
        _wireSphere.setTranslation(Vector3f.ZERO);
        _wireSphere.setScale(0.1f);
        _wireSphere.setColor(0f, 0.5f, 0f, 1f);
        _wireSphere.draw();
        
		// Render Blended objects second
		glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		glDisable(GL_DEPTH_TEST);

		// Draw all of the concursion nodes
		// - light them up red if the cursor is close to them
		// - light them up blue if they're connected
		// - put the target square over them if they're marked as hovering
		// - throw in a star if they're within the bounding volume
		Concursion concursion = _gameClient.getConcursion();
		Iterator<CosmNode> citr = concursion.getCosmNodeIterator();
		Vector3f tempVector = new Vector3f();
		while (citr.hasNext()) {
			
			CosmNode cnode = citr.next();
			tempVector.set(cnode.getX(), (float) cnode.getZ().f(curtime), cnode.getY());
			// Find out the pixel coords of this node
			_camera.applyViewMatrix(tempVector);
			_projection.applyProjectionMatrix(tempVector);
			_projection.translateToScreenCoordinates(tempVector);
			_glowSphere.setTranslation(cnode.getX(), (float) cnode.getZ().f(curtime), cnode.getY());
			float R = 0.01f;
			float distance = mouseVector.distance(tempVector);
			if (distance < 50.0f) {
				R = (50.0f - distance)/150.0f;
			}
			if (cnode.getId() == _hoverCosmId) {
				_glowSphere.setColor(R, 0.2f, 0.0f, 1.0f);
				_concursionStateRootPane._cosmTargetWidget.setTargetPosition((int) tempVector.x, Display.getHeight() - (int) tempVector.y);
			} else if (cnode.getState() == CosmNode.CosmState.connected) {
				_glowSphere.setColor(R, 0.05f, 0.2f, 1.0f);
			} else {
				_glowSphere.setColor(R+0.03f, 0.0f, 0.04f, 1.0f);				
			}
			_glowSphere.draw();
		}
		
		// Go over the list of bound termini
		BindingList bindingList = _gameClient.getBindingList();
		Iterator<LinkBinding> lbitr = bindingList.getLinkBindingIterator();
		_concursionPoint.setColor(0.1f, 0, 0, 1);
		while (lbitr.hasNext()) {
			LinkBinding lb = lbitr.next();
			// Look up the terminus
			LinkTerminus lt = concursion.getLinkTerminus(lb.getLinkTerminusId());
			CosmNode cnode = concursion.getCosmNode(lt.getCosmNodeId());
			// _concursionPoint.setTranslation(cnode.getX(), (float) cnode.getZ().f(curtime), cnode.getY());
			// _concursionPoint.draw();
		}
		// Go over the list of termini
		if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
			Iterator<LinkTerminus> ltitr = concursion.getLinkTerminusIterator();
			while (ltitr.hasNext()) {
				LinkTerminus lt = ltitr.next();
				System.out.println(lt.asOneLineString());
			}
		}
		/*
		Vector3f tempVectorB = new Vector3f();
		Iterator<CosmEdge> eitr = concursion.getCosmEdgeIterator();
		while (eitr.hasNext()) {
			CosmEdge enode = eitr.next();
			enode.getCosmAID();
			CosmNode cosmA = concursion.getCosmNode(enode.getCosmAID());
			tempVector.set(cosmA.getX(), (float) cosmA.getZ().f(curtime), cosmA.getY());
			CosmNode cosmB = concursion.getCosmNode(enode.getCosmBID());
			tempVectorB.set(cosmB.getX(), (float) cosmB.getZ().f(curtime), cosmB.getY());
			_concursionEdge.setPoints(tempVector,tempVectorB);
			_concursionEdge.setColor(0.1f, 0.4f, 0, 1);
			_concursionEdge.draw();			
		}
		*/

		/*
		// Second pass to draw
		itr = _gameClient.getConcursion().getCosmNodeIterator();
		while (itr.hasNext()) {
			
			CosmNode cnode = itr.next();
			_concursionPoint.setTranslation(cnode.getX(), (float) cnode.getZ().f(curtime), cnode.getY());
			_concursionPoint.draw();
		}
		*/
		
		// Sub Window Work
		_subCamera.addHorizontalRotationAboutTarget(-dx/400f);
		_subCamera.addVerticalRotationAboutTarget(-dy/400f);
		_subCamera.update();

		// Set up matrices for sub window
		_gameClient.getShaderProgramManager().setShaderProgramMatrixes(_subProjection.getProjectionMatrixFloatBuffer(), _subCamera.getViewMatrixFloatBuffer());
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
    	GL11.glViewport(_subWindowRect.getX(), _subWindowRect.getY(), _subWindowRect.getWidth(), _subWindowRect.getHeight());
    	GL11.glScissor(_subWindowRect.getX(), _subWindowRect.getY(), _subWindowRect.getWidth(), _subWindowRect.getHeight());
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

        _axis.draw();
        // Reset things so GUI works
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
    	GL11.glViewport(0,0,Display.getWidth(),Display.getHeight());
    	GL11.glScissor(0,0,Display.getWidth(),Display.getHeight());
	}

	private LinkBindingEntry createLinkBindingEntry(LinkBinding lb) {
		// Look up terminus name
		long ltId = lb.getLinkTerminusId();
		LinkTerminus lt = _gameClient.getConcursion().getLinkTerminus(ltId);
		// Look up cosm name
		long cnId = lt.getCosmNodeId();
		CosmNode cn = _gameClient.getConcursion().getCosmNode(cnId);
		return new LinkBindingEntry(lb.getId(),lb.getName(),lt.getId(), cn.getId(), cn.getName());
	}
	
	public void addLinkBinding(LinkBinding lb) {
		SimpleChangableListModel<LinkBindingEntry> lbm = _concursionStateRootPane.getLinkBindingModel();
		ListBox<LinkBindingEntry> lbb = _concursionStateRootPane._linkBindingBox;

		Font font = _gameClient.getTheme().findThemeInfo(lbb.getTheme()).getFont("font");

		// Add the link binding
		LinkBindingEntry lbe = createLinkBindingEntry(lb);
		_concursionStateRootPane.getLinkBindingModel().addElements(lbe);
		// Calculate the width
		int newwidth = font.computeTextWidth(lbe.toString());
		if (newwidth > lbb.getWidth()) {
			lbb.setCellWidth(newwidth);
		}
		if (_gameClient.getBindingList().getNumEntries() != lbm.getNumEntries()) {
			throw new RuntimeException("Global BindingList out of sync with Concursion LinkBindingList");
		}
	}
	
	public void setCosmName(long cnId, String name) {
		// Fix up any link binding entries
		SimpleChangableListModel<LinkBindingEntry> lbm = _concursionStateRootPane.getLinkBindingModel();
		for (int i = 0; i < lbm.getNumEntries(); i++) {
			LinkBindingEntry lbe = lbm.getEntry(i);
			if (cnId == lbe._cnId) {
				lbm.setElement(i, createLinkBindingEntry(_gameClient.getBindingList().getLinkBinding(lbe._lbId)));
				// FIXME Find out why this doesn't fire a change event 

			}
		}
	}
	
	public void resetLinkBindingModel() {
		// Only update if all the required subscriptions are active
		if (_gameClient.getBindingList() == null)
			return;
		SimpleChangableListModel<LinkBindingEntry> lbm = _concursionStateRootPane.getLinkBindingModel();
		ListBox<LinkBindingEntry> lbb = _concursionStateRootPane._linkBindingBox;

		Font font = _gameClient.getTheme().findThemeInfo(lbb.getTheme()).getFont("font");
		
		// Find out which link binding is currently chosen
		long selectedLbId = -1;
		int selected = lbb.getSelected();
		if (selected >= 0) {
			selectedLbId = lbm.getEntry(selected)._lbId;
		}
		
		// Rebuild the model
		int maxwidth = 0;
		lbm.clear();
		Iterator<LinkBinding> lbitr = _gameClient.getBindingList().getLinkBindingIterator();
		while (lbitr.hasNext()) {
			LinkBinding lb = lbitr.next();
			// Add the link binding
			LinkBindingEntry lbe = createLinkBindingEntry(lb);
			_concursionStateRootPane.getLinkBindingModel().addElements(lbe);
			// Calculate the width
			int thiswidth = font.computeTextWidth(lbe.toString());
			if (thiswidth > maxwidth)
				maxwidth = thiswidth;
			// Check if we need to set this as selected
			if (lb.getId() == selectedLbId) {
				lbb.setSelected(lbm.getNumEntries()-1);
			}
		}
		lbb.setCellWidth(maxwidth+5);
		lbb.invalidateLayout();
	}
	
	CosmNode _focusCosmNode = null;
	
	void selectCosm(long cnId) {
		// Look up the cosm
		CosmNode newNode = _gameClient.getConcursion().getCosmNode(cnId);
		_concursionStateRootPane.getCosmReadout().newText(newNode.getName());
		if (newNode != _focusCosmNode) {
			_focusCosmNode = newNode;
			_camera.startTargetTransition(new Vector3f(_focusCosmNode.getX(), (float) _focusCosmNode.getZ().f(System.currentTimeMillis()), _focusCosmNode.getY()), 500);
			_camera.startRadiusTransition(0.2f, 500);
		}
	}

	public static class RootPane extends Widget {
		private SimpleChangableListModel<LinkBindingEntry> _linkBindingModel;
		private HoverListBox<LinkBindingEntry> _linkBindingBox;
		private TargetWidget _cosmTargetWidget;
		private Widget _cosmWindow;
		private ReadoutWidget _cosmReadout;
		private Rect _subWindowRect;
		
		public RootPane(final ConcursionState cs) {
			_subWindowRect = cs._subWindowRect;
			
			_cosmReadout = new ReadoutWidget();
			_cosmReadout.setVisible(true);
			add(_cosmReadout);
			
	        _cosmWindow = new Widget();
	        _cosmWindow.setTheme("cosmwindow");
	        _cosmWindow.setVisible(true);
	        add(_cosmWindow);

	        setTheme("");
	        _linkBindingModel = new SimpleChangableListModel<LinkBindingEntry>();
	        _linkBindingBox = new HoverListBox<LinkBindingEntry>(_linkBindingModel, cs);
	        _linkBindingBox.setTheme("listboxbindings");
	        _linkBindingBox.getChild(0).setVisible(false); // WARN: Scrollbar is assumed child 0	        
	        _linkBindingBox.addCallback(new CallbackWithReason<CallbackReason>() {
	        	
				@Override
				public void callback(CallbackReason arg0) {
					System.out.println(arg0.toString());
					int sel = _linkBindingBox.getSelected();
					if (sel != -1) {
						LinkBindingEntry lbe = _linkBindingModel.getEntry(sel);
						cs.selectCosm(lbe._cnId);
					}
				}	        	
	        });
	        add(_linkBindingBox);
	        
	        _cosmTargetWidget = new TargetWidget();
	        _cosmTargetWidget.setVisible(false);
	        _cosmTargetWidget.setPulse(true);
	        add(_cosmTargetWidget);	        
		}
		
		@Override
		protected void layout() {
			_linkBindingBox.setSize(getWidth()-20, getHeight() - 240);
			_linkBindingBox.setPosition(10, 10);
			
			_cosmWindow.setPosition(_subWindowRect.getX(), getHeight() - _subWindowRect.getY() - _subWindowRect.getHeight());
			_cosmWindow.setSize(_subWindowRect.getWidth(), _subWindowRect.getHeight());
			
			_cosmReadout.setPosition(_subWindowRect.getX()+_subWindowRect.getWidth()+5, getHeight() - _subWindowRect.getY() - _subWindowRect.getHeight());
		}
		
		public SimpleChangableListModel<LinkBindingEntry> getLinkBindingModel() {
			return _linkBindingModel;
		}

		public ReadoutWidget getCosmReadout() {
			return _cosmReadout;
		}

	}
	
	public static class HoverListBox<T> extends ListBox<T> {

		private ConcursionState _concursionState;

		public HoverListBox(ListModel<T> model, ConcursionState cs) {
			super(model);
			_concursionState = cs;
		}

		@Override
		protected ListBoxDisplay createDisplay() {
			return new HoverListBoxLabel();
		}
		
		protected class HoverListBoxLabel extends ListBoxLabel {

			private LinkBindingEntry _linkBindingEntry;

			@Override
			protected boolean handleEvent(Event evt) {
				if (_linkBindingEntry != null) {
					// If hoverentry then inform state that it needs to highlight the given cosm
					if (evt.getType() == Event.Type.MOUSE_ENTERED)
						_concursionState.setHoverCosm(_linkBindingEntry._cnId);
						// Log.info("Entered "+_linkBindingEntry.toString());
					// If hoverexit then inform state that it can unhighlight the given cosm
					if (evt.getType() == Event.Type.MOUSE_EXITED)
						_concursionState.matchAndClearHoverCosm(_linkBindingEntry._cnId);
						// Log.info("Exited "+_linkBindingEntry.toString());
				}
				return super.handleEvent(evt);
			}

			@Override
			public void setData(Object data) {
				_linkBindingEntry = (LinkBindingEntry) data;
				super.setData(data);
			}			
		}
	}
	
	public static class LinkBindingEntry {
		private final long _lbId;
		private final String _lbName;
		private final long _ltId;
		private final long _cnId; 
		private final String _cnName;
		
		private String _label;

		public LinkBindingEntry(long lbId, String lbName, long ltId, long cnId, String cnName) {
			_lbId = lbId;
			_lbName = lbName;
			_ltId = ltId;
			_cnId = cnId;
			_cnName = cnName;
			genLabel();
		}
		
		private void genLabel() {
			_label = _cnName + " : " + _lbName;
		}

		@Override
		public String toString() {
			return _label;
		}
	}

	public void setTimeDelta(long timeDelta) {
		_timeDelta = timeDelta;
	}
}
