package ca.mcpnet.demurrage.GameClient;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import ca.mcpnet.demurrage.Common.ConcursionProtocol.BindingList;
import ca.mcpnet.demurrage.Common.ConcursionProtocol.Concursion;
import ca.mcpnet.demurrage.Common.ConcursionProtocol.ConcursionServerConnectionProcessor;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

/**
 * 
 * @author gentili
 *
 */
public class GameClient {
	static public String VERSION;
	Logger _log = Logger.getLogger("GameClient");
	
	public static void main(String[] args) {
		Properties logprops = new Properties();
		logprops.setProperty("log4j.rootLogger", "INFO, A1");
		logprops.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		logprops.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		logprops.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%-20t] %-5p %c.%M %x - %m%n");
		PropertyConfigurator.configure(logprops);
		
		Logger log = Logger.getLogger("GameClient");
		log.info("Starting GameClient");
		log.debug("java.library.path="+System.getProperty("java.library.path"));
		try {
			new GameClient().run();

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() != null) {
				System.out.println("Caused by:");
				e.getCause().printStackTrace();
			}
			System.exit(1);
		}
		log.info("Main Thread Exiting");
	}    
	
	private Widget _emptyRootWidget;
	private GUI _gui;
	private LWJGLRenderer _renderer;
	private ThemeManager _theme;
	
	private ShaderProgramManager _shaderProgramManager;

	public final SplashState _splashState;
	public final MainMenuState _mainMenuState;
	public final ConcursionState _concursionState;
	private ClientState _curState;
	private boolean _terminate;
	
	private ConcursionServerMessageProcessor _concursionServerMessageProcessor;
	public ConcursionServerConnectionProcessor _concursionServerConnectionProcessor;
	private Concursion _concursion;
	private BindingList _bindingList;

	private ConcurrentLinkedQueue<GameClientTask> _gameClientTaskQueue;

	public GameClient() throws LWJGLException, IOException {
		VERSION = GameClient.class.getPackage().getImplementationVersion();
		if (VERSION == null) {
			VERSION = "DEV-SNAPSHOT";
		}

		_log.info("Initializing GameClient "+VERSION);
		// Display Init
		// Display.setDisplayMode(new DisplayMode(1280,1024));
		Display.setDisplayMode(Display.getDesktopDisplayMode());
		Display.setFullscreen(true);
		Display.setTitle("Demurrage GameClient "+VERSION);
		Display.setVSyncEnabled(true);
		
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAttribs = new ContextAttribs(3,2);
		contextAttribs = contextAttribs.withProfileCompatibility(true);
		Display.create(pixelFormat, contextAttribs);
		
		_log.info("Java version:   " + System.getProperty("java.runtime.version"));
		_log.info("LWJGL version:  " + org.lwjgl.Sys.getVersion());
		_log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

        // Shader Init
		_shaderProgramManager = new ShaderProgramManager();

        // TWL Menu Init
		_renderer = new LWJGLRenderer();
        _emptyRootWidget = new Widget();
        _emptyRootWidget.setTheme("");
        _gui = new GUI(_emptyRootWidget, _renderer);
        _theme = ThemeManager.createThemeManager(
                TestNetworkClient.class.getResource("/theme.xml"), _renderer);
        _gui.applyTheme(_theme);
        
        _splashState = new SplashState(this);
        _mainMenuState = new MainMenuState(this);
        _concursionState = new ConcursionState(this);
        _terminate = false;
        
        // ConcursionServer stuff
        _concursionServerMessageProcessor = new ConcursionServerMessageProcessor(this);
        _concursionServerConnectionProcessor = new ConcursionServerConnectionProcessor(_concursionServerMessageProcessor);
        
        _gameClientTaskQueue = new ConcurrentLinkedQueue<GameClientTask>();
	}
	
	public void run() {
		// changeState(_mainMenuState);
		changeState(_mainMenuState);
        while(!Display.isCloseRequested() && !_terminate) {
        	// Do a synchronous task
        	GameClientTask task = _gameClientTaskQueue.poll();
        	if (task != null) {
        		task.execute(this);
        	}
        	// Do the graphics stuff
            _curState.update();
            _gui.update();
            Display.update();
            // TestUtils.reduceInputLag();
        }
        _concursionServerConnectionProcessor.blockingStop();
        _gui.destroy();
        _theme.destroy();
        Display.destroy();		
	}
	
	public void addGameClientTask(GameClientTask gct) {
		_gameClientTaskQueue.offer(gct);
	}

	public void changeState(ClientState state) {
		if (_curState == state) {
			return;
		}
		if (_curState != null) { 
			_curState.onExitState();
		}
		_curState = state;
		state.onEnterState();
	}
	
	public void terminate() {
		_terminate = true;
	}
	
	public GUI getGUI() {
		return _gui;
	}
	
	public ThemeManager getTheme() {
		return _theme;
	}

	public ShaderProgramManager getShaderProgramManager() {
		return _shaderProgramManager;
	}
	
	public void initConcursion(Concursion concursion) {
		// This will throw away the old concursion
		_concursion = concursion;
	}
	
	public Concursion getConcursion() {
		return _concursion;
	}

	public void initBindingList(BindingList bindingList) {
		// This will throw away the old binding list
		_bindingList = bindingList;
	}
	
	public BindingList getBindingList() {
		return _bindingList;
	}

}
