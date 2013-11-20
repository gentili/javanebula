package ca.mcpnet.demurrage.GameClient;

public class GameClientSettings {

	private String _user = "";
	private String _password = "";
	private String _serverAddr = "";
	private String _serverPort = "";

	public void setUser(String user) {
		if (user == null)
			return;
		_user = user;
	}
	public String getUser() {
		return _user;
	}

	public void setPassword(String password) {
		if (password == null)
			return;
		_password = password;
	}
	public String getPassword() {
		return _password;
	}

	public void setServerAddr(String serverAddr) {
		if (serverAddr == null)
			return;
		_serverAddr = serverAddr;
	}
	public String getServerAddr() {
		return _serverAddr;
	}

	public void setServerPort(String serverPort) {
		if (serverPort == null)
			return;
		_serverPort = serverPort;
	}
	public String getServerPort() {
		return _serverPort;
	}



}
