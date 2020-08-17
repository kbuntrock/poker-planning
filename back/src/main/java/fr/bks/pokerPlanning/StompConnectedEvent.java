package fr.bks.pokerPlanning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class StompConnectedEvent implements ApplicationListener<SessionConnectedEvent> {

    @Autowired
    public SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        String message = event.getUser().getName() + " viens de se connecter.";

        messagingTemplate.convertAndSend("/topic/greetings", new Greeting( message ));

    }
}