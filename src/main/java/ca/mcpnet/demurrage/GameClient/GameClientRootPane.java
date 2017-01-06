package ca.mcpnet.demurrage.GameClient;

import org.lwjgl.opengl.Display;

import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.ScrollPane.Fixed;
import de.matthiasmann.twl.textarea.SimpleTextAreaModel;

public class GameClientRootPane extends Widget {
	
	private MainMenuWidget _mainmenuwidget;
	SettingsDialog _settingsdialog;
	private ScrollPane _logPane;
	private StringBuilder _logBuf;
	private SimpleTextAreaModel _logPaneTextAreaModel;
	private PopupWindow2 _popup;
	int linecount = 0;
	private Widget _clientStateRootPane = null;

	GameClientRootPane(final GameClient gc) {
		setTheme("");
		// Init the logging output pane
		_logPaneTextAreaModel = new SimpleTextAreaModel();
		_logPane = new ScrollPane(new TextArea(_logPaneTextAreaModel));
		_logPane.setTheme("scrollinglogpane");
		_logPane.setFixed(Fixed.HORIZONTAL);
		_logPane.setExpandContentSize(true);
		_logPane.setEnabled(false);
		_logPane.setVisible(true);
		_logBuf = new StringBuilder();
		appendToLogPane("*** DEMURRAGE GAMECLIENT "+GameClient.VERSION+" ***\n\n");
		_logPane.validateLayout();

		add(_logPane);
		// Init the main menu
		_mainmenuwidget = new MainMenuWidget();
		_mainmenuwidget.setExitCallback(
				new Runnable() {
					@Override
					public void run() { 
						gc.terminate();
						}
					}
				);
		_mainmenuwidget.setConnectCallback(new Runnable() {
			@Override
			public void run() {
				// _settingsdialog.setVisible(false);
				String server = _settingsdialog.getServer();
				appendToLogPane("Connecting to "+server+ "... ");
				gc._concursionServerConnectionProcessor.connect(server, 1234);
			}
		});
		
		_mainmenuwidget.setSettingsCallback(new Runnable() {
			@Override
			public void run() {
				gotoSettingsDialog();
			}
		});
		add(_mainmenuwidget);
		
		// Init the login dialog
		_settingsdialog = new SettingsDialog(gc.getGameClientSettings());
		_settingsdialog.setVisible(false);
		_settingsdialog.setSaveSettingsCallback(new Runnable() {
			@Override
			public void run() {
				gotoMainMenu();
			}
		});
		add(_settingsdialog);
		
		_popup = new PopupWindow2(this);
		_popup.setVisible(false);
		_popup.add(new Label("Something Unexpected Happened!"));
		_popup.setRequestCloseCallback(new Runnable() {
			@Override
			public void run() {
				gotoMainMenu();
			}
		});
		/*
		_efServer.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN) {
                    _efUsername.requestKeyboardFocus();
                }
            }
        });
        */
		_popup.getOrCreateActionMap();
	}
	
	@Override
	protected void layout() {
		if (_clientStateRootPane != null)
			layoutChildFullInnerArea(_clientStateRootPane);
		
		_mainmenuwidget.adjustSize();
		_mainmenuwidget.setPosition((getParent().getWidth()-_mainmenuwidget.getWidth())/2, (getParent().getHeight()-_mainmenuwidget.getHeight())/2);

		_settingsdialog.adjustSize();
		_settingsdialog.setPosition((getParent().getWidth()-_settingsdialog.getWidth())/2, (getParent().getHeight()-_settingsdialog.getHeight())/2);
		
		_logPane.setSize(Display.getWidth() - 100, Display.getHeight() - 100);
		_logPane.setPosition((getParent().getWidth()-_logPane.getWidth())/2, (getParent().getHeight()-_logPane.getHeight())/2);
		
		_popup.adjustSize();
		_popup.setPosition((getParent().getWidth()-_popup.getWidth())/2, (getParent().getHeight()-_popup.getHeight())/2);
	}

	void appendToLogPane(String str) {
		_logBuf.append(str);
		_logPaneTextAreaModel.setText(_logBuf.toString());
		_logPane.setScrollPositionY(_logPane.getMaxScrollPosY());
	}
	
	public void setConnectFocus() {
		_mainmenuwidget.setConnectFocus();
	}
	
	public void gotoSettingsDialog() {
		_mainmenuwidget.setVisible(false);
		_settingsdialog.setVisible(true);
		_settingsdialog._btnSaveSettings.requestKeyboardFocus();
		_popup.setVisible(false);
	}
	
	public void gotoMainMenu() {
		_mainmenuwidget.setVisible(true);
		_settingsdialog.setVisible(false);
		_popup.setVisible(false);
		_mainmenuwidget.setConnectFocus();
	}
	
	public void gotoPopup(String message) {
		_mainmenuwidget.setVisible(false);
		_settingsdialog.setVisible(false);
		Label l = (Label) _popup.getChild(0);
		l.setText(message);
		_popup.openPopupCentered();
	}

	public void setClientStateRootPane(Widget clientStateRootPane) {
		removeChild(_clientStateRootPane);
		_clientStateRootPane = clientStateRootPane;
		add(_clientStateRootPane);
	}
}