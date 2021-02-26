package fr.bks.pokerPlanning.service;

import fr.bks.pokerPlanning.bean.User;
import fr.bks.pokerPlanning.websocket.WebSocketPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SecurityService {

    private static Logger LOG = LoggerFactory.getLogger(SecurityService.class);

    private final ThreadLocal<WebSocketPrincipal> principalHolder = new ThreadLocal<>();

    // Map websocket Id / User
    private final Map<String, User> wsIdToUser = new ConcurrentHashMap<>();

    public void registerPrincipal(WebSocketPrincipal principal) {
        principalHolder.set(principal);
    }

    public void unRegisterPrincipal(WebSocketPrincipal principal) {
        principalHolder.remove();
    }

    public WebSocketPrincipal getPrincipal() {
        return principalHolder.get();
    }

    public String getWsId() {
        return getPrincipal().getWsId();
    }

    public User getUser() {
        return wsIdToUser.get(getWsId());
    }

    public void registerUser(User user) {
        String wsId = getWsId();
        wsIdToUser.put(wsId, user);
        user.connectBy(wsId);
    }

    public void unregisterUser(User user) {
        if (user != null) {
            String wsId = getWsId();
            user.disconnectBy(wsId);
            wsIdToUser.remove(wsId);
        }
    }

    public SecurityService checkNotAnonymous() {
        if (getPrincipal().getName() == null) {
            throw new SecurityException("Must not be anonymous");
        }
        return this;
    }

    public SecurityService checkBelongToPlanning(UUID planningId) {
        User user = getUser();

        if (user == null) {
            throw new SecurityException("User must register before acting");
        }

        if (!planningId.equals(user.getSession().getPlanningUuid())) {
            throw new SecurityException("Planing id error");
        }

        return this;
    }

    public SecurityService checkIfAdmin() {
        User user = getUser();
        if (!user.getSession().getAdminList().contains(user.getName())) {
            throw new SecurityException("Must be admin of this planning");
        }
        return this;
    }

}
