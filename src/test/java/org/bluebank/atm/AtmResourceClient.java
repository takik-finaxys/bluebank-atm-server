package org.bluebank.atm;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bluebank.contract.Messages.Message.MessageType;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.bluebank.contract.Messages.Message;

@ClientEndpoint
public class AtmResourceClient {

    public static final ConcurrentMap<MessageType, Message> messages = new ConcurrentHashMap<>();
    private static WebSocketContainer webSocketContainer;

    public static Session connect(URI uri) throws Exception {
        webSocketContainer = ContainerProvider.getWebSocketContainer();
        return webSocketContainer.connectToServer(AtmResourceClient.class, uri);
    }

    public static void close() throws Exception {
        if (webSocketContainer instanceof LifeCycle) {
            ((LifeCycle) webSocketContainer).stop();
        }
    }

    @OnMessage
    public void onMessage(byte[] payload) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(payload);
        messages.put(message.getType(), message);
    }

}
