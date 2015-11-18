package org.bluebank.atm.authorization.outbound;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.bluebank.api.command.processor.CommandProcessor;
import org.bluebank.api.endpoint.OutboundEndPoint;
import org.bluebank.atm.Atm;
import org.bluebank.atm.authorization.command.HandleCardValidationResponse;
import org.bluebank.atm.authorization.command.factory.HandleCardValidationResponseFactory;
import org.bluebank.atm.authorization.model.Card;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.bluebank.atm.Transaction.CardValidationRequested;
import static org.bluebank.contract.Messages.CardReadConfirmation;
import static org.bluebank.contract.Messages.Message;
import static org.bluebank.contract.Messages.Message.MessageType.CARD_READ_CONFIRMATION;

@Singleton
public class CardValidationRequestedHandler {
    private final OutboundEndPoint<Message> cardValidationRequestSender;
    private final Atm atm;
    private final HandleCardValidationResponseFactory handleCardValidationResponseFactory;
    private final CommandProcessor commandProcessor;

    @Inject
    public CardValidationRequestedHandler(EventBus eventBus,
                                          @Named("cardValidationRequestSender") OutboundEndPoint<Message> cardValidationRequestSender,
                                          Atm atm,
                                          HandleCardValidationResponseFactory handleCardValidationResponseFactory,
                                          CommandProcessor commandProcessor) {
        this.cardValidationRequestSender = cardValidationRequestSender;
        this.atm = atm;
        this.handleCardValidationResponseFactory = handleCardValidationResponseFactory;
        this.commandProcessor = commandProcessor;
        eventBus.register(this);
    }

    @Subscribe
    public void requestCardValidation(CardValidationRequested cardValidationRequested) {
        Card card = atm.readCard(cardValidationRequested.accountNumber);

        final Message message = Message.newBuilder()
                .setType(CARD_READ_CONFIRMATION)
                .setCardReadConfirmation(CardReadConfirmation.newBuilder())
                .build();
        cardValidationRequestSender.send(message);

        HandleCardValidationResponse handleCardValidationResponse = handleCardValidationResponseFactory.build(
                cardValidationRequested.id,
                card
        );
        commandProcessor.process(handleCardValidationResponse);
    }
}
