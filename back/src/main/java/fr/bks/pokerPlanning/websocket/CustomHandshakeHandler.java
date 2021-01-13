package fr.bks.pokerPlanning.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpUtils;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
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
        List<String> cookieHeaders = request.getHeaders().get("cookie");
        if(!cookieHeaders.isEmpty()){
            try{
                String cookieString = URLDecoder.decode(cookieHeaders.get(0), StandardCharsets.UTF_8.name());
                String[] cookieStrings = cookieString.split(";");
                Map<String, HttpCookie> cookieJar = new HashMap<>();
                for(String s : cookieStrings){
                    List<HttpCookie> cookie = HttpCookie.parse(s);
                    cookieJar.put(cookie.get(0).getName(), cookie.get(0));
                }
                WebSocketPrincipal principal = new WebSocketPrincipal(cookieJar.get("userId").getValue());
                principal.setDisplayName(cookieJar.get("username").getValue());
                return principal;
            } catch(UnsupportedEncodingException ex){
                throw new RuntimeException(ex);
            }
            // TODO : rejeter la requête si les cookies n'ont pas été envoyés. Et supprimer la connexion par défaut ci-dessous.
        }
        return new WebSocketPrincipal(UUID.randomUUID().toString()); // todo: ne plus se baser sur un id généré à la co mais plutot par un id métier généré côté UI et envoyé au register ?
        // todo suite : en l'absence d'authent c'est sans doute le plus simple pour prendre en charge le fait de se reco "en tant que le même user"
    }
}
