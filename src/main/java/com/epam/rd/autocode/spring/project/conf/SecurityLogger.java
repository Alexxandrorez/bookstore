package com.epam.rd.autocode.spring.project.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityLogger {

    @EventListener
    public void handleSuccess(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        log.info("SECURITY EVENT | Success login | User: {}", email);
    }

    @EventListener
    public void handleFailure(AbstractAuthenticationFailureEvent event) {
        String email = event.getAuthentication().getName();
        String reason = event.getException().getMessage();
        log.warn("SECURITY EVENT | Failed login | User: {} | Reason: {}", email, reason);
    }
}