package fr.bks.pokerPlanning.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Set anonymous user (Principal) in WebSocket messages by using UUID
 * This is necessary to avoid broadcasting messages but sending them to specific user sessions
 */
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // generate user name by UUID
        return new WebSocketPrincipal(UUID.randomUUID().toString()); // todo: ne plus se baser sur un id généré à la co mais plutot par un id métier généré côté UI et envoyé au register ?
        // todo suite : en l'absence d'authent c'est sans doute le plus simple pour prendre en charge le fait de se reco "en tant que le même user"
    }
}
