package fr.bks.pokerPlanning.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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
        List<String> cookieHeaders = request.getHeaders().get("cookie");
        if(!cookieHeaders.isEmpty()){
            try{
                String cookieString = URLDecoder.decode(cookieHeaders.get(0), StandardCharsets.UTF_8.name());
                String[] cookieStrings = cookieString.split(";");
                Map<String, String> cookieJar = new HashMap<>();
                for(String s : cookieStrings){
                    List<HttpCookie> cookie = HttpCookie.parse(s);
                    cookieJar.put(cookie.get(0).getName(), cookie.get(0).getValue());
                }
                String id = cookieJar.get("userId");
                String displayName = cookieJar.get("username");
                String key = cookieJar.get("userKey");

                return new WebSocketPrincipal(id, displayName, key);
            } catch(UnsupportedEncodingException ex){
                throw new RuntimeException(ex);
            }
        }
        throw new SecurityException("Need cookie authent");
    }
}
