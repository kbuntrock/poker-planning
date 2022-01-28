package fr.bks.pokerPlanning.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Documentation utile (outre la doc officielle spring) :
 * https://stackoverflow.com/questions/45405332/websocket-authentication-and-authorization-in-spring
 * @author Kévin Buntrock
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry securityConfiguration) {
        // Les subscribes à des routes dédiées sont permises
        securityConfiguration.simpSubscribeDestMatchers(WebSocketConfig.TOPIC_PLANNING_PREFIX).authenticated();
        securityConfiguration.simpSubscribeDestMatchers("/app/planning/*").authenticated();
        securityConfiguration.simpSubscribeDestMatchers(WebSocketConfig.USER_TOPIC_PLANNING_PREFIX).authenticated();
        securityConfiguration.simpSubscribeDestMatchers(WebSocketConfig.USER_TOPIC_ERROR_PREFIX).authenticated();
		// Les autres subscribes ne sont pas autorisés
        securityConfiguration.simpSubscribeDestMatchers("/**").denyAll();
        // Les seuls messages autorisés sont ceux destinés à l'application :
        securityConfiguration.simpMessageDestMatchers(
                WebSocketConfig.APP_DESTINATION_PREFIX+"/**").authenticated();
        // Aucun envoi autre envoi de message n'est autorisé (pas de communication client to client)
        securityConfiguration.simpMessageDestMatchers("/**").denyAll();
    }

    /**
     * Variable à mon sens assez mal nommée. Si retourne false, elle greffe un intercepteur demandant un token csrf sur les messages de
     * connexion. Chose qui n'est pas souhaitée.
     * <p>
     * En l'occurence, en prod, l'origine n'est effectivement pas la même. Et en test, l'utilisation "same origin" n'est absolument pas
     * bloquée.
     *
     * @return true
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
