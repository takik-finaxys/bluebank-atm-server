package org.bluebank.atm;

import org.bluebank.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.Session;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.UUID;

import static com.jayway.awaitility.Awaitility.await;
import static java.lang.String.format;
import static java.nio.ByteBuffer.wrap;
import static org.bluebank.atm.AtmResourceClient.close;
import static org.bluebank.atm.AtmResourceClient.connect;
import static org.bluebank.atm.AtmResourceClient.messages;
import static org.bluebank.contract.Messages.DepositRequest;
import static org.bluebank.contract.Messages.Message;
import static org.bluebank.contract.Messages.Message.MessageType.CARD_READ_CONFIRMATION;
import static org.bluebank.contract.Messages.Message.MessageType.CARD_VALIDATION_STATUS;
import static org.bluebank.contract.Messages.Message.MessageType.DEPOSIT_REQUEST;
import static org.bluebank.contract.Messages.Message.MessageType.RECEIPT;
import static org.bluebank.contract.Messages.Message.MessageType.VALIDATE_CARD_REQUEST;
import static org.bluebank.contract.Messages.Message.MessageType.VALIDATE_PIN_REQUEST;
import static org.bluebank.contract.Messages.ValidateCardRequest;
import static org.bluebank.contract.Messages.ValidatePinRequest;

public class AtmResourceIT {

    private Application server;
    private Session session;

    @Before
    public void setUp() throws Exception {
        server = new Application();
        int port = findOpenPort();
        server.start(port);
        URI uri = new URI(format("ws://localhost:%s/events", port));
        session = connect(uri);
    }

    @After
    public void shutdown() throws Exception {
        server.stop();
        session.close();
        close();
    }

    @Test
    public void should_make_deposit() throws Exception {

        UUID transactionId = new UUID(0, 1);

        Message message = Message.newBuilder()
                .setType(VALIDATE_CARD_REQUEST)
                .setValidateCardRequest(ValidateCardRequest.newBuilder()
                        .setTransactionId(transactionId.toString())
                        .setCardNumber("5555444433331111"))
                .build();
        session.getBasicRemote().sendBinary(wrap(message.toByteArray()));
        await().until(() -> {
            return messages.containsKey(CARD_READ_CONFIRMATION);
        });

        Message validatePinMessage = Message.newBuilder()
                .setType(VALIDATE_PIN_REQUEST)
                .setValidatePinRequest(ValidatePinRequest.newBuilder()
                        .setTransactionId(transactionId.toString())
                        .setPin("1234"))
                .build();
        session.getBasicRemote().sendBinary(wrap(validatePinMessage.toByteArray()));
        await().until(() -> {
            return messages.containsKey(CARD_VALIDATION_STATUS);
        });

        Message depositMessage = Message.newBuilder()
                .setType(DEPOSIT_REQUEST)
                .setDepositRequest(DepositRequest.newBuilder()
                        .setTransactionId(transactionId.toString())
                        .setAmount(200))
                .build();
        session.getBasicRemote().sendBinary(wrap(depositMessage.toByteArray()));
        await().until(() -> {
            return messages.containsKey(RECEIPT);
        });
    }

    private int findOpenPort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
