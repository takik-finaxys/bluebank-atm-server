package org.bluebank.atm.authorization.outbound;

import org.bluebank.resource.AtmResource;
import org.bluebank.api.endpoint.OutboundEndPoint;
import org.bluebank.contract.Messages.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CardValidationRequestSender implements OutboundEndPoint<Message> {
    private final AtmResource serverEndpoint;

    @Inject
    public CardValidationRequestSender(AtmResource serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }

    @Override
    public void send(Message message) {
        serverEndpoint.send(message);
    }
}
