package org.bluebank.atm.authorization.outbound;

import org.bluebank.api.endpoint.OutboundEndPoint;
import org.bluebank.contract.Messages.CardValidationStatus;
import org.bluebank.resource.AtmResource;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.bluebank.contract.Messages.Message;
import static org.bluebank.contract.Messages.Message.MessageType.CARD_VALIDATION_STATUS;

@Singleton
public class PinValidationResponseSender implements OutboundEndPoint<CardValidationStatus> {
    private final AtmResource serverEndpoint;

    @Inject
    public PinValidationResponseSender(AtmResource serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }

    @Override
    public void send(CardValidationStatus cardValidationStatus) {
        Message message = Message.newBuilder()
                .setType(CARD_VALIDATION_STATUS)
                .setCardValidationStatus(cardValidationStatus)
                .build();

        serverEndpoint.send(message);
    }
}
