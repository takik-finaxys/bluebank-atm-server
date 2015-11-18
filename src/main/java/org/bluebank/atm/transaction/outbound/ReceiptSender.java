package org.bluebank.atm.transaction.outbound;

import org.bluebank.api.endpoint.OutboundEndPoint;
import org.bluebank.resource.AtmResource;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.bluebank.contract.Messages.Message;
import static org.bluebank.contract.Messages.Message.MessageType.RECEIPT;
import static org.bluebank.contract.Messages.Receipt;

@Singleton
public class ReceiptSender implements OutboundEndPoint<Receipt> {
    private final AtmResource serverEndpoint;

    @Inject
    public ReceiptSender(AtmResource serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }

    @Override
    public void send(Receipt receipt) {
        Message message = Message.newBuilder()
                .setType(RECEIPT)
                .setReceipt(receipt)
                .build();

        serverEndpoint.send(message);
    }
}
