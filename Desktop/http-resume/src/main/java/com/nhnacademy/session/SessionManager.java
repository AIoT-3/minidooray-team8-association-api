package com.nhnacademy.session;

import com.sun.net.httpserver.HttpExchange;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, String> sessionStore = new ConcurrentHashMap<>();

    public String createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, userId);
        return sessionId;
    }

    public void removeSession(String sessionId) {
        sessionStore.remove(sessionId);
    }

    public boolean isLoggedIn(HttpExchange exchange) {
        return getSessionId(exchange).map(sessionStore::containsKey).orElse(false);
    }

    public Optional<String> getSessionId(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            return Arrays.stream(cookieHeader.split(";"))
                    .map(String::trim)
                    .filter(s -> s.startsWith("session="))
                    .map(s -> s.substring("session=".length()))
                    .findFirst();
        }
        return Optional.empty();
    }
}