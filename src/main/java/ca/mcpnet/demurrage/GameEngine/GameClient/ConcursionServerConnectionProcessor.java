package ca.mcpnet.demurrage.GameEngine.GameClient;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import ca.mcpnet.demurrage.GameEngine.LengthFieldConnectionClient;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.ConcursionServerCallbacks;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.ConcursionServerMessageDecoder;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.GameClientConnectionProcessor;
import ca.mcpnet.demurrage.GameEngine.ConcursionServer.GameClientMessageDecoder.GameClientMessageTypes;
import ca.mcpnet.demurrage.GameEngine.Utils.StringCodec;

public class ConcursionServerConnectionProcessor extends LengthFieldConnectionClient {
	
	ConcursionServerCallbacks _concursionServerCallbacks;
	ConcursionServerMessageDecoder _concursionServerMessageDecoder;
	
	public ConcursionServerConnectionProcessor(ConcursionServerCallbacks csc) {
		super(Logger.getLogger("ConcursionServerConnectionProcessor"),GameClientConnectionProcessor.MAX_MESSAGE_SIZE);
		_concursionServerCallbacks = csc;
		_concursionServerMessageDecoder = new ConcursionServerMessageDecoder(csc);		
	}

	@Override
	public OneToOneEncoder getEncoder() {
		// Encoder is in this class
		return null;
	}

	@Override
	public OneToOneDecoder getDecoder() {
		return _concursionServerMessageDecoder;
	}

	// Connect related stuff
	
	@Override
	public void disconnectEvent(ChannelFuture future) {
		_log.info("Disconnect "+future.getChannel().getRemoteAddress().toString());
		_concursionServerCallbacks.disconnectEvent(future);
	}

	@Override
	public void connectEvent(ChannelFuture future) {
		_concursionServerCallbacks.connectEvent(future);
	}

	@Override
	public void connectFailureEvent(ChannelFuture future) {
		_concursionServerCallbacks.connectFailureEvent(future);
	}


	/**
	 * Send a LoginRequest message to the ConcursionServer 
	 */
	public void sendLoginRequest(String username, String password) {
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		// Write the message type
		cb.writeByte(GameClientMessageTypes.LoginRequest.ordinal());
		// Write the username
		StringCodec.encode(cb, username);
		// Write the password
		StringCodec.encode(cb, password);
		_channel.write(cb);
	}

	/**
	 * Send a ConcursionSubscriptionRequest message to the ConcursionServer
	 */
	public void sendConcursionSubscriptionRequest() {
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		// Write the message type
		cb.writeByte(GameClientMessageTypes.ConcursionSubscriptionRequest.ordinal());
		_channel.write(cb);
	}

	/**
	 * Send a ServerTimeRequest message to the ConcursionServer
	 */
	public void sendServerTimeRequest() {
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		// Write the message type
		cb.writeByte(GameClientMessageTypes.ServerTimeRequest.ordinal());
		_channel.write(cb);
	}
	
	/**
	 * Send a BindingListSubscriptionRequest message to the ConcursionServer
	 */
	public void sendBindingListSubscriptionRequest() {
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		// Write the message type
		cb.writeByte(GameClientMessageTypes.BindingListSubscriptionRequest.ordinal());
		_channel.write(cb);
	}
	
	/**
	 * Send a BindLinkTerminusRequest message to the ConcursionServer
	 * @param id
	 */
	public void sendBindLinkTerminusRequest(long ltId) {
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		// Write the message type
		cb.writeByte(GameClientMessageTypes.BindLinkTerminusRequest.ordinal());
		cb.writeLong(ltId);
		_channel.write(cb);
	}
	
	/**
	 * Send a LinkLinkTerminiRequest message to the ConcursionServer
	 * @param lbId1 id of the first LinkBinding
	 * @param lbId2 id of the second LinkBinding
	 */

	public void sendLinkLinkTerminiRequest(long lbId1, long lbId2) {
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		// Write the message type
		cb.writeByte(GameClientMessageTypes.LinkLinkTerminiRequest.ordinal());
		cb.writeLong(lbId1);
		cb.writeLong(lbId2);
		_channel.write(cb);
	}

}
