package ca.mcpnet.demurrage.GameClient;


import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

public abstract class SimpleDemo extends Application {

    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";
                                                                         
    protected Node rootNode = new Node("Root Node");
    protected Node guiNode = new Node("Gui Node");
    protected float secondCounter = 0.0f;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;
    protected ChaseCamera chaseCam;
    protected boolean showSettings = true;
    private  boolean showFps = true;
    private AppActionListener actionListener = new AppActionListener();

    private class AppActionListener implements ActionListener {

        public void onAction(String name, boolean value, float tpf) {
            if (!value) {
                return;
            }

            if (name.equals(INPUT_MAPPING_EXIT)) {
                stop();
            } else if (name.equals(INPUT_MAPPING_CAMERA_POS)) {
                if (cam != null) {
                    Vector3f loc = cam.getLocation();
                    Quaternion rot = cam.getRotation();
                    System.out.println("Camera Position: ("
                            + loc.x + ", " + loc.y + ", " + loc.z + ")");
                    System.out.println("Camera Rotation: " + rot);
                    System.out.println("Camera Direction: " + cam.getDirection());
                    System.out.println("Chase Distance: " + chaseCam.getDistanceToTarget());
                    System.out.println("Chase Horiz: " + chaseCam.getHorizontalRotation());
                    System.out.println("Chase Vert: " + chaseCam.getVerticalRotation());
                }
            } else if (name.equals(INPUT_MAPPING_MEMORY)) {
                BufferUtils.printCurrentDirectMemory(null);
            }
        }
    }

    public SimpleDemo() {
        super();
    }

    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }

    /**
     * Retrieves guiNode
     * @return guiNode Node object
     *
     */
    public Node getGuiNode() {
        return guiNode;
    }

    /**
     * Retrieves rootNode
     * @return rootNode Node object
     *
     */
    public Node getRootNode() {
        return rootNode;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    /**
     * Toggles settings window to display at start-up
     * @param showSettings Sets true/false
     *
     */
    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }

    /**
     * Attaches FPS statistics to guiNode and displays it on the screen.
     *
     */
    public void loadFPSText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        fpsText = new BitmapText(guiFont, false);
        fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        fpsText.setText("Frames per second");
        guiNode.attachChild(fpsText);
    }

    /**
     * Attaches Statistics View to guiNode and displays it on the screen
     * above FPS statistics line.
     *
     */
    public void loadStatsView() {
        statsView = new StatsView("Statistics View", assetManager, renderer.getStatistics());
//         move it up so it appears above fps text
        statsView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        guiNode.attachChild(statsView);
    }

    @Override
    public void initialize() {
        super.initialize();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        loadFPSText();
        loadStatsView();
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);

        if (inputManager != null) {
            chaseCam = new ChaseCamera(cam, rootNode);
            
            chaseCam.setMaxVerticalRotation((float) (FastMath.PI/2-1E-3));
            chaseCam.setMinVerticalRotation((float) (-FastMath.PI/2+1E-3));
            // chaseCam.setRotationSensitivity(150.0f);
            chaseCam.registerWithInput(inputManager);

            if (context.getType() == Type.Display) {
                inputManager.addMapping(INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
            }

            inputManager.addMapping(INPUT_MAPPING_CAMERA_POS, new KeyTrigger(KeyInput.KEY_C));
            inputManager.addMapping(INPUT_MAPPING_MEMORY, new KeyTrigger(KeyInput.KEY_M));
            inputManager.addListener(actionListener, INPUT_MAPPING_EXIT,
                    INPUT_MAPPING_CAMERA_POS, INPUT_MAPPING_MEMORY);
        }

        // call user code
        simpleInitApp();
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        if (showFps) {
            secondCounter += timer.getTimePerFrame();
            int fps = (int) timer.getFrameRate();
            if (secondCounter >= 1.0f) {
                fpsText.setText("Frames per second: " + fps);
                secondCounter = 0.0f;
            }          
        }

        // update states
        stateManager.update(tpf);

        // simple update and root node
        simpleUpdate(tpf);
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        if (context.isRenderable()){
            renderManager.render(tpf);
        }
        simpleRender(renderManager);
        stateManager.postRender();
    }

    public void setDisplayFps(boolean show) {
        showFps = show;
        fpsText.setCullHint(show ? CullHint.Never : CullHint.Always);
    }

    public void setDisplayStatView(boolean show) {
        statsView.setEnabled(show);
        statsView.setCullHint(show ? CullHint.Never : CullHint.Always);
    }

    public abstract void simpleInitApp();

    public void simpleUpdate(float tpf) {
    }

    public void simpleRender(RenderManager rm) {
    }

}
