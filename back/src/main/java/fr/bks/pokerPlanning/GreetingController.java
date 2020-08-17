package fr.bks.pokerPlanning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class GreetingController {

	@Autowired
	public SimpMessageSendingOperations messagingTemplate;

	@MessageMapping("/hello")
	@SendToUser("/topic/greetings")
	public Greeting greeting(Message message, Principal principal, HelloMessage businessMessage) throws Exception {
		messagingTemplate.convertAndSend( "/topic/greetings", new Greeting( "Message a tout le monde" ) );
		messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/greetings", new Greeting( "Message a tous les " + principal.getName() ) );

		String sessionId = message.getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
				.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setLeaveMutable(true);

		messagingTemplate.convertAndSendToUser(principal.getName(), "/topic/greetings", new Greeting( "Message juste a l'envoyeur." ), headerAccessor.getMessageHeaders());


		//Thread.sleep(1000); // simulated delay
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(businessMessage.getName()) + "!");
	}

}
