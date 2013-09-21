package ca.mcpnet.demurrage.GameEngine.GameClient;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;

import ca.mcpnet.demurrage.GameEngine.ConcursionServer.CosmNode;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.CosmNode.CosmState;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.GameClientMessageDecoder.GameClientMessageTypes;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.ConcursionServerCallbacks;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.LinkBinding;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.LinkTerminus;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.TimeFunction;

public class ConcursionServerMessageProcessor implements ConcursionServerCallbacks {
	Logger _log = Logger.getLogger("ConcursionServerMessageProcessor");

	private GameClient _gameClient;

	public ConcursionServerMessageProcessor(GameClient gameClient) {
		_gameClient = gameClient;
	}

	// DISCONNECT EVENT
	@Override
	public void disconnectEvent(ChannelFuture future) {
		_gameClient.addGameClientTask(new DisconnectEvent());
	}
	
	private static class DisconnectEvent implements GameClientTask {

		@Override
		public void execute(GameClient gc) {
			gc._mainMenuState.appendToLogPane("Disconnected from Concursion Server!\n");
			// RETURN: Disconnect everyone
			// Return to the main menu state
			gc.changeState(gc._mainMenuState);
			// Fire the transient message window up
			gc._mainMenuState.gotoPopup("Disconnected from Concursion Server!");			
		}
	}

	// UNAUTHORIZED REQUEST
	@Override
	public void unauthorizedRequest(
			GameClientMessageTypes gameClientMessageTypes) {
		throw new RuntimeException("unauthorizedRequest message handler unimplemented!");
	}

	// CONNECT EVENT
	@Override
	public void connectEvent(ChannelFuture future) {
		_gameClient.addGameClientTask(new ConnectEvent());
	}
	
	private static class ConnectEvent implements GameClientTask {

		@Override
		public void execute(GameClient gc) {
			gc._mainMenuState.appendToLogPane("Success!\n");
			String username = gc._mainMenuState.getUsername();
			String password = gc._mainMenuState.getPassword();
			gc._mainMenuState.appendToLogPane("Logging in user "+ username + "... ");
			gc._concursionServerConnectionProcessor.sendLoginRequest(username, password);			
		}
	}
	
	// CONNECT FAILURE EVENT
	@Override
	public void connectFailureEvent(ChannelFuture future) {
		_gameClient.addGameClientTask(new ConnectFailureEvent(future.getCause().getMessage()));
	}
	
	private static class ConnectFailureEvent implements GameClientTask {

		private String _reason;

		public ConnectFailureEvent(String reason) {
			_reason = reason;
		}

		@Override
		public void execute(GameClient gc) {
			gc._mainMenuState.appendToLogPane("Failure: "+_reason+"\n");
			gc._mainMenuState.gotoPopup("Failed to connect : "+_reason);
		}
		
	}

	// LOGIN FAILED
	@Override
	public void loginFailed(String reason) {
		_gameClient.addGameClientTask(new LoginFailed(reason));
	}
	
	private static class LoginFailed implements GameClientTask {
		
		private String _reason;

		private LoginFailed(String reason) {
			_reason = reason;
		}

		@Override
		public void execute(GameClient gc) {
			gc._mainMenuState.appendToLogPane("Failure: "+_reason+"\n");
			gc._mainMenuState.gotoPopup("Login Failed : "+_reason);			
		}
		
	}

	// LOGIN SUCCESS
	@Override
	public void loginSuccess(long uid) {
		_gameClient.addGameClientTask(new LoginSuccess());
	}
	
	private static class LoginSuccess implements GameClientTask {

		@Override
		public void execute(GameClient gc) {
			gc._mainMenuState.appendToLogPane("Success!\n");
			gc._mainMenuState.appendToLogPane("Downloading Concursion... ");
			gc._concursionServerConnectionProcessor.sendServerTimeRequest();
			gc._concursionServerConnectionProcessor.sendConcursionSubscriptionRequest();
		}		
	}
	
	// SERVERTIME
	@Override
	public void serverTime(long serverTime) {
		long delta = serverTime - System.currentTimeMillis();
		_log.info("Server Client Time Differential : "+ delta);
		// If server time is ahead of local time
		// Then differential is positive and must be added
		// to localtime to get servertime
	
		// If server time is behind local time
		// Then differential is negative and must be added
		// to localtime to get servertime
		_gameClient.addGameClientTask(new ServerTime(delta));
	}
	
	private static class ServerTime implements GameClientTask {

		private long _delta;

		public ServerTime(long delta) {
			_delta = delta;
		}

		@Override
		public void execute(GameClient gc) {
			gc._concursionState.setTimeDelta(_delta);
		}
	}

	// CONCURSION
	@Override
	public void concursion(
			ca.mcpnet.demurrage.GameEngine.ConcursionServer.Concursion concursion) {
		_gameClient.addGameClientTask(new Concursion(concursion));
	}
	
	private static class Concursion implements GameClientTask {

		private ca.mcpnet.demurrage.GameEngine.ConcursionServer.Concursion _concursion;

		public Concursion(
				ca.mcpnet.demurrage.GameEngine.ConcursionServer.Concursion concursion) {
			_concursion = concursion;
		}

		@Override
		public void execute(GameClient gc) {
			gc.initConcursion(_concursion);
			gc._mainMenuState.appendToLogPane("Success!\n");
			gc._mainMenuState.appendToLogPane("Downloading Bindings... ");
			gc._concursionServerConnectionProcessor.sendBindingListSubscriptionRequest();
		}		
	}

	// ADD NEW COSM NODE
	@Override
	public void addNewCosmNode(CosmNode cosmNode) {
		_gameClient.addGameClientTask(new AddNewCosmNode(cosmNode));
	}
	
	private static class AddNewCosmNode implements GameClientTask {

		private CosmNode _cosmNode;

		public AddNewCosmNode(CosmNode cosmNode) {
			_cosmNode = cosmNode;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().addCosmNode(_cosmNode);
		}
		
	}

	// REMOVE COSM NODE
	@Override
	public void removeCosmNode(long cnId) {
		_gameClient.addGameClientTask(new RemoveCosmNode(cnId));
	}
	
	private static class RemoveCosmNode implements GameClientTask {

		private long _cnId;

		public RemoveCosmNode(long cnId) {
			_cnId = cnId;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().removeCosmNode(_cnId);
		}		
	}
	
	// SET COSM NODE Z
	@Override
	public void setCosmNodeZ(long cnId, TimeFunction tf) {
		_gameClient.addGameClientTask(new SetCosmNodeZ(cnId, tf));
	}
	
	private static class SetCosmNodeZ implements GameClientTask {

		private long _cnId;
		private TimeFunction _tf;

		public SetCosmNodeZ(long cnId, TimeFunction tf) {
			_cnId = cnId;
			_tf = tf;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().getCosmNode(_cnId).setZ(_tf);
		}
	}
	
	// SET COSM STATE
	@Override
	public void setCosmState(long cnId, CosmState state) {
		_gameClient.addGameClientTask(new SetCosmState(cnId, state));
	}
	
	private static class SetCosmState implements GameClientTask {
		
		private long _cnId;
		private CosmState _state;
		
		public SetCosmState(long cnId, CosmState state) {
			_cnId = cnId;
			_state = state;
		}
		
		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().getCosmNode(_cnId).setState(_state);
		}
		
	}

	// SET COSM NAME
	@Override
	public void setCosmName(long cnId, String name) {
		_gameClient.addGameClientTask(new SetCosmName(cnId, name));
	}
	
	private static class SetCosmName implements GameClientTask {
		
		private long _cnId;
		private String _name;
		
		public SetCosmName(long cnId, String name) {
			_cnId = cnId;
			_name = name;
		}
		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().getCosmNode(_cnId).setName(_name);
			gc._concursionState.setCosmName(_cnId,_name);
		}
		
		
	}

	// ADD LINK TERMINUS
	@Override
	public void addLinkTerminus(LinkTerminus linkTerminus) {
		_gameClient.addGameClientTask(new AddLinkTerminus(linkTerminus));
	}
	
	private static class AddLinkTerminus implements GameClientTask {
		
		private LinkTerminus _linkTerminus;

		public AddLinkTerminus(LinkTerminus linkTerminus) {
			_linkTerminus = linkTerminus;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().addLinkTerminus(_linkTerminus);
		}
		
	}
	
	// REMOVE LINK TERMINUS
	
	@Override
	public void removeLinkTerminus(long ltId) {
		_gameClient.addGameClientTask(new RemoveLinkTerminus(ltId));
	}
	
	private static class RemoveLinkTerminus implements GameClientTask {

		private long _ltId;

		public RemoveLinkTerminus(long ltId) {
			_ltId = ltId;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().removeLinkTerminus(_ltId);
		}
		
	}

	// BIND LINK TERMINUS
	
	@Override
	public void bindLinkTerminus(long ltId) {
		_gameClient.addGameClientTask(new BindLinkTerminus(ltId));
	}
	
	private static class BindLinkTerminus implements GameClientTask {

		private long _ltId;

		public BindLinkTerminus(long ltId) {
			_ltId = ltId;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getConcursion().getLinkTerminus(_ltId).bind();
		}
		
	}

	// LINK LINK TERMINI
	
	@Override
	public void linkLinkTermini(long linkTerminusId1, long linkTerminusId2) {
		throw new RuntimeException("linkLinkTermini message handler unimplemented!");
	}

	// BINDING LIST
	
	@Override
	public void bindingList(
			ca.mcpnet.demurrage.GameEngine.ConcursionServer.BindingList bindingList) {
		_gameClient.addGameClientTask(new BindingList(bindingList));
	}
	
	private static class BindingList implements GameClientTask {

		private ca.mcpnet.demurrage.GameEngine.ConcursionServer.BindingList _bindingList;

		public BindingList(
				ca.mcpnet.demurrage.GameEngine.ConcursionServer.BindingList bindingList) {
			_bindingList = bindingList;
		}

		@Override
		public void execute(GameClient gc) {
			gc.initBindingList(_bindingList);
			gc._mainMenuState.appendToLogPane("Success!\n");
			gc.changeState(gc._concursionState);
		}
		
	}

	// ADD LINK BINDING
	
	@Override
	public void addLinkBinding(LinkBinding linkBinding) {
		_gameClient.addGameClientTask(new AddLinkBinding(linkBinding));
	}
	
	private static class AddLinkBinding implements GameClientTask {

		private LinkBinding _linkBinding;

		public AddLinkBinding(LinkBinding linkBinding) {
			_linkBinding = linkBinding;
		}

		@Override
		public void execute(GameClient gc) {
			gc.getBindingList().addLinkBinding(_linkBinding);
			gc._concursionState.addLinkBinding(_linkBinding);
		}
		
	}

	/*
	 * UNIMPLEMENTED HANDLERS
	 */
	
	@Override
	public void setLinkBindingName(long lbId, String name) {
		throw new RuntimeException("setLinkBindingName message handler unimplemented!");
	}

}
