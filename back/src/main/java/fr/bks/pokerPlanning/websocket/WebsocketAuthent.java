package fr.bks.pokerPlanning.websocket;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;

public class WebsocketAuthent extends AbstractAuthenticationToken {

    private static final GrantedAuthority USER_AUTHORITY = () -> "USER";

    private final Object principal;

    public WebsocketAuthent(final WebSocketPrincipal principal) {
        super(Collections.singleton(USER_AUTHORITY));
        this.principal = principal;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
