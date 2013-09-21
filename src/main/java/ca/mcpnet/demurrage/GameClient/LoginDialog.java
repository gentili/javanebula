package ca.mcpnet.demurrage.GameClient;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.EditField.Callback;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Label;

public class LoginDialog extends DialogLayout {
	private EditField _efServer;
	private EditField _efUsername;
	private EditField _efPassword;
	
	public Button _btnLogin;
	public Button _btnCancel;
	
	public LoginDialog() {
		_efServer = new EditField();
		_efServer.setText("127.0.0.1");
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
		_efUsername.setText("testuser");
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
		_efPassword.setText("testpassword");
		_efPassword.addCallback(new Callback() {
			@Override
			public void callback(int key) {
				if (key == Event.KEY_RETURN) {
					_btnLogin.requestKeyboardFocus();
				}
			}
		});
		Label lPassword = new Label("Password");
		lPassword.setLabelFor(_efPassword);
		
		_btnLogin = new Button("Login");
		_btnCancel = new Button("Cancel");
		
        DialogLayout.Group hLabels = createParallelGroup(lServer, lName, lPassword);
        DialogLayout.Group hFields = createParallelGroup(_efServer, _efUsername, _efPassword);
        DialogLayout.Group hBtn = createSequentialGroup()
                .addGap()   // right align the button by using a variable gap
                .addWidget(_btnLogin)
                .addWidget(_btnCancel)
                .addGap();
        
        setHorizontalGroup(createParallelGroup()
                .addGroup(createSequentialGroup(hLabels, hFields))
                .addGroup(hBtn));
        setVerticalGroup(createSequentialGroup()
        		.addGroup(createParallelGroup(lServer, _efServer))
                .addGroup(createParallelGroup(lName, _efUsername))
                .addGroup(createParallelGroup(lPassword, _efPassword))
                .addGroup(createParallelGroup(_btnLogin,_btnCancel)));
	}
	
	public void setLoginCallback(Runnable loginCallback) {
		_btnLogin.addCallback(loginCallback);
	}
	
	public void setCancelCallback(Runnable cancelCallback) {
		_btnCancel.addCallback(cancelCallback);
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
