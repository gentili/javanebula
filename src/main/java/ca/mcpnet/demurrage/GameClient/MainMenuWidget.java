package ca.mcpnet.demurrage.GameClient;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Event;

public class MainMenuWidget extends DialogLayout {
	Button[] _menuButtons;
		
	public MainMenuWidget() {
		
        _menuButtons = new Button[3];
        for(int i=0 ; i<_menuButtons.length ; i++) {
            _menuButtons[i] = new Button();
        }
        _menuButtons[0].setText("Connect");
        _menuButtons[1].setText("Settings");
        _menuButtons[2].setText("Exit");	        
    	
        Group menuButtonsH = createSequentialGroup()
//            .addGap()
            .addGroup(createParallelGroup(_menuButtons));
//            .addGap();
        Group menuButtonsV = createSequentialGroup()
//            .addGap()
            .addWidgets(_menuButtons);
//            .addGap();

        setHorizontalGroup(createParallelGroup(menuButtonsH));
        setVerticalGroup(createParallelGroup(menuButtonsV));		
	}

	public void setConnectCallback(Runnable connectCallback) {
    	_menuButtons[0].addCallback(connectCallback);	            
	}
	public void setSettingsCallback(Runnable settingsCallback) {
    	_menuButtons[1].addCallback(settingsCallback);	            
	}
	public void setExitCallback(Runnable exitCallback) {
    	_menuButtons[2].addCallback(exitCallback);	            
	}
	@Override
	protected boolean handleEvent(Event evt) {
		
		return super.handleEvent(evt);
	}

	public void setLoginFocus() {
		_menuButtons[0].requestKeyboardFocus();
	}	
}