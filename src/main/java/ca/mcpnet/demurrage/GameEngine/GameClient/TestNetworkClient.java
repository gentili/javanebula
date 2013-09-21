package ca.mcpnet.demurrage.GameEngine.GameClient;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.channel.ChannelFuture;

import ca.mcpnet.demurrage.GameEngine.ConcursionServer.BindingList;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.Concursion;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.ConcursionServerCallbacks;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.CosmNode;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.CosmNode.CosmState;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.GameClientMessageDecoder.GameClientMessageTypes;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.LinkBinding;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.LinkTerminus;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.TimeFunction;


public class TestNetworkClient implements ConcursionServerCallbacks {
	Logger _log = Logger.getLogger("TestClient");
	Integer _eventCounter = 0;
	Concursion _concursion;

	ConcursionServerConnectionProcessor _CSConnection;
	
	public TestNetworkClient() {
		_CSConnection = new ConcursionServerConnectionProcessor(this);
	}
	
	public void run() {
		try {
			_log.info("Starting TestClient");
			_CSConnection.blockingConnect("localhost", 1234);
			
			_log.info("Sending an Unauthorized Request");
			_CSConnection.sendConcursionSubscriptionRequest();
			waitOnEvent(1);

			_log.info("Sending Bad Login");
			_CSConnection.sendLoginRequest("badusername", "badpassword");
			waitOnEvent(2);
	
			_log.info("Sending Good Login");
			_CSConnection.sendLoginRequest("testuser", "testpassword");
			waitOnEvent(3);
			
			_log.info("Sending Concursion Subscription Request");
			_CSConnection.sendConcursionSubscriptionRequest();
			waitOnEvent(4);
			
			_log.info("Process addCosmNode messages");
			waitOnEvent(10);
		} finally {
			_log.info("Shutting Down");
			_CSConnection.blockingStop();
		}
		_log.info("TestClient Complete");
	}
	
	protected synchronized int waitOnEvent(int eventNum) {
			while (_eventCounter < eventNum) {
				try {
					wait(1000);
				} catch (InterruptedException e) {
					_log.warn("Unexpected Interrupt:"+e.getMessage());
				}
			}
			return _eventCounter;
	}
	
	protected synchronized void incEventCounter() {
			notifyAll();
			_eventCounter++;
	}
	
	/**
	 * Event Handlers
	 */
	
	@Override
	public void unauthorizedRequest(
			GameClientMessageTypes gameClientMessageTypes) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void disconnectEvent(ChannelFuture future) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void loginFailed(String reason) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void loginSuccess(long uid) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void concursion(
			Concursion concursion) {
		_log.info("Received Concursion "+concursion.getId());
		_concursion = concursion;
		incEventCounter();
	}

	@Override
	public void addNewCosmNode(CosmNode cosmNode) {
		_log.info("Received CosmNode "+ cosmNode.getId());
		_concursion.addCosmNode(cosmNode);
		incEventCounter();
	}

	@Override
	public void removeCosmNode(long cnId) {
		_log.info("Received Remove Request for "+cnId);
		_concursion.removeCosmNode(cnId);
		incEventCounter();
	}

	public static void main(String[] args) {
		Properties logprops = new Properties();
		logprops.setProperty("log4j.rootLogger", "DEBUG, A1");
		logprops.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		logprops.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		logprops.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%-20t] %-5p %c.%M %x - %m%n");
		PropertyConfigurator.configure(logprops);

		TestNetworkClient tc = new TestNetworkClient();
		tc.run();
	}

	@Override
	public void connectEvent(ChannelFuture future) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void connectFailureEvent(ChannelFuture future) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void serverTime(long readLong) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void setCosmNodeZ(long readLong, TimeFunction decode) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void setCosmState(long readLong, CosmState cosmState) {
		_log.info("Event Received");
		incEventCounter();
	}
	
	@Override
	public void setCosmName(long readLong, String decode) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void addLinkTerminus(LinkTerminus linkTerminus) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void removeLinkTerminus(long readLong) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void bindingList(BindingList bindingList) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void bindLinkTerminus(long readLong) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void addLinkBinding(LinkBinding linkBinding) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void linkLinkTermini(long linkTerminusId1, long linkTerminusId2) {
		_log.info("Event Received");
		incEventCounter();
	}

	@Override
	public void setLinkBindingName(long lbId, String name) {
		_log.info("Event Received");
		incEventCounter();
	}

}
