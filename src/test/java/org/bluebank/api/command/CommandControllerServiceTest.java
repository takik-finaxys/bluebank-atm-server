package org.bluebank.api.command;

import org.bluebank.api.command.processor.CommandController;
import org.bluebank.api.command.service.CommandControllerService;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CommandControllerServiceTest {

    private CommandControllerService commandControllerService;
    private CommandController commandController;

    @Before
    public void before() {
        commandController = mock(CommandController.class);
        commandControllerService = new CommandControllerService(commandController);
    }

    @Test
    public void should_start_command_controller_service() {
        // when
        commandControllerService.startAsync().awaitRunning();

        // then
        verify(commandController, atLeastOnce()).run();
    }

}