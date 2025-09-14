package com.nhnacademy.handler;

import com.nhnacademy.session.SessionManager;
import com.nhnacademy.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Optional;

public class LogoutApiHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public LogoutApiHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Optional<String> sessionId = sessionManager.getSessionId(exchange);
        sessionId.ifPresent(sessionManager::removeSession);
        String cookie = "session=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0";
        exchange.getResponseHeaders().set("Set-Cookie", cookie);
        HttpUtils.sendResponse(exchange, 200, "{\"status\":\"success\"}", "application/json");
    }
}
