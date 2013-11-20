package ca.mcpnet.demurrage.GameClient;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.EditField.Callback;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Label;

public class SettingsDialog extends DialogLayout {
	private EditField _efServer;
	private EditField _efUsername;
	private EditField _efPassword;
	
	public Button _btnSaveSettings;
	
	public SettingsDialog(GameClientSettings gcs) {
		_efServer = new EditField();
		_efServer.setText(gcs.getServerAddr());
		_efServer.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN) {
                    _efUsername.requestKeyboardFocus();
                }
            }
        });
		Label lServer = new Label("Server");
		lServer.setLabelFor(_efServer);
		
		_efUsername = new EditField();
		_efUsername.setText(gcs.getUser());
		_efUsername.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN) {
                    _efPassword.requestKeyboardFocus();
                }
            }
        });
		Label lName = new Label("Name");
		lName.setLabelFor(_efUsername);
		
		_efPassword = new EditField();
		_efPassword.setPasswordMasking(true);
		_efPassword.setText(gcs.getPassword());
		_efPassword.addCallback(new Callback() {
			@Override
			public void callback(int key) {
				if (key == Event.KEY_RETURN) {
					_btnSaveSettings.requestKeyboardFocus();
				}
			}
		});
		Label lPassword = new Label("Password");
		lPassword.setLabelFor(_efPassword);
		
		_btnSaveSettings = new Button("Save Settings");
		
        DialogLayout.Group hLabels = createParallelGroup(lServer, lName, lPassword);
        DialogLayout.Group hFields = createParallelGroup(_efServer, _efUsername, _efPassword);
        DialogLayout.Group hBtn = createSequentialGroup()
                .addGap()   // right align the button by using a variable gap
                .addWidget(_btnSaveSettings)
                // Here's where another button goes
                .addGap();
        
        setHorizontalGroup(createParallelGroup()
                .addGroup(createSequentialGroup(hLabels, hFields))
                .addGroup(hBtn));
        setVerticalGroup(createSequentialGroup()
        		.addGroup(createParallelGroup(lServer, _efServer))
                .addGroup(createParallelGroup(lName, _efUsername))
                .addGroup(createParallelGroup(lPassword, _efPassword))
                .addGroup(createParallelGroup(_btnSaveSettings)));
	}
	
	public void setSaveSettingsCallback(Runnable saveSettingsCallback) {
		_btnSaveSettings.addCallback(saveSettingsCallback);
	}
	
	public String getServer() {
		return _efServer.getText();
	}
	public String getUsername() {
		return _efUsername.getText();
	}
	public String getPassword() {
		return _efPassword.getText();
	}
}
