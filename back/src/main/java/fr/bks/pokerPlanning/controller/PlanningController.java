package fr.bks.pokerPlanning.controller;

import fr.bks.pokerPlanning.bean.PlanningOutputMessage;
import fr.bks.pokerPlanning.bean.PlanningSession;
import fr.bks.pokerPlanning.bean.PlanningVoteMessage;
import fr.bks.pokerPlanning.service.PlanningService;
import fr.bks.pokerPlanning.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@RestController
@RequestMapping("/api/")
public class PlanningController {

    private static Logger LOGGER = LoggerFactory.getLogger(PlanningController.class);

    @Autowired
    private PlanningService planningService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("")
    public RedirectView redirectToApp() {
        return new RedirectView("/app", true);
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(Exception ex) {
        LOGGER.error("websocket error", ex);
        return ex.getMessage();
    }

    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") // Autorise le CORS pour le devmode
    @PostMapping("/planning/create")
    public PlanningSession createPlanning(@CookieValue(value = "userId", required = true) String userId,
         @RequestBody final String roomName) {
        return planningService.createSession(userId, roomName);
    }

    /**
     * Le retour d'un subscribe est par défaut transféré directement à la personne ayant souscrit.
     * @param planningUuid
     * @return full planning state
     */
    @SubscribeMapping("/planning/{planningUuid}")
    public PlanningOutputMessage register(@DestinationVariable UUID planningUuid) {
        return planningService.register(planningUuid);
    }

    @MessageMapping("/planning/{planningUuid}/vote")
    public void vote(@DestinationVariable UUID planningUuid, PlanningVoteMessage inputMessage) {
        planningService.vote(planningUuid, inputMessage.getValue());
    }

    @MessageMapping("/planning/{planningUuid}/newStory")
    public void newStory(@DestinationVariable UUID planningUuid, String label) {
        planningService.newStory(planningUuid, label);
    }

    @MessageMapping("/planning/{planningUuid}/reveal")
    public void reveal(@DestinationVariable UUID planningUuid) {
        planningService.reveal(planningUuid, true);
    }

    @MessageMapping("/planning/{planningUuid}/promote-user")
    public void promoteUser(@DestinationVariable UUID planningUuid, String userIdToPromote) {
        planningService.promoteUser(planningUuid, userIdToPromote);
    }

    @MessageMapping("/planning/{planningUuid}/demote-user")
    public void demoteUser(@DestinationVariable UUID planningUuid, String userIdToDemote) {
        planningService.demoteUser(planningUuid, userIdToDemote);
    }

    @MessageMapping("/planning/{planningUuid}/{userId}/set-spectator")
    public void setSpectator(@DestinationVariable UUID planningUuid, @DestinationVariable String userId, boolean isSpectator) {
        planningService.setSpectator(planningUuid, userId, isSpectator);
    }


}
