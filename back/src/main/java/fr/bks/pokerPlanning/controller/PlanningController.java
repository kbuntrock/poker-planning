package fr.bks.pokerPlanning.controller;

import fr.bks.pokerPlanning.bean.PlanningRegisterMessage;
import fr.bks.pokerPlanning.bean.PlanningSession;
import fr.bks.pokerPlanning.bean.PlanningVoteMessage;
import fr.bks.pokerPlanning.service.PlanningService;
import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PlanningController {

    @Autowired
    private PlanningService planningService;

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(Exception ex) {
        return ex.getMessage();
    }

    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") // Autorise le CORS pour le devmode
    @GetMapping("/planning/create")
    public PlanningSession createPlanning(@CookieValue(value = "userId", required = true) String userId,
                                          @CookieValue(value = "username", required = true) String username) {
        return planningService.createSession(userId, username);
    }


    @MessageMapping("/planning/{planningUuid}/register")
    public void register(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal, PlanningRegisterMessage inputMessage) {
        planningService.register(planningUuid, principal, inputMessage.getName());
    }

    // todo changer de nom, on repasse par register ou méthode dédiée ?

    @MessageMapping("/planning/{planningUuid}/vote")
    public void vote(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal, PlanningVoteMessage inputMessage) {
        planningService.vote(planningUuid, principal, inputMessage.getValue());
    }

    // @MessageMapping("/planning/{planningUuid}/admin/{planningAdminKey}/newStory")
    // public void newStory(@DestinationVariable UUID planningUuid, @DestinationVariable UUID planningAdminKey, WebSocketPrincipal principal, String label) {
    @MessageMapping("/planning/{planningUuid}/newStory")
    public void newStory(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal, String label) {
        planningService.newStory(planningUuid, label);
    }

    // @MessageMapping("/planning/{planningUuid}/admin/{planningAdminKey}/reveal")
    // public void reveal(@DestinationVariable UUID planningUuid, @DestinationVariable UUID planningAdminKey, WebSocketPrincipal principal) {
    @MessageMapping("/planning/{planningUuid}/reveal")
    public void reveal(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal) {
        planningService.reveal(planningUuid);
    }


      /*
    @MessageMapping("/planning/{planningUuid}")
    public void planningMessageHandler(Message message, PlanningInputMessage businessMessage, @DestinationVariable Long planningUuid) throws Exception {

        messagingTemplate.convertAndSend("/topic/greetings", new Greeting("Message a tout le monde"));
        messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/greetings", new Greeting("Message a tous les " + principal.getName()));

        String sessionId = message.getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/greetings", new Greeting("Message juste a l'envoyeur."), headerAccessor.getMessageHeaders());


        sendToSession(message, "/topic/planning/" + planningUuid, new PlanningOutputMessage("Message juste a l'envoyeur."));

        messagingTemplate.convertAndSend("/topic/planning/" + planningUuid, new PlanningOutputMessage("Test"));
    }
*/
/*
    private void sendToSession(Message inputMessage, String topic, PlanningOutputMessage message) {
        String sessionId = inputMessage.getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        Principal principal = inputMessage.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(principal.getName(), topic, message, headerAccessor.getMessageHeaders());
    }
*/

}
