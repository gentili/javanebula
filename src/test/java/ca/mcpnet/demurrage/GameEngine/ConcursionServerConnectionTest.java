package ca.mcpnet.demurrage.GameEngine;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.channel.ChannelFuture;

import ca.mcpnet.demurrage.GameClient.ConcursionServerConnectionProcessor;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.BindingList;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.Concursion;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.ConcursionServerCallbacks;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.CosmNode;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.CosmNode.CosmState;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.GameClientCallbacks;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.GameClientConnectionProcessor;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.GameClientMessageDecoder.GameClientMessageTypes;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.GameClientSession;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.LinkBinding;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.LinkTerminus;
import ca.mcpnet.demurrage.GameEngine.ConcursionProtocol.TimeFunction;

public class ConcursionServerConnectionTest extends TestCase {
	Logger log = Logger.getLogger("ConcursionServerConnectionTest");
	
	public ConcursionServerConnectionTest(String name) {
		super(name);
	}
	
	public static Test suite() {
		return new TestSuite(ConcursionServerConnectionTest.class);
	}
	
	public static void main(String[] args) {
		Properties logprops = new Properties();
		logprops.setProperty("log4j.rootLogger", "DEBUG, A1");
		logprops.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		logprops.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		logprops.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%-8t] %-5p %c.%M %x - %m%n");
		PropertyConfigurator.configure(logprops);

		String suitename = new Throwable().getStackTrace()[0].getClassName().substring(new Throwable().getStackTrace()[0].getClassName().lastIndexOf('.')+1);
		System.out.println ("Running " + suitename + " Standalone");
		new ConcursionServerConnectionTest("testAllMessages").testAllMessages();
		System.out.println ("Terminating " + suitename + " Standalone");

	}
	
	public void testAllMessages() {
		log.info("TEST - Start Server");
		testGameClientCallbacks gameClientCallbacks = new testGameClientCallbacks();
		GameClientConnectionProcessor server = new GameClientConnectionProcessor(gameClientCallbacks);
		server.startLocal("ConcursionServer");
		
		log.info("TEST - Start Client");
		testConcursionServerCallbacks concursionServerCallbacks = new testConcursionServerCallbacks();
		ConcursionServerConnectionProcessor client = new ConcursionServerConnectionProcessor(concursionServerCallbacks);
		client.blockingConnectLocal("ConcursionServer");
		assertTrue(client.isConnected());
		
		// AUTH
		
		log.info("TEST - concursionSubscriptionRequest -> unauthorizedRequest");
		assertNull(concursionServerCallbacks._unauthorizedRequest);
		client.sendConcursionSubscriptionRequest();
		assertNotNull(concursionServerCallbacks._unauthorizedRequest);
		
		log.info("TEST - loginRequest -> loginFailed");
		assertFalse(gameClientCallbacks._loginRequest);
		client.sendLoginRequest("baduser","badpassword");
		assertNotNull(concursionServerCallbacks._loginFailed);

		log.info("TEST - loginRequest -> loginSuccess");
		client.sendLoginRequest("testuser","testpassword");
		assertTrue(gameClientCallbacks._loginRequest);
		assertEquals(1,concursionServerCallbacks._loginSuccess);
		
		log.info("TEST - serverTimeRequest -> serverTime");
		assertFalse(gameClientCallbacks._serverTimeRequest);
		client.sendServerTimeRequest();
		assertTrue(gameClientCallbacks._serverTimeRequest);
		assertEquals(987654321,concursionServerCallbacks._serverTime);
		
		// CONCURSION 
		
		log.info("TEST - concursionSubscriptionRequest -> concursion");
		client.sendConcursionSubscriptionRequest();
		assertTrue(gameClientCallbacks._concursionSubscriptionRequest);
		assertEquals(125, concursionServerCallbacks._concursion.getId());
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		// COSMNODE
		
		log.info("TEST - addCosmNode");
		CosmNode cn140 = gameClientCallbacks._concursion.addCosmNode(new CosmNode(140l, 1, 0, new TimeFunction.Fixed(1.2f)));
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendAddCosmNode(cn140);
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));

		log.info("TEST - setCosmNodeZ");
		gameClientCallbacks._concursion.getCosmNode(cn140.getId()).setZ(new TimeFunction.Fixed(4));
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendSetCosmNodeZ(cn140.getId(), new TimeFunction.Fixed(4));
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		log.info("TEST - setCosmState");
		gameClientCallbacks._concursion.getCosmNode(cn140.getId()).setState(CosmNode.CosmState.connected);
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendSetCosmState(cn140.getId(), CosmNode.CosmState.connected);
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		log.info("TEST - setCosmName");
		gameClientCallbacks._concursion.getCosmNode(cn140.getId()).setName("TestName");
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendSetCosmName(cn140.getId(),"TestName");
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		
		log.info("TEST - removeCosmNode "+ gameClientCallbacks._cn150.getId());
		assertNotNull(gameClientCallbacks._concursion.removeCosmNode(gameClientCallbacks._cn150.getId()));
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendRemoveCosmNode(gameClientCallbacks._cn150.getId());
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		// TERMINUS
		
		log.info("TEST - addLinkTerminus");
		LinkTerminus tn330 = gameClientCallbacks._concursion.addLinkTerminus(new LinkTerminus(330l, cn140, GameUser.Test));
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendAddLinkTerminus(tn330);
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		log.info("TEST - removeLinkTerminus");
		assertNotNull(gameClientCallbacks._concursion.removeLinkTerminus(tn330.getId()));
		assertFalse(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		gameClientCallbacks._gcsession.sendRemoveLinkTerminus(tn330.getId());
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));

		// BINDING
		
		log.info("TEST - bindingListSubscriptionRequest -> bindingList");
		assertFalse(gameClientCallbacks._bindingList.equivalent(concursionServerCallbacks._bindingList));
		client.sendBindingListSubscriptionRequest();
		assertTrue(gameClientCallbacks._bindingListSubscriptionRequest);
		assertTrue(gameClientCallbacks._bindingList.equivalent(concursionServerCallbacks._bindingList));
		
		log.info("TEST - bindLinkTerminusRequest -> bindLinkTerminus -> addLinkBinding");
		assertFalse(gameClientCallbacks._bindLinkTerminusRequest);
		client.sendBindLinkTerminusRequest(gameClientCallbacks._lt320.getId());
		assertTrue(gameClientCallbacks._bindLinkTerminusRequest);
		assertTrue(gameClientCallbacks._bindingList.equivalent(concursionServerCallbacks._bindingList));
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));
		
		log.info("TEST - setLinkBindingName");
		gameClientCallbacks._lb420.setName("StarGate 420");
		assertFalse(gameClientCallbacks._bindingList.equivalent(concursionServerCallbacks._bindingList));
		gameClientCallbacks._gcsession.sendSetLinkBindingName(gameClientCallbacks._lb420.getId(),"StarGate 420");
		assertTrue(gameClientCallbacks._bindingList.equivalent(concursionServerCallbacks._bindingList));
		
		log.info("TEST - linkLinkTerminiRequest -> linkLinkTermini");
		client.sendLinkLinkTerminiRequest(gameClientCallbacks._lb410.getId(),gameClientCallbacks._lb420.getId());
		
		// gameClientCallbacks._concursion.dump();
		// gameClientCallbacks._bindingList.dump();

		log.info("TEST - Stop Server");
		server.blockingStop();
		assertTrue(concursionServerCallbacks._disconnectEvent);
		assertTrue(gameClientCallbacks._concursion.equivalent(concursionServerCallbacks._concursion));		
	}

	static class testGameClientCallbacks implements GameClientCallbacks {

		Logger log = Logger.getLogger("testGameClientCallbacks");
		
		Concursion _concursion;
		BindingList _bindingList;
		GameClientSession _gcsession;

		CosmNode _cn110;
		CosmNode _cn120;
		CosmNode _cn150;

		LinkTerminus _lt310;
		LinkTerminus _lt320;

		LinkBinding _lb410;
		LinkBinding _lb420;
		
		// 1XX are CosmNodes
		// 2XX are reserved
		// 3XX are linkTermini
		// 4XX are linkBindings

		public testGameClientCallbacks() {
			 _concursion = new Concursion(125);
			 _cn110 = _concursion.addCosmNode(new CosmNode(110l, 0, 0, new TimeFunction.Fixed(2.3f)));
			 _cn110.setState(CosmState.connected);
			 _cn120 = _concursion.addCosmNode(new CosmNode(120l, 1, 1, new TimeFunction.Linear(3.4f, 0)));
			 _cn120.setState(CosmState.connected);

			 _bindingList = new BindingList();
			 _lt310 = _concursion.addLinkTerminus(new LinkTerminus(310l, _cn110, GameUser.Test));
			 _lt310.bind();
			 _lb410 = _bindingList.addLinkBinding(new LinkBinding(410l, "StarGate 310", _lt310));
			 _lt320 = _concursion.addLinkTerminus(new LinkTerminus(320l, _cn120, GameUser.Test));

			 _cn150 = _concursion.addCosmNode(new CosmNode(150l, 2, 2, new TimeFunction.Fixed(3.14f)));
		}
		
		// LOGINREQUEST
		
		public boolean _loginRequest = false;
		
		@Override
		public void loginRequest(GameClientSession session, String username, String password) {
			if ((!username.equals("testuser")) || (!password.equals("testpassword"))) {
				session.sendLoginFailed("Invalid username or password");
				return;
			}
			session.sendLoginSuccess(GameUser.Test);
			// Mark login as complete
			_loginRequest = true;
			// Save the session
			_gcsession = session;
		}

		// SERVERTIMEREQUEST
		
		public boolean _serverTimeRequest = false;
		
		@Override
		public void serverTimeRequest(GameClientSession session) {
			_serverTimeRequest = true;
			session.sendServerTime(987654321);
		}
		
		// CONCURSIONSUBSCRIPTIONREQUEST

		public boolean _concursionSubscriptionRequest = false;
		
		@Override
		public void concursionSubscriptionRequest(GameClientSession session) {
			// Send the concursion back to the client
			_concursionSubscriptionRequest = true;
			session.sendConcursion(_concursion);
		}

		// BINDINGLISTSUBSCRIPTIONREQUEST
		
		public boolean _bindingListSubscriptionRequest = false;

		@Override
		public void bindingListSubscriptionRequest(GameClientSession session) {
			// Send the bindingList back to the client
			_bindingListSubscriptionRequest = true;
			session.sendBindingList(_bindingList);
		}

		// BINDLINKTERMINUSREQUEST
		
		public boolean _bindLinkTerminusRequest = false;
		
		@Override
		public void bindLinkTerminusRequest(GameClientSession session, long ltId) {
			_bindLinkTerminusRequest = true;
			// Bind the link terminus
			LinkTerminus lt = _concursion.getLinkTerminus(ltId);
			lt.bind();
			// send the bindLinkTerminus message
			session.sendBindLinkTerminus(ltId);
			
			// Add the linkBinding
			_lb420 = _bindingList.addLinkBinding(new LinkBinding(420l, "StarGate", lt));
			// send the addLinkBinding message
			session.sendAddLinkBinding(_lb420);
		}

		@Override
		public void linkLinkTerminiRequest(GameClientSession session,
				long linkBinding1, long linkBinding2) {
			// Look up the two linkBindings
			LinkBinding lb1 = _bindingList.getLinkBinding(linkBinding1);
			LinkBinding lb2 = _bindingList.getLinkBinding(linkBinding2);
			// Link the two
			_concursion.linkLinkTermini(lb1.getLinkTerminusId(), lb2.getLinkTerminusId());
			// Send LinkLinkTermini message back to client
			session.sendLinkLinkTermini(lb1.getLinkTerminusId(), lb2.getLinkTerminusId());
		}
	}
	
	static class testConcursionServerCallbacks implements ConcursionServerCallbacks {
		
		Logger log = Logger.getLogger("testConcursionServerCallbacks");
		
		public boolean _disconnectEvent = false;

		@Override
		public void disconnectEvent(ChannelFuture future) {
			_disconnectEvent = true;
		}

		@Override
		public void connectEvent(ChannelFuture future) {
			throw new RuntimeException("Unexpected connectEvent!");
		}
		@Override
		public void connectFailureEvent(ChannelFuture future) {
			throw new RuntimeException("Unexpected connectFailureEvent!");
		}

		public GameClientMessageTypes _unauthorizedRequest;
		@Override
		public void unauthorizedRequest(
				GameClientMessageTypes gcMessageType) {
			_unauthorizedRequest = gcMessageType;
		}

		public String _loginFailed;
		@Override
		public void loginFailed(String reason) {
			_loginFailed = reason;
		}
		
		public long _loginSuccess = 0;
		@Override
		public void loginSuccess(long uid) {
			_loginSuccess = uid;
		}

		public long _serverTime = -1;
		@Override
		public void serverTime(long serverTime) {
			_serverTime = serverTime;
		}

		// Concursion Stuff
		
		public Concursion _concursion;
		@Override
		public void concursion(Concursion concursion) {
			_concursion = concursion;
		}
		
		// Cosm Stuff
		
		@Override
		public void addNewCosmNode(CosmNode cosmNode) {
			_concursion.addCosmNode(cosmNode);
		}
		@Override
		public void removeCosmNode(long cnId) {
			_concursion.removeCosmNode(cnId);
		}
		@Override
		public void setCosmNodeZ(long cnId, TimeFunction tf) {
			_concursion.getCosmNode(cnId).setZ(tf);
		}
		@Override
		public void setCosmState(long cnId, CosmState state) {
			_concursion.getCosmNode(cnId).setState(state);
		}
		@Override
		public void setCosmName(long cnId, String name) {
			_concursion.getCosmNode(cnId).setName(name);
		}
		
		// LinkTerminus Stuff
		
		@Override
		public void addLinkTerminus(LinkTerminus linkTerminus) {
			_concursion.addLinkTerminus(linkTerminus);
		}
		@Override
		public void removeLinkTerminus(long ltId) {
			_concursion.removeLinkTerminus(ltId);
		}
		@Override
		public void bindLinkTerminus(long ltId) {
			_concursion.getLinkTerminus(ltId).bind();
		}
		@Override
		public void linkLinkTermini(long ltId1, long ltId2) {
			_concursion.linkLinkTermini(ltId1, ltId2);
		}
		
		// Binding List stuff
		
		public BindingList _bindingList;
		@Override
		public void bindingList(BindingList bindingList) {
			_bindingList = bindingList;
		}
		@Override
		public void addLinkBinding(LinkBinding linkBinding) {
			_bindingList.addLinkBinding(linkBinding);
		}

		@Override
		public void setLinkBindingName(long lbId, String name) {
			_bindingList.getLinkBinding(lbId).setName(name);
		}

	}
}
