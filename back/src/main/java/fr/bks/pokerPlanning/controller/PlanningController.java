package fr.bks.pokerPlanning.controller;

import fr.bks.pokerPlanning.bean.PlannerSession;
import fr.bks.pokerPlanning.bean.PlanningOutputMessage;
import fr.bks.pokerPlanning.bean.PlanningRegisterMessage;
import fr.bks.pokerPlanning.bean.PlanningVoteMessage;
import fr.bks.pokerPlanning.core.PlannerSessionsHolder;
import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PlanningController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private PlannerSessionsHolder plannerSessionsHolder;

    @GetMapping("/planning/create")
    public PlannerSession createPlanning() {
        return plannerSessionsHolder.createSession();
    }

    private void sendToPlanning(UUID planningUuid, PlanningOutputMessage output) {
        messagingTemplate.convertAndSend("/topic/planning/" + planningUuid.toString(), output);
    }

    @MessageMapping("/planning/{planningUuid}/register")
    public void register(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal, PlanningRegisterMessage inputMessage) {
        // todo vérifier pas déjà enregistré
        principal.setDisplayName(inputMessage.getName());

        PlannerSession session = plannerSessionsHolder.getSession(planningUuid);
        PlanningOutputMessage output = session.register(principal);

        sendToPlanning(planningUuid, output);
    }

    // todo changer de nom, on repasse par register ou méthode dédiée ?

    @MessageMapping("/planning/{planningUuid}/vote")
    public void vote(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal, PlanningVoteMessage inputMessage) {
        PlannerSession session = plannerSessionsHolder.getSession(planningUuid);

        PlanningOutputMessage output = session.vote(principal.getName(), inputMessage.getValue());

        sendToPlanning(planningUuid, output);
    }

    // @MessageMapping("/planning/{planningUuid}/admin/{planningAdminKey}/newStory")
    @MessageMapping("/planning/{planningUuid}/newStory")
    public void newStory(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal, String label) {

    }

    // @MessageMapping("/planning/{planningUuid}/admin/{planningAdminKey}/reveal")
    @MessageMapping("/planning/{planningUuid}/reveal")
    public void reveal(@DestinationVariable UUID planningUuid, WebSocketPrincipal principal) {

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
